package mwdetection.eyetracking.objects;



import mwdetection.base.abstracts.io.Loggable;
import mwdetection.base.objects.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO.
 * <p>
 * This class represents a Fixation with the centroid {@link Point} of the
 * fixation, a timestamp (moment of fixation) as it is inherited from
 * {@link Loggable} (automatically set to current system-time), a duration
 * (length of fixation) and a list of gaze {@link Gaze}s resulting in the
 * fixation.
 * <p>
 * Note on uniqueness: we check for uniqueness by local attributes and ignore
 * inherited ones (timestamp)!
 * <p>
 * Created by Ioannis Giannopoulos extended by rudid.
 */
public class Fixation extends Loggable {
    // ------ATTRIBUTES------
    /**
     * Increase this counter if you change this class to "verify that the sender
     * and receiver of a serialized object have loaded classes for that object
     * that are compatible with respect to serialization".
     */
    private static final long serialVersionUID = 1L;

    /**
     * The fixation duration.
     */
    private long duration = 0;

    /**
     * The Fixation point as a Point object.
     */
    private Point centroid = null;
    private Point centroid_Left = null;
    private Point centroid_Right = null;

    /**
     * A local list of gazes contributing to this fixation.
     */
    private List<Gaze> gazes = new ArrayList<>();

    // ------CONSTRUCTORS------

    /**
     * The default constructor containing only the centroid of the Fixation.
     *
     * @param centroid the center {@link Point} of fixation
     */
    public Fixation(Point centroid) {
        super();
        this.centroid = centroid;
    }

    /**
     * The preferable constructor for Fixations. Containing the duration of the
     * fixation, its center and the gazes it consists of.
     *
     * @param duration how long the Fixation area was "looked at"
     * @param centroid the center {@link Point} of the Fixation area
     */
    public Fixation(long duration, Point centroid) {
        super();
        this.duration = duration;
        this.centroid = centroid;
    }

    // ------GETTER/SETTER------

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public List<Gaze> getGazes() {
        return gazes;
    }

    // ------TOSTRING;EQUALS;HASHCODE------

    @Override
    public String toString() {
        return "Fixation{" + "timestamp=" + timestamp + ", duration="
                + duration + ", centroid=" + centroid + ", gazes=" + gazes
                + '}';
    }

    /**
     * Note our equality check for Fixation considers only the timestamp parameter.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Fixation fixation = (Fixation) o;

        return timestamp == fixation.timestamp;
    }

    @Override
    public int hashCode() {
        int result = (int) (duration ^ (duration >>> 32));
        result = 31 * result + (centroid != null ? centroid.hashCode() : 0);
        result = 31 * result + gazes.hashCode();
        return result;
    }

    public Point getCentroid_Left() {
        return centroid_Left;
    }

    public void setCentroid_Left(Point centroid_Left) {
        this.centroid_Left = centroid_Left;
    }

    public Point getCentroid_Right() {
        return centroid_Right;
    }

    public void setCentroid_Right(Point centroid_Right) {
        this.centroid_Right = centroid_Right;
    }
}
