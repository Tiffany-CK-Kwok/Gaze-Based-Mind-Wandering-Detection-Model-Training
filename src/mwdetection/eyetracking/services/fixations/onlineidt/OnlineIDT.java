package mwdetection.eyetracking.services.fixations.onlineidt;


import mwdetection.base.objects.Point;
import mwdetection.eyetracking.interfaces.FixationAlgorithm;
import mwdetection.eyetracking.objects.Fixation;
import mwdetection.eyetracking.objects.Gaze;

import java.util.ArrayList;


/**
 * The FixationComputation project is a modified implementation (online
 * computation) of the I-DT algorithm based on the description found in the
 * following paper:
 * <p>
 * Dario D. Salvucci and Joseph H. Goldberg. 2000. Identifying fixations and
 * saccades in eye-tracking protocols. In Proceedings of the 2000 symposium on
 * Eye tracking research & applications (ETRA '00). ACM, New York, NY, USA,
 * 71-78. DOI=http://dx.doi.org/10.1145/355017.355028
 *
 * @author Ioannis Giannopoulos
 * @version 1.0
 * @since 2016-05-19
 */
public class OnlineIDT implements FixationAlgorithm {

	// Constructor Parameters
	/**
	 * Dispersion threshold
	 */
	private int dispersion_t;
	/**
	 * Duration threshold
	 */
	private long duration_t;

	/**
	 * This window uses the duration_t value to fill up with points
	 */
	private ArrayList<Gaze> window;
	/**
	 * The time stamp of each incoming gaze
	 */
	private ArrayList<Long> timestamps;
	/**
	 * MinMax values for the dispersion computation: not used globally
	 */
	private ArrayList<Long> durations;
	private int minX, maxX, minY, maxY;
	/**
	 * this flag is set to true every time the minimum requirements for a
	 * fixation are met.
	 */
	private boolean flag;
	/**
	 * This array stores all generated fixations
	 */
	private ArrayList<Fixation> fixations;

	/**
	 * These parameters set the desired thresholds for the fixation computation
	 *
	 * @param dispersion
	 * @param duration
	 */
	public OnlineIDT(int dispersion, long duration) {
		this.flag = false;
		this.dispersion_t = dispersion;
		this.duration_t = duration;
		this.window = new ArrayList<>();
		this.timestamps = new ArrayList<>();
		this.durations = new ArrayList<>();
		this.fixations = new ArrayList<>();
	}

	/**
	 * This method is the core of the algorithm. Every incoming gaze is
	 * evaluated based on the thresholds specified through the constructor. The
	 * boolean return type is used to tell if the incoming gaze is part of an
	 * existing fixation or not. True if the gaze is part of an already formed
	 * fixation
	 *
	 * @param gaze      gaze
	 * @return boolean
	 */
	@Override
	public boolean computeFixation(Gaze gaze) {
		window.add(gaze);
		 timestamps.add(gaze.getEtTimeStamp());
		durations.add(gaze.getTimeDiff());
		// if the dispersion is below the threshold
		if (isValidDispersion()) {
			// if the current duration is over the threshold
			if (minDurationReached()) {
				flag = true;
				return true;
			} else {
				// just continue
				if (flag) {
					return true;
				}
				return false;

			}
		} else {
			// if the dispersion is not valid check the current state
			handleInvalidDispersion();
			return false;
		}
	}

	/**
	 * Once an incoming gaze is not satisfying the dispersion threshold, we
	 * check if before that gaze a fixation was formed (flag should be true). If
	 * yes, we compute the centroid of the fixation and store it in an array.
	 */
	private void handleInvalidDispersion() {
		// if fixation compute it
		if (flag) {
			fixations.add(computeFixationPoint());
			initAfterFixation();
		} else {// if not fixation then remove the first point
			determineNewWindow();
		}
	}

	/**
	 * Once a fixation occurred and is over we initialize the flag and remove
	 * the gazes included in the fixation.
	 */
	private void initAfterFixation() {
		// set fixation flag to false
		flag = false;
		// remove all gaze points, but not the last one since it was not part of
		// the fixation
		int windowsize = window.size();
		for (int i = 0; i < (windowsize - 1); i++) {
			window.remove(window.size() - 2);
			timestamps.remove(timestamps.size() - 2);
			durations.remove(durations.size()-2);
		}
	}

	/**
	 * If an incoming gaze is not satisfying the dispersion threshold, the
	 * existing window has to be determined again. Starting from the first gaze
	 * in that window, we delete every gaze until the dispersion threshold
	 * criterion is satisfied.
	 */
	private void determineNewWindow() {
		// remove the first gaze from the window
		window.remove(0);
		timestamps.remove(0);
		durations.remove(0);
		// shrink the window until a valid dispersion is reached
		while (!isValidDispersion()) {
			window.remove(0);
			timestamps.remove(0);
			durations.remove(0);
		}
	}

	/**
	 * If the minimum duration criterion is satisfied the method returns TRUE
	 *
	 * @return boolean
	 */
	private boolean minDurationReached() {
		// current time - start time
		//return (timestamps.get(timestamps.size() - 1) - timestamps.get(0)) >= duration_t;
		double checkT =0;
		for(double d : durations)
			checkT += d;

		return checkT >=duration_t;
	}

	/**
	 * This method checks if the dispersion criterion is still satisfied
	 *
	 * @return boolean
	 */
	private boolean isValidDispersion() {
		// get min and max values of X and Y
		computeMinMax();
		// calculate dispersion
		int D = (maxX - minX) + (maxY - minY);

		return D <= dispersion_t;
	}

	/**
	 * This method calculates the Min and Max value for X and Y from the gaze
	 * points currently in the window.
	 */
	private void computeMinMax() {
		// This method calculates the Min and Max value for X and Y from the
		// gaze points
		// currently in the window.
		minX = window.get(0).x;
		minY = window.get(0).y;

		maxX = window.get(0).x;
		maxY = window.get(0).y;

		for (int i = 1; i < window.size(); i++) {
			if (window.get(i).x < minX) {
				minX = window.get(i).x;
			}
			if (window.get(i).y < minY) {
				minY = window.get(i).y;
			}
			if (window.get(i).x > maxX) {
				maxX = window.get(i).x;
			}
			if (window.get(i).y > maxY) {
				maxY = window.get(i).y;
			}
		}
	}

	/**
	 * This method computes the centroid of a fixation
	 *
	 * @return Point
	 */
	private Fixation computeFixationPoint() {
		Fixation fixation = new Fixation(getFixationDuration(), new Point(getCentroid()[0], getCentroid()[1]));

		// ---- Commented for reanalysising the data (15/11/2021)
		//fixation.setCentroid_Left(new Point(getCentroid_left()[0], getCentroid_left()[1]));
       // fixation.setCentroid_Right(new Point(getCentroid_right()[0], getCentroid_right()[1]));

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

	private int[] getCentroid_left() {
		int centroidX = 0;
		int centroidY = 0;

		// the last point in the window does not belong to the fixation
		for (int i = 0; i < (window.size() - 1); i++) {
			centroidX = centroidX + window.get(i).getGaze_L().x;
			centroidY = centroidY + window.get(i).getGaze_L().y;
		}

		centroidX = centroidX / (window.size() - 1);
		centroidY = centroidY / (window.size() - 1);

		return new int[]{centroidX, centroidY};
	}

	private int[] getCentroid_right() {
		int centroidX = 0;
		int centroidY = 0;

		// the last point in the window does not belong to the fixation
		for (int i = 0; i < (window.size() - 1); i++) {
			centroidX = centroidX + window.get(i).getGaze_R().x;
			centroidY = centroidY + window.get(i).getGaze_R().y;
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
		for(int loop =0; loop< durations.size()-1;loop++){
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

        unfinishedFixation.setCentroid_Left(new Point(getCentroid_left()[0], getCentroid_left()[1]));
        unfinishedFixation.setCentroid_Right(new Point(getCentroid_right()[0], getCentroid_right()[1]));

		unfinishedFixation.setTimestamp(getFixationTimestamp());

		unfinishedFixation.getGazes().addAll(getFixationGazePoints());

		return unfinishedFixation;
	}
}
