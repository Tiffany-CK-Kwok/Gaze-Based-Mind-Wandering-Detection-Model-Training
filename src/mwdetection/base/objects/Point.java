package mwdetection.base.objects;


import mwdetection.base.abstracts.io.Loggable;

/**
 * POJO.
 * <p>
 * This class represents a 2D Point coordinate/point. We implement our own version to be able to support both mobile
 * and desktop applications. It stores an x- and y-coordinate (integer) and a timestamp as it is inherited from
 * {@link Loggable} (automatically set to current system-time).
 * <p>
 * Note on uniqueness: we check for uniqueness by local attributes and ignore inherited ones (timestamp)!
 * <p>
 * Created by rudid on 18.03.2016.
 */
public class Point extends Loggable {
    //------ATTRIBUTES------
    /**
     * Increase this counter if you change this class to "verify that the sender and receiver of a
     * serialized object have loaded classes for that object that are compatible with respect to
     * serialization".
     */
    private static final long serialVersionUID = 1L;

    /**
     * The x-coordinate of the point.
     */
    public int x = 0;

    /**
     * The y-coordinate of the point.
     */
    public int y = 0;

    //------CONSTRUCTORS------

    /**
     * The default constructor.
     */
    public Point() {
        super();
    }

    /**
     * This constructor requires the coordinates of the Point.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Point(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    //------TOSTRING;EQUALS;HASHCODE------

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
