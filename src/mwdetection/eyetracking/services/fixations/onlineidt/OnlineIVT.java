package mwdetection.eyetracking.services.fixations.onlineidt;


import mwdetection.base.objects.Point;
import mwdetection.eyetracking.interfaces.FixationAlgorithm;
import mwdetection.eyetracking.objects.Fixation;
import mwdetection.eyetracking.objects.Gaze;

import java.util.ArrayList;


/**
 * The FixationComputation project is a modified implementation (online
 * computation) of the I-VT algorithm based on the description found in the
 * following paper:
 *
 * @author Tiffany Kwok
 * @version 1.0
 * @since 2019-08-21
 */
public class OnlineIVT implements FixationAlgorithm {

    // Constructor Parameters
    /**
     * Velocity threshold
     */
    private int velocity_t_in_px;
    /**
     * Velocity threshold
     */
    private double velocity_t_in_angle;

    /**
     * This window uses the duration_t value to fill up with points
     */
    private ArrayList<Gaze> window;
    /**
     * The time stamp of each incoming gaze
     */
    private ArrayList<Long> timestamps;
    private ArrayList<Long> durations;
    private ArrayList<Fixation> fixations;

    private boolean saveFixationFlag = false;
    /**
     * These parameters set the desired thresholds for the fixation computation
     *
     * @param velocity_t_in_angle
     * @param angle_to_px_ratio
     */
    public OnlineIVT(double angle_to_px_ratio, double velocity_t_in_angle) {

        this.velocity_t_in_angle = velocity_t_in_angle;
        this.velocity_t_in_px = (int) (velocity_t_in_angle * angle_to_px_ratio);
        this.window = new ArrayList<>();
        this.timestamps = new ArrayList<>();
        this.fixations = new ArrayList<>();
        this.durations = new ArrayList<>();
    }

    /**
     * This method is the core of the algorithm. Every incoming gaze is
     * evaluated based on the thresholds specified through the constructor. The
     * boolean return type is used to tell if the incoming gaze is part of an
     * existing fixation or not. True if the gaze is part of an already formed
     * fixation
     *
     * @param gaze gaze
     * @return boolean
     */
    @Override
    public boolean computeFixation(Gaze gaze) {
        window.add(gaze);
        timestamps.add(gaze.getEtTimeStamp());
        durations.add(gaze.getTimeDiff());

        if(window.size() < 2) return false;
        Gaze previousGaze = window.get(window.size()-2);

        double calVelocity = eduDist(previousGaze,gaze) / ( gaze.getTimeDiff()/1000.0 );
        if(calVelocity >= velocity_t_in_px){
            if(saveFixationFlag){
                fixations.add(computeFixationPoint());
                initAfterFixation();
                saveFixationFlag = false;
            }
            return false;
        }
        else{
            // is fixation
            saveFixationFlag = true;
            return true;
        }
    }

    public void saveLastFixation(){
        if(saveFixationFlag){
            fixations.add(computeFixationPoint());
            initAfterFixation();
            saveFixationFlag = false;
        }
    }

    private double eduDist (Gaze a, Gaze b){
        return Math.sqrt(Math.pow(a.x-b.x,2) + Math.pow(a.y-b.y,2));
    }

    /**
     * Once a fixation occurred and is over we initialize the flag and remove
     * the gazes included in the fixation.
     */
    private void initAfterFixation() {
        // remove all gaze points, but not the last one since it was not part of
        // the fixation
        int windowsize = window.size();
        for (int i = 0; i < (windowsize - 1); i++) {
            window.remove(window.size() - 2);
            timestamps.remove(timestamps.size() - 2);
            durations.remove(durations.size() - 2);
        }
    }

    /**
     * This method computes the centroid of a fixation
     *
     * @return Point
     */
    private Fixation computeFixationPoint() {
        Fixation fixation = new Fixation(getFixationDuration(), new Point(getCentroid()[0], getCentroid()[1]));

        fixation.setTimestamp(getFixationTimestamp());

        fixation.getGazes().addAll(getFixationGazePoints());

        return fixation;
    }

    /**
     * This method returns an integer array containing the x,y coordinates of
     * the fixation centroid.
     *
     * @return int[]
     */
    private int[] getCentroid() {
        int centroidX = 0;
        int centroidY = 0;

        // the last point in the window does not belong to the fixation
        for (int i = 0; i < (window.size() - 1); i++) {
            centroidX = centroidX + window.get(i).x;
            centroidY = centroidY + window.get(i).y;
        }

        centroidX = centroidX / (window.size() - 1);
        centroidY = centroidY / (window.size() - 1);

        return new int[]{centroidX, centroidY};
    }

    /**
     * This method computes the duration of the current fixation
     *
     * @return long
     */
    private long getFixationDuration() {
        // The fixation duration is the second last gaze time stamp minus the
        // first gaze time stamp
        // return timestamps.get((timestamps.size() - 2)) - timestamps.get(0);

        // *** Changed to be fit the eyetracker nano second timer ,
        long duration = 0;
        for (int loop = 0; loop < durations.size() - 1; loop++) {
            duration = (long) (duration + durations.get(loop));
        }
        return duration;
    }

    /**
     * This method returns the time stamp of the current fixation. This is the
     * time stamp of the first gaze point included in the fixation cluster.
     *
     * @return long
     */
    private long getFixationTimestamp() {
        // the fixation time stamp is the time the first gaze in the fixation
        // occurred
        return timestamps.get(0);
    }

    private ArrayList<Gaze> getFixationGazePoints() {
        ArrayList<Gaze> fixationGazes = new ArrayList<>();
        for (int i = 0; i < (window.size() - 1); i++) {
            fixationGazes.add(window.get(i));
        }
        return fixationGazes;
    }

    /**
     * This method returns all fixations that occurred so far.
     *
     * @return ArrayList<Point>
     */
    public ArrayList<Fixation> getFixations() {
        return fixations;
    }

    /**
     * This method returns an unfinished fixation, meaning that there is an
     * ongoing fixation.
     *
     * @return Fixation
     */
    @Override
    public Fixation getCurrentFixation() {
        Fixation unfinishedFixation = new Fixation(getFixationDuration(), new Point(getCentroid()[0], getCentroid()[1]));

        unfinishedFixation.setTimestamp(getFixationTimestamp());

        unfinishedFixation.getGazes().addAll(getFixationGazePoints());

        return unfinishedFixation;
    }
}
