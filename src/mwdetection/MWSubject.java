package mwdetection;

import java.io.File;
import java.util.ArrayList;

public class MWSubject {
    /**
     * This is a object that represent 1 subject in the Study 1 of the MW-LAMETTA study
     * Each subjects listened to 4 focus audios and 4 audios that they may mind-wandering
     * <p>
     * This object contain a list of segments of this subject
     * This could make the leave n subject out validation easier
     */

    private int subjectId;
    private int numberOfInstance_focus = 0;
    private int numberOfInstance_mw = 0;
    private int currentSegmentNumber = -1;
    private long valideMWinMS = 0, valideFocusinMS = 0;
    private int validSegmentCounter = 0;
    private ArrayList<GazeSegment> segments;

    private int focusThreshold = 0;

    public MWSubject(String rootFolder, int subjectId, int focusThreshold) {
        this.focusThreshold = focusThreshold;
        this.subjectId = subjectId;
        segments = new ArrayList<GazeSegment>();

        // Read .csv
        File folder = new File(rootFolder + "/s" + subjectId);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                int fileNameLen = fileName.length();
                boolean isFocusData = (Integer.parseInt(fileName.substring(fileNameLen - 5, fileNameLen - 4)) <= 2);
                int cityId = Integer.parseInt(fileName.substring(fileNameLen - 6, fileNameLen - 5));
                // System.out.println("Is Focus data  " + isFocusData );

                // Save Segments
                ArrayList<ArrayList<String>> gazeData = CSVWriter.readCSV2String(listOfFiles[i].getAbsolutePath(), true, ";");

                // Read line and do data filtering
                if (isFocusData) {
                    // no need to do segmentation
                    numberOfInstance_focus++;
                    GazeSegment gazeSegment = new GazeSegment("0", cityId);
                    for (ArrayList<String> oneline : gazeData) {
                        gazeSegment.addGazeByString(oneline);
                    }
                    segments.add(gazeSegment);
                    valideFocusinMS += (int) (Math.ceil( gazeSegment.getSegmentDuration() / 1000.0) * 1000); ;
                } else {
                    // need to read the first col for segment checking
                    GazeSegment gazeSegment = new GazeSegment("1", cityId);
                    for (ArrayList<String> oneline : gazeData) {
                        if (currentSegmentNumber != (int) Double.parseDouble(oneline.get(0))) {
                            // save the current segment
                            if (currentSegmentNumber != -1) {
                                segments.add(gazeSegment);
                                System.out.print("ReactionTime;"+subjectId+";"+gazeSegment.getSegmentDuration());
                                if (gazeSegment.getSegmentDuration() > focusThreshold) {
                                    System.out.println(";true");
                                    validSegmentCounter++;
                                    valideMWinMS += (int) (Math.ceil( (gazeSegment.getSegmentDuration() - focusThreshold) / 1000.0) * 1000); ;
                                }else{
                                    System.out.println(";false");
                                }
                            }

                            // renew the object, change the current segment number
                            gazeSegment = new GazeSegment("1", cityId);
                            currentSegmentNumber = (int) Double.parseDouble(oneline.get(0));
                            numberOfInstance_mw++;
                        }

                        // read data
                        gazeSegment.addGazeByString(oneline);
                    }
                    if (gazeSegment.hasGazeData()) {
                        segments.add(gazeSegment); //  add the last gazeSegment
                        if (gazeSegment.getSegmentDuration() > focusThreshold) {
                            validSegmentCounter++;
                            valideMWinMS +=  (int) (Math.ceil( (gazeSegment.getSegmentDuration() - focusThreshold) / 1000.0) * 1000); ;
                        }
                    }
                }
            }
        }
    }

    
    public void runFeatureGeneration(boolean withVergence){
        for (GazeSegment seg : segments){
            seg.runFeatureGeneration(focusThreshold,withVergence);
        }
    }

    public ArrayList<ArrayList<Double>> getFeatureListsWithLabel (){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        for (GazeSegment seg : segments){
            output.addAll(seg.getFeaturesList());
        }

        return output;
    }

    public ArrayList<String> getFeatureHeader(){
        return segments.get(0).getHeaderLine();
    }

    // ------------ Getter and Setter ------------

    public int getValidSegmentCounter() {
        return validSegmentCounter;
    }

    public int getNumebrOfSegments() {
        return segments.size();
    }

    public ArrayList<GazeSegment> getSegments() {
        return segments;
    }

    public long getValideMWinMS() {
        return valideMWinMS;
    }

    public long getValideFocusinMS() {
        return valideFocusinMS;
    }

    public int getSubjectId() {
        return subjectId;
    }
}
