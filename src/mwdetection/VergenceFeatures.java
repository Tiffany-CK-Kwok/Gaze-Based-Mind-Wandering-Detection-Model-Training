package mwdetection;

import mwdetection.base.objects.Point;
import mwdetection.eyetracking.objects.Gaze;
import mwdetection.eyetracking.services.fixations.onlineidt.OnlineIDT;

import java.util.ArrayList;

public class VergenceFeatures {

    private OnlineIDT onlineIDT;
    private ArrayList<Boolean> isFixation = new ArrayList<Boolean>();
    private ArrayList<Double> gaze_pairs_disparity = new ArrayList<Double>();
    private ArrayList<Double> gaze_centroids_disparity = new  ArrayList<Double>();

    public VergenceFeatures(ArrayList<?> inputObj) {
        ArrayList<Gaze> gazeObj = (ArrayList<Gaze>) inputObj;
        processGazeObj(gazeObj);
    }

    private void initIDTObject() {
        onlineIDT = new OnlineIDT(120, 100); // 3 degree = 63
    }

    public void processGazeObj(ArrayList<Gaze> gazeObj) {
        initIDTObject();
        for (Gaze g : gazeObj) {
            isFixation.add(onlineIDT.computeFixation(g));
            gaze_pairs_disparity.add(GeneralFunction.euclidienDistance(g.getGaze_L().x, g.getGaze_L().y, g.getGaze_R().x, g.getGaze_R().y));
        }

        // Get the fixations duration and fixations centroids
        for (int loop = 0; loop< onlineIDT.getFixations().size();loop++){
           Point centroid_left = onlineIDT.getFixations().get(loop).getCentroid_Left();
           Point centroid_right = onlineIDT.getFixations().get(loop).getCentroid_Right();

           if(centroid_left != null && centroid_right != null )
               gaze_centroids_disparity.add(GeneralFunction.euclidienDistance(centroid_left.x,centroid_left.y,centroid_right.x,centroid_right.y));
        }
    }


    public ArrayList<String> getVergenceFeatures_Header() {
        ArrayList<String> header = new ArrayList<String>();

        header.add("Mean_Disparity_gaze_pair");
        header.add("SD_Disparity_gaze_pair");

        header.add("Mean_Centroid_Dist_of_Gazes");
        header.add("SD_Centroid_Dist_of_Gazes");

        return header;
    }

    public ArrayList<Double> getVergenceFeatures() {
        ArrayList<Double> output = new ArrayList<Double>();

        if (gaze_pairs_disparity.size() > 0) {
            output.add(GeneralFunction.arrayMean(gaze_pairs_disparity)); // Get Mean_Disparity_gaze_pair
            output.add(GeneralFunction.arrayStdv(gaze_pairs_disparity)); // Get SD_Disparity_gaze_pair
            output.add(GeneralFunction.arrayMean(gaze_centroids_disparity)); // Get Mean_Centroid_Dist_of_Gazes
            output.add(GeneralFunction.arrayStdv(gaze_centroids_disparity)); // Get SD_Centroid_Dist_of_Gazes


        } else {
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
            output.add(0.0);
        }

        return output;
    }

}
