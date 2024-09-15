package mwdetection;

import java.util.ArrayList;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * General Functions that could be used in different classes
 * All function are static so that function could be called without creating the object
 */
public class GeneralFunction {
    public static double arrayMean(ArrayList<Double> numberArray){
        double total = arraySum(numberArray);
        return total/(double)numberArray.size();
    }

    public static double arraySum(ArrayList<Double> numberArray){
        double sum = 0;
        for(int i =0;i<numberArray.size();i++) {
            sum += (double) numberArray.get(i);
        }
        return Double.isNaN(sum)? 0.0 : sum;
    }

    public static double arrayStdv(ArrayList<Double> numberArray){
        double average = arrayMean(numberArray);
        double stdv =0;
        for (int i=0; i<numberArray.size();i++)
        {
            stdv = stdv + Math.pow(numberArray.get(i) - average, 2);
        }
        double result = Math.sqrt(stdv/(numberArray.size()-1));
        return Double.isNaN(result)?0.0:result;
    }

    public static double arrayMin(ArrayList<Double> numberArray){
       // int minIndx =0;
        double minValue = Double.POSITIVE_INFINITY;
        for (int i=0; i<numberArray.size();i++)
        {
            if(numberArray.get(i) < minValue){
        //        minIndx = i;
                minValue = numberArray.get(i);
            }
        }
        return minValue;
    }

    public static double arrayMax(ArrayList<Double> numberArray){
       // int maxIndex =0;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i=0; i<numberArray.size();i++)
        {
            if(numberArray.get(i) > maxValue){
        //        maxIndex = i;
                maxValue = numberArray.get(i);
            }
        }
        return maxValue;
    }

    public static double arrayMedian(ArrayList<Double> numberArray)
    {
        double[] target = new double[numberArray.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = numberArray.get(i);
        }

        double result = new Median().evaluate(target);
        return Double.isNaN(result)?0.0:result;
    }
    // Function to calculate skewness.
    public static double arraySkewness(ArrayList<Double> numberArray)
    {
        // Find skewness = [n / (n -1) (n - 2)] sum[(x_i - mean)^3] / std^3
        double[] target = new double[numberArray.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = numberArray.get(i);
        }
        double s = new Skewness().evaluate(target, 0, numberArray.size());
        return Double.isNaN(s)? 0.0 : s;
    }

    public static double arrayKurtosis(ArrayList<Double> numberArray)
    {
        // Find kurtosis = { [n(n+1) / (n -1)(n - 2)(n-3)] sum[(x_i - mean)^4] / std^4 } - [3(n-1)^2 / (n-2)(n-3)]
        double[] target = new double[numberArray.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = numberArray.get(i);
        }

        double k = new Kurtosis().evaluate(target, 0, numberArray.size());
        return Double.isNaN(k)? 0.0 : k;
    }


    public static double euclidienDistance (double x1, double y1,double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
}
