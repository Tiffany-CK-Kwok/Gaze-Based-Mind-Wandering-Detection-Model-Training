package mwdetection.eyetracking.interfaces;

import mwdetection.eyetracking.objects.Fixation;
import mwdetection.eyetracking.objects.Gaze;

/**
 * This interface represents any Fixation Algorithm and defines the minimum set of methods, that such an algorithm
 * should provide.
 * <p>
 * Created by rudid on 23.06.2016.
 */
public interface FixationAlgorithm {
    /**
     * This method uses the given gaze for the Fixation computation and returns true, as soon as the gaze
     * contributed to a new Fixation. It returns false if it did not, i.e., also, when the gaze was no longer
     * part of any previous fixation.
     *
     * @param gaze the gaze to consider for the fixation computation.
     * @return true if the given gaze contributed to a new fixation, false otherwise.
     */
    boolean computeFixation(Gaze gaze);

    /**
     * This method returns the current fixation, i.e., the fixation the algorithm is currently associating the given
     * the gazes with.
     *
     * @return the currently "running" fixation.
     */
    Fixation getCurrentFixation();
}
