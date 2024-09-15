package mwdetection.eyetracking.objects;

import mwdetection.base.objects.Point;

/**
 * This is a wrapper class for {@link Point}s, representing Gazes.
 * <p>
 * Note on uniqueness: we check for uniqueness by inherited attributes from
 * {@link Point}, but without timestamp!
 * <p>
 * Created by rudid on 17.06.2016.
 * Edited by tiff on 16.07.2019
 */
public class Gaze extends Point {
    double pupilRadius_L = -1;
    double pupilConfidence_L = -1;
    double pupilRadius_R = -1;
    double pupilConfidence_R = -1;
    Point gaze_L, gaze_R;
	long timeDiff = 0L;
	long etTimeStamp = 0L;
	boolean newData = false;
    public Point gaze_before_match= new Point(0,0);

	/**
	 * The default constructor of the Gaze object calls the constructor of
	 * {@link Point}.
	 */
	public Gaze() {
		super();
		gaze_L = new Point(0,0);
        gaze_R = new Point(0,0);
	}

	/**
	 * The default constructor of the Gaze object calls the constructor of
	 * {@link Point}.
	 *
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public Gaze(int x, int y, long currentTime,long diff,long etTimeStamp) {
		super(x, y);
		this.setTimestamp(currentTime);
		this.timeDiff = diff;
		this.etTimeStamp = etTimeStamp;
	}

    // -------------  Getter & Setter --------------

	public long getTimeDiff() {
		return timeDiff;
	}

    public long getEtTimeStamp() {
        return etTimeStamp;
    }

	public boolean isNewData() {
		return newData;
	}

	public void setNewData(boolean newData) {
		this.newData = newData;
	}

    public double getPupilRadius_L() {
        return pupilRadius_L;
    }

    public void setPupilRadius_L(double pupilRadius_L) {
        this.pupilRadius_L = pupilRadius_L;
    }

    public double getPupilConfidence_L() {
        return pupilConfidence_L;
    }

    public void setPupilConfidence_L(double pupilConfidence_L) {
        this.pupilConfidence_L = pupilConfidence_L;
    }

    public double getPupilRadius_R() {
        return pupilRadius_R;
    }

    public void setPupilRadius_R(double pupilRadius_R) {
        this.pupilRadius_R = pupilRadius_R;
    }

    public double getPupilConfidence_R() {
        return pupilConfidence_R;
    }

    public void setPupilConfidence_R(double pupilConfidence_R) {
        this.pupilConfidence_R = pupilConfidence_R;
    }

    public Point getGaze_L() {
        return gaze_L;
    }

    public void setGaze_L(Point gaze_L) {
        this.gaze_L = gaze_L;
    }

    public Point getGaze_R() {
        return gaze_R;
    }

    public void setGaze_R(Point gaze_R) {
        this.gaze_R = gaze_R;
    }

    public void setTimeDiff(long timeDiff) {
        this.timeDiff = timeDiff;
    }

    public void setEtTimeStamp(long etTimeStamp) {
        this.etTimeStamp = etTimeStamp;
    }
}
