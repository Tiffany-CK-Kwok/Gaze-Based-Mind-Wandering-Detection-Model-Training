package mwdetection;

import java.util.ArrayList;

/**
 * This is a class that for extracting any Saccade related features
 * The saccade sequences are generated with fixation features object creation
 * Recommended to use it together with fixation feature object
 */
public class SaccadesFeatures {

    private ArrayList<Double> saccadeDurations = new ArrayList<Double>();
    private ArrayList<Double> saccadeDistance = new ArrayList<Double>();
    private ArrayList<Double> saccadeSpeed = new ArrayList<Double>();
    private ArrayList<Double> saccadeSpeed_Mean_collection = new ArrayList<Double>();
    private ArrayList<Double> saccadeLength_Mean_collection = new ArrayList<Double>();
    private double segmentLength =0;

    public SaccadesFeatures( FixaitonFeatures fixFeature) {

        this.segmentLength = fixFeature.getSegmentLength();

        this.saccadeDurations = fixFeature.getSaccadeD();
        this.saccadeDistance = fixFeature.getSaccadeDistance();

        for (int i = 0; i < saccadeDistance.size(); i++) {
            if (saccadeDurations.get(i) == 0)
                saccadeSpeed.add(0.0);
            else
                saccadeSpeed.add(saccadeDistance.get(i) / saccadeDurations.get(i));
        }
    }

    /**
     * This function is for returning the header of length related features
     * @return String ArrayList
     */
    public ArrayList<String> lengthFeaturesHeader(){
        ArrayList<String> output = new ArrayList<String>();

        output.add("Stdv_Saccade_Length");
        output.add("Mean_Saccade_Length");
        output.add("Max_Saccade_Length");
        output.add("Min_Saccade_Length");
        output.add("Median_Saccade_Length");
        output.add("Skewness_Saccade_Length");
        output.add("Kurtosis_Saccade_Length");
        output.add("Count_Saccade_per_Second");

        return output;
    }

    /**
     * For getting statistical features related to saccade length
     */
    public ArrayList<Double> lengthFeatures(){
        ArrayList<Double> output = new ArrayList<Double>();
        if (saccadeDistance.size()>0) {
            // There are fixations in side the List
            output.add(GeneralFunction.arrayStdv(saccadeDistance));
            output.add(GeneralFunction.arrayMean(saccadeDistance));
            output.add(GeneralFunction.arrayMax(saccadeDistance));
            output.add(GeneralFunction.arrayMin(saccadeDistance));
            output.add(GeneralFunction.arrayMedian(saccadeDistance));
            output.add(GeneralFunction.arraySkewness(saccadeDistance));
            output.add(GeneralFunction.arrayKurtosis(saccadeDistance));

            output.add(saccadeDistance.size()/(segmentLength/1000));
        }else{
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

    public ArrayList<String> durationFeatureHeader(){
        ArrayList<String> output = new ArrayList<String>();

        output.add("Mean_Saccade_Duration");
        output.add("Stdv_Saccade_Duration");
        output.add("Skewness_Saccade_Duration");
        output.add("Kurtosis_Saccade_Duration");
        output.add("Median_Saccade_Duration");
        output.add("Min_Saccade_Duration");
        output.add("Max_Saccade_Duration");

        return output;
    }

    public ArrayList<Double> durationFeature(){
        ArrayList<Double> output = new ArrayList<Double>();
        if (saccadeDistance.size()>0) {
            // There are fixations in side the List
            output.add(GeneralFunction.arrayMean(saccadeDistance));
            output.add(GeneralFunction.arrayStdv(saccadeDistance));
            output.add(GeneralFunction.arraySkewness(saccadeDistance));
            output.add(GeneralFunction.arrayKurtosis(saccadeDistance));
            output.add(GeneralFunction.arrayMedian(saccadeDistance));
            output.add(GeneralFunction.arrayMin(saccadeDistance));
            output.add(GeneralFunction.arrayMax(saccadeDistance));
        }else{
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

    /**
     * This function is for returning the header of speed related features
     * @return String ArrayList
     */
    public ArrayList<String> speedFeatureHeader(){
        ArrayList<String> output = new ArrayList<String>();

        output.add("Mean_Saccade_Speed");
        output.add("Stdv_Saccade_Speed");
        output.add("Skewness_Saccade_Speed");
        output.add("Kurtosis_Saccade_Speed");
        output.add("Median_Saccade_Speed");
        output.add("Min_Saccade_Speed");
        output.add("Max_Saccade_Speed");

        return output;
    }

    /**
     * For getting statistical features related to saccade speed
     * @return ArrayList<Double> output [ Mean, Stdv]
     */
    public ArrayList<Double> speedFeatures(){
        ArrayList<Double> output = new ArrayList<Double>();
        if (saccadeSpeed.size()>0) {
            // There are fixations in side the List
            output.add(GeneralFunction.arrayMean(saccadeSpeed));
            output.add(GeneralFunction.arrayStdv(saccadeSpeed));
            output.add(GeneralFunction.arraySkewness(saccadeSpeed));
            output.add(GeneralFunction.arrayKurtosis(saccadeSpeed));
            output.add(GeneralFunction.arrayMedian(saccadeSpeed));
            output.add(GeneralFunction.arrayMin(saccadeSpeed));
            output.add(GeneralFunction.arrayMax(saccadeSpeed));

        }else{
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
}
