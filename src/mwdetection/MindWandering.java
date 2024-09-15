package mwdetection;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;

public class MindWandering {

    private String rootFolder = "";
    private boolean withVergence = true;
    private int focusThreshold = 3592; 

    private ArrayList<MWSubject> subjectsList;
    private Instances testSet, trainingSet;

    public MindWandering(String rootFolder, String mlModel) {
        System.out.print("Project: Mind Wandering Detection-- ");

        this.rootFolder = rootFolder;

        // Step : Create a list to store "Subject"s
        subjectsList = new ArrayList<MWSubject>();

        long validMWinMS = 0, validFocusinMs = 0;

        // Step : Loop to Read Data to "Subject"
        for (int sid = 1; sid <= 27; sid++) {
            System.out.println("------ Subject " + sid);
            subjectsList.add(new MWSubject(rootFolder, sid, focusThreshold));  // Create MWSubject object and save it to the subject list , CSV are read on object construction
            validMWinMS += subjectsList.get(subjectsList.size() - 1).getValideMWinMS();
            validFocusinMs += subjectsList.get(subjectsList.size() - 1).getValideFocusinMS();
            System.out.println("End of Subject " + sid + " ; Number of valid segment " + subjectsList.get(subjectsList.size() - 1).getValidSegmentCounter() + " out of " + subjectsList.get(subjectsList.size() - 1).getNumebrOfSegments());
            System.out.println("Subject;" + sid + ";"+subjectsList.get(subjectsList.size() - 1).getValideMWinMS() / 1000.0 +";"+subjectsList.get(subjectsList.size() - 1).getValideFocusinMS() / 1000.0);

            // Step : Feature Extraction
            subjectsList.get(sid - 1).runFeatureGeneration(withVergence);
        }

        System.out.println("MS data in second : " + validMWinMS / 1000.0 + "  ; Focus data in second : " + validFocusinMs / 1000.0);

        // Step : Loop to train and test the model With Weka 3.0
        setupWekaHeader(subjectsList.get(0).getFeatureHeader());

        // Save Segments : for Hyperparameter tunning "/preRandom_list-Hyperparameter.csv", for training and testing "/preRandom_list.csv"
        ArrayList<ArrayList<String>> all_set_string = CSVWriter.readCSV2String(rootFolder + "/preRandom_list.csv", false, ",");

        switch (mlModel) {
            case "RF":
                System.out.println("Run random forest");
                break;
            case "SVM":
                System.out.println("Run SVM");
                break;
            case "MLP":
                System.out.println("Run MLP");
                break;
            default:
                System.out.println("Unknown model");
                break;
        }

        System.out.println("AOC, F1, FP, Precision, Recall");

        for (int iteration = 0; iteration < all_set_string.size(); iteration++){
            ArrayList<ArrayList<Integer>> all_set = new ArrayList<>();

            ArrayList<Integer> preRandom_set1 = new ArrayList<Integer>();
            for(int i =0; i <9; i++){
                preRandom_set1.add(Integer.parseInt(all_set_string.get(iteration).get(i)));
            }
            all_set.add(preRandom_set1);

            ArrayList<Integer> preRandom_set2 = new ArrayList<Integer>();
            for(int i =9; i <18; i++){
                preRandom_set2.add(Integer.parseInt(all_set_string.get(iteration).get(i)));
            }
            all_set.add(preRandom_set2);

            ArrayList<Integer> preRandom_set3 = new ArrayList<Integer>();
            for(int i =18; i <27; i++){
                preRandom_set3.add(Integer.parseInt(all_set_string.get(iteration).get(i)));
            }
            all_set.add(preRandom_set3);

            for (int setNo = 0 ; setNo < 3; setNo++){
                for (MWSubject sub : subjectsList) {
                    for (ArrayList<Double> oneLine : sub.getFeatureListsWithLabel()) {
                        Instance instance = new DenseInstance(oneLine.size());
                        for (int i = 0; i < oneLine.size(); i++) {
                            instance.setValue(i, oneLine.get(i));
                        }

                        if ( all_set.get(setNo).contains(sub.getSubjectId())) {
                            testSet.add(instance);

                        } else {
                            trainingSet.add(instance);
                        }
                    }
                }
                try {
                    ConverterUtils.DataSink.write("./OutputArff/training_"+ iteration+"_"+ setNo +".arff", trainingSet);
                    ConverterUtils.DataSink.write("./OutputArff/testing_"+ iteration+"_"+ setNo +".arff", testSet);

                    if(mlModel.equals("RF")){
                        // Run Random Forest
                        runRandomForest(100, false);
                    }else if(mlModel.equals("SVM")){
                        // Run SMO
                        runSMO("-C 20.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\"", false);
                    }else if(mlModel.equals("MLP")){
                        // Run MLP
                        runMLP("-L 0.5 -M 0.3 -N 500 -V 0 -S 0 -E 20 -H a,5 -D", false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                testSet.clear();
                trainingSet.clear();
            }
        }

    }

    public void runRandomForest(int numIterations, boolean printAlgorithmSummary) throws Exception {
        RandomForest rf = new RandomForest();
        rf.setNumIterations(numIterations);

        rf.buildClassifier(trainingSet);

        Evaluation eval = new Evaluation(trainingSet);
        eval.evaluateModel(rf, testSet);

        if(printAlgorithmSummary) {
            System.out.println("** Decision Tress Evaluation with Datasets **");
            System.out.println(eval.toSummaryString());
            System.out.print(" the expression for the input data as per algorithm is ");
            System.out.println(rf);
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toClassDetailsString());
        }

        String ownOutput = eval.weightedAreaUnderROC() + "," + eval.weightedFMeasure() +","+ eval.falsePositiveRate(1) + "," + eval.precision(1) + "," + eval.recall(1);

        System.out.println(ownOutput);
    }

    public void runSMO(String option, boolean printAlgorithmSummary) throws Exception {
        SMO smo = new SMO();
        smo.setOptions(Utils.splitOptions(option));

        smo.buildClassifier(trainingSet);

        Evaluation eval = new Evaluation(trainingSet);
        eval.evaluateModel(smo, testSet);

        if(printAlgorithmSummary){
            System.out.println("** SMO Evaluation with Datasets **");
            System.out.println(eval.toSummaryString());
            System.out.print(" the expression for the input data as per algorithm is ");
            System.out.println(smo);
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toClassDetailsString());
        }

        String ownOutput = eval.weightedAreaUnderROC() + "," + eval.weightedFMeasure() +","+ eval.falsePositiveRate(1) + "," + eval.precision(1) + "," + eval.recall(1);

        System.out.println(ownOutput);
    }

    public void runMLP(String option, boolean printAlgorithmSummary) throws Exception {
        MultilayerPerceptron mlp = new MultilayerPerceptron();

        mlp.setOptions(Utils.splitOptions(option));

        mlp.buildClassifier(trainingSet);

        Evaluation eval = new Evaluation(trainingSet);
        eval.evaluateModel(mlp, testSet);

        if(printAlgorithmSummary) {
            System.out.println("** MLP Evaluation with Datasets **");
            System.out.println(eval.toSummaryString());
            System.out.print(" the expression for the input data as per algorithm is ");
            System.out.println(mlp);
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toClassDetailsString());
        }

        String ownOutput = eval.weightedAreaUnderROC() + "," + eval.weightedFMeasure() +","+ eval.falsePositiveRate(1) + "," + eval.precision(1) + "," + eval.recall(1);

        System.out.println(ownOutput);
    }


    public void setupWekaHeader(ArrayList<String> header) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for (String name : header) {
            if(name.equals("GT")){
                ArrayList<String> m_normal_value = new ArrayList<>();
                m_normal_value.add("0");
                m_normal_value.add("1");
                attributes.add(new Attribute(name, m_normal_value));
            }else{
                attributes.add(new Attribute(name));
            }
        }

        testSet = new Instances("Test dataset", attributes, 0);
        trainingSet = new Instances("Training dataset", attributes, 0);

        testSet.setClassIndex(testSet.numAttributes() - 1);
        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
    }
}
