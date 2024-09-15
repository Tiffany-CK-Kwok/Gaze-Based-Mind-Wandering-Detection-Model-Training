package mwdetection;

import mwdetection.base.objects.Point;
import mwdetection.eyetracking.objects.Gaze;
import mwdetection.eyetracking.services.fixations.onlineidt.OnlineIDT;
import mwdetection.eyetracking.services.fixations.onlineidt.OnlineIVT;

import java.util.ArrayList;

/**
 * Any kinds of features related to Fixation
 *
 */
public class FixaitonFeatures {

    private ArrayList<Double> fixationsD = new  ArrayList<Double>();
    private ArrayList<Point> fixCenter = new  ArrayList<Point>();
    private ArrayList<Double> saccadeD = new ArrayList<Double>();
    private ArrayList<Double> saccadeDistance = new ArrayList<Double>();

    // THESE are for moving window or ignore gaze guidance [ 2nd layer features ]
    private ArrayList<ArrayList<Double>> fixationsD_collection = new  ArrayList<ArrayList<Double>>();
    private ArrayList<Double> fixationsD_Mean_collection = new  ArrayList<Double>();
    private ArrayList<ArrayList<Point>> fixCenter_collection = new  ArrayList<ArrayList<Point>>();
    private ArrayList<ArrayList<Double>> saccadeD_collection = new ArrayList<ArrayList<Double>>();
    private ArrayList<ArrayList<Double>> saccadeDistance_collection = new ArrayList<ArrayList<Double>>();

    ArrayList<Boolean> isFixation = new ArrayList<Boolean>();

    private OnlineIDT onlineIDT;
    private OnlineIVT onlineIVT;

    private double segmentLength =0;

    private boolean useIVT = false;

    /**
     * This is a constructor for using just simply 1 gaze array list , which will not calculate TTOI and Focal K
     *
     * @param inputObj
     */
    public FixaitonFeatures(ArrayList<?> inputObj, boolean useIVT){
        this.useIVT = useIVT;
        ArrayList<Gaze> gazeObj = (ArrayList<Gaze>) inputObj;
        if(!useIVT) processGazeObj(gazeObj);
        else processGazeObj_IVT(gazeObj);
    }

    private void initIVTObject(){
        onlineIVT = new OnlineIVT(21,20); 
    }

    private void initIDTObject(){
        onlineIDT = new OnlineIDT(21,100); 
    }

    public void processGazeObj_IVT(ArrayList<Gaze> gazeObj){
        segmentLength = 0;
        for(int i =0;i<gazeObj.size();i++) {
            segmentLength += (double) gazeObj.get(i).getTimeDiff();
        }

        initIVTObject();

        // check fixation + Get the saccades duration
        boolean saveSaccadeFlag = false;
        double saccadeDuration = 0;
        Point saccadeStart = null;
        for (int loop = 0; loop< gazeObj.size();loop++){
            isFixation.add(onlineIVT.computeFixation(gazeObj.get(loop)));
            if (!isFixation.get(loop)){
                // add when it is not a fixation
                if(saccadeStart == null){
                    saccadeStart = new Point(gazeObj.get(loop).x,gazeObj.get(loop).y);
                }
                saccadeDuration += gazeObj.get(loop).getTimeDiff();
                if(saveSaccadeFlag){
                    saccadeD.add(saccadeDuration);
                    if(saccadeStart != null) saccadeDistance.add(GeneralFunction.euclidienDistance(saccadeStart.x,saccadeStart.y,gazeObj.get(loop).x,gazeObj.get(loop).y));
                        // save the eudclidian dist of saccade dist
                    else saccadeDistance.add(0.0);
                    saccadeDuration = 0;
                    saccadeStart = null;
                    saveSaccadeFlag = false;
                }
            }

            if (onlineIVT.getFixations().size()>0 && isFixation.get(loop) && !isFixation.get(loop-1) ){
                // The point that changed from not Fixation -> Fixation and at the same time there is a Fixation before [as saccade is between 2 fixation ]
                saveSaccadeFlag = true;

            }
        }

        onlineIVT.saveLastFixation();

        // Get the fixations duration and fixations centroids
        for (int loop = 0; loop< onlineIVT.getFixations().size();loop++){
            fixationsD.add((double) onlineIVT.getFixations().get(loop).getDuration());
            fixCenter.add(onlineIVT.getFixations().get(loop).getCentroid());

        }

        fixationsD_collection.add((ArrayList)fixationsD.clone());
        if(fixationsD.size() > 0){
            fixationsD_Mean_collection.add(GeneralFunction.arrayMean(fixationsD));
        }else{
            fixationsD_Mean_collection.add(0.0);
        }
        fixCenter_collection.add((ArrayList) fixCenter.clone());
        saccadeD_collection.add((ArrayList) saccadeD.clone());
        saccadeDistance_collection.add((ArrayList)saccadeDistance.clone());
    }


    public void processGazeObj(ArrayList<Gaze> gazeObj){
        segmentLength = 0;
        for(int i =0;i<gazeObj.size();i++) {
            segmentLength += (double) gazeObj.get(i).getTimeDiff();
        }

        initIDTObject(); 

        // check fixation + Get the saccades duration
        boolean saveSaccadeFlag = false;
        double saccadeDuration = 0;
        Point saccadeStart = null;
        for (int loop = 0; loop< gazeObj.size();loop++){
            isFixation.add(onlineIDT.computeFixation(gazeObj.get(loop)));
            if (!isFixation.get(loop)){
                // add when it is not a fixation
                if(saccadeStart == null){
                    saccadeStart = new Point(gazeObj.get(loop).x,gazeObj.get(loop).y);
                }
                saccadeDuration += gazeObj.get(loop).getTimeDiff();
                if(saveSaccadeFlag){
                    saccadeD.add(saccadeDuration);
                    if(saccadeStart != null) saccadeDistance.add(GeneralFunction.euclidienDistance(saccadeStart.x,saccadeStart.y,gazeObj.get(loop).x,gazeObj.get(loop).y));
                        // save the eudclidian dist of saccade dist
                    else saccadeDistance.add(0.0);
                    saccadeDuration = 0;
                    saccadeStart = null;
                    saveSaccadeFlag = false;
                }
            }

            if (onlineIDT.getFixations().size()>0 && isFixation.get(loop) && !isFixation.get(loop-1) ){
                // The point that changed from not Fixation -> Fixation and at the same time there is a Fixation before [as saccade is between 2 fixation ]
                saveSaccadeFlag = true;

            }
        }

        // Get the fixations duration and fixations centroids
        for (int loop = 0; loop< onlineIDT.getFixations().size();loop++){
            fixationsD.add((double) onlineIDT.getFixations().get(loop).getDuration());
            fixCenter.add(onlineIDT.getFixations().get(loop).getCentroid());

        }

        fixationsD_collection.add((ArrayList)fixationsD.clone());
        if(fixationsD.size() > 0){
            fixationsD_Mean_collection.add(GeneralFunction.arrayMean(fixationsD));
        }else{
            fixationsD_Mean_collection.add(0.0);
        }
        fixCenter_collection.add((ArrayList) fixCenter.clone());
        saccadeD_collection.add((ArrayList) saccadeD.clone());
        saccadeDistance_collection.add((ArrayList)saccadeDistance.clone());
    }


    /**
     * This function is for returning the header of statistic Feature
     * @return String ArrayList
     */
    public ArrayList<String> statisticFeaturesHeader(){
        ArrayList<String> output = new ArrayList<String>();

        output.add("Mean_FixDuration");
        output.add("Stdv_FixDuration");
        output.add("Percentage_FixDuration");
        output.add("Min_FixDuration");
        output.add("Max_FixDuration");
        output.add("Median_FixDuration");
        output.add("Skewness_FixDuration");
        output.add("Kurtosis_FixDuration");
        output.add("FixPerSecond");
        output.add("Ratio_Fix_to_Sac");
        output.add("Radii_minimal_circle");

        return output;
    }

    /**
     * This is a function for getting statistical features for fixations
     * @return ArrayList [ Mean, Stdv, Percentage, Min, Max ]
     */
    public ArrayList<Double> statisticFeatures(){
        ArrayList<Double> output = new ArrayList<Double>();

        if (fixationsD.size()>0){
            // There are fixations in side the List
            output.add(GeneralFunction.arrayMean(fixationsD)); // Get Mean
            output.add(GeneralFunction.arrayStdv(fixationsD)); // Get STDV
            output.add(GeneralFunction.arraySum(fixationsD)/segmentLength); // Get Percentage , need to write code
            output.add(GeneralFunction.arrayMin(fixationsD)); // Get min
            output.add(GeneralFunction.arrayMax(fixationsD)); // Get max
            output.add(GeneralFunction.arrayMedian(fixationsD)); // Get Median
            output.add(GeneralFunction.arraySkewness(fixationsD)); // Get Skew
            output.add(GeneralFunction.arrayKurtosis(fixationsD)); // Get Kurtosis
            output.add(fixationsD.size()/(segmentLength/1000)); // fixation per sec
            output.add(GeneralFunction.arraySum(fixationsD)/(segmentLength-GeneralFunction.arraySum(fixationsD))); // ratio fix to sac

            output.add(getRadiiOfMinCircle());//radii of minimal circle

        }else{
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
        }

        return output;
    }

    private double getRadiiOfMinCircle(){
        ArrayList<mwdetection.Point> list = new ArrayList<>();
        for (Point p : fixCenter){
            list.add(new mwdetection.Point(p.x,p.y));
        }
        Circle c = SmallestEnclosingCircle.makeCircle(list);
        return c.r;
    }

    /**
     * Getter of fixation duration list [ in ms ]
     * @return ArrayList<Double> fixation durations
     */
    public ArrayList<Double> getFixationsD() {
        return fixationsD;
    }

    public ArrayList<ArrayList<Double>> getSaccadeD_collection() {
        return saccadeD_collection;
    }

    public ArrayList<ArrayList<Double>> getSaccadeDistance_collection() {
        return saccadeDistance_collection;
    }

    /**
     * Getter of saccade duration list [ in ms ]
     * Saccade duration is basically the duration between two fixations
     * @return ArrayList<Double> saccade durations
     */
    public ArrayList<Double> getSaccadeD() {return saccadeD; }

    public double getSegmentLength() {
        return segmentLength;
    }

    /**
     * Getter of saccade distance list [ in px ]

     * Saccade distance is the straight distance between two fixations centorids
     * @return ArrayList<Double> saccade distance
     */
    public ArrayList<Double> getSaccadeDistance() {
        return saccadeDistance;
    }
}
