package mwdetection;

import mwdetection.base.objects.Point;
import mwdetection.eyetracking.objects.Gaze;

import java.util.ArrayList;

public class GazeSegment {
    private ArrayList<Gaze> gazes;
    private String label;
    private double labelValue = 0.0;
    private int cityid;
    private double segmentDuration = 0;
    private ArrayList<ArrayList<Double>> featuresList;
    private ArrayList<String> headerLine;
    private boolean isHeaderReady = false;

    public GazeSegment(String label, int cityid) {
        this.label = label;
        this.cityid = cityid;

        if(label.equals("1")) {
            labelValue = 1.0;
        }

        gazes = new ArrayList<Gaze>();
        featuresList = new ArrayList<ArrayList<Double>>();
        headerLine = new ArrayList<String>();
    }

    /**
     * This is a function that used for reading 1 csv [ exported with the udp_full function of gaze_matching.exe ] line to a gaze data
     * @param input
     */
    public void addGazeByString(ArrayList<String> input){
        Gaze g = new Gaze();

        // Start to read data
        long timestamp = Double.valueOf(input.get(1)).longValue();
        g.setTimestamp(timestamp);
        g.getGaze_L().setTimestamp(timestamp);
        g.getGaze_R().setTimestamp(timestamp);

        // Timediff : instead using the default one , we use the time difference between of eye tracker timestamp, more accurate
        if(gazes.size() != 0){
            long d = Double.valueOf(input.get(5)).longValue() - gazes.get(gazes.size()-1).getEtTimeStamp();
            segmentDuration += d;
            g.setTimeDiff(d);
        }else{
            g.setTimeDiff(0); // first instance
        }

        g.x = (int) Double.parseDouble(input.get(3));
        g.y = (int) Double.parseDouble(input.get(4));

        g.setEtTimeStamp(Double.valueOf(input.get(5)).longValue());

        g.setGaze_L(new Point((int) Double.parseDouble(input.get(6)),(int) Double.parseDouble(input.get(7))));
        g.setPupilRadius_L(Double.parseDouble(input.get(8)));
        g.setPupilConfidence_L(Double.parseDouble(input.get(9)));

        g.setGaze_R(new Point((int) Double.parseDouble(input.get(10)),(int) Double.parseDouble(input.get(11))));
        g.setPupilRadius_R(Double.parseDouble(input.get(12)));
        g.setPupilConfidence_R(Double.parseDouble(input.get(13)));

        gazes.add(g);
    }

    public void runFeatureGeneration(int focusThreshold, boolean withVergence){
        ArrayList<Gaze> processGaze = (ArrayList<Gaze>) gazes.clone();
        if(label.equals("1")){
            labelValue = 1.0;
            // if MW, cut the gaze data based on the threshold
            double countTime = 0;
            for (int loopBackward = processGaze.size()-1 ; loopBackward >= 0 ; loopBackward --){
                if(countTime < focusThreshold){
                    countTime += processGaze.get(loopBackward).getTimeDiff();
                    processGaze.remove(loopBackward);
                }else{
                    break;
                }
            }
        }

        // loop 1 second jumping window -> Generate data.
        double countMS = 0;
        double windowSize = 1000;
        ArrayList<Gaze> gazeWindow = new ArrayList<Gaze>();
        for(int curIdx = 0; curIdx <= processGaze.size(); curIdx++){ // intentionally make it to <= processGaze.size ()  so that it add the data if it is the last chunk
            // Process if larger then window size
            if(countMS > windowSize || ( curIdx ==  processGaze.size() && gazeWindow.size() != 0 ) ){
                featuresList.add(genFeatures(gazeWindow, withVergence));// all generation are done in this function
                featuresList.get(featuresList.size()-1).add(labelValue);
                gazeWindow.clear();
                countMS = 0;
            }

            if(curIdx !=  processGaze.size() ){
                gazeWindow.add(processGaze.get(curIdx));
                countMS += processGaze.get(curIdx).getTimeDiff();
            }
        }
    }

    private ArrayList<Double> genFeatures(ArrayList<Gaze> inputList,boolean withVergence){
        ArrayList<Double> oneLine = new ArrayList<Double>();
        // Fixation Features
        FixaitonFeatures fixFeature;
        fixFeature = new FixaitonFeatures(inputList,true);

        if (fixFeature.getFixationsD().size() == 0)
            System.out.println("------- CHECK : NO Fixation detected ! ");

        if (!isHeaderReady) {
            headerLine.addAll(fixFeature.statisticFeaturesHeader());
        }

        oneLine.addAll(fixFeature.statisticFeatures());

        // Saccade Features
        SaccadesFeatures sacFeature = new SaccadesFeatures(fixFeature);
        if (!isHeaderReady) {
            headerLine.addAll(sacFeature.lengthFeaturesHeader());
            headerLine.addAll(sacFeature.speedFeatureHeader());
            headerLine.addAll(sacFeature.durationFeatureHeader());
        }
        oneLine.addAll(sacFeature.lengthFeatures());
        oneLine.addAll(sacFeature.speedFeatures());
        oneLine.addAll(sacFeature.durationFeature());

        if(withVergence){
            VergenceFeatures verFeature = new VergenceFeatures(inputList);
            if (!isHeaderReady) {
                headerLine.addAll(verFeature.getVergenceFeatures_Header());
            }

            oneLine.addAll(verFeature.getVergenceFeatures());
        }
        if (!isHeaderReady) {
            headerLine.add("GT");
        }
        isHeaderReady = true;
        return oneLine;
    }

    public boolean hasGazeData(){
        return !(gazes.size()==0);
    }

    // ------------ Getter & Setter --------------------
    public double getSegmentDuration() {
        return segmentDuration;
    }

    public ArrayList<Gaze> getGazes() {
        return gazes;
    }

    public ArrayList<ArrayList<Double>> getFeaturesList() {
        return featuresList;
    }

    public ArrayList<String> getHeaderLine() {
        return headerLine;
    }
}
