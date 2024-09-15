package mwdetection.eyetracking.objects;

import java.awt.Polygon;
import java.util.Arrays;

/**
 * This class represents an Area of Interest (AOI), which has a polygon shape
 * <p>
 * Created by vanagnos on 02.08.2016.
 */
public class AOIPolygon extends AOI {

    // ------ATTRIBUTES------
    /**
     * The polygon representing the area of the AOI
     */
    private Polygon polygon;

    // ------CONSTRUCTORS------

    /**
     * This simple constructor helps to serve as a container for AOIs.
     *
     * @param name the name of the AOI bing gazed/fixated at.
     */
    public AOIPolygon(String name) {
        super(name);
    }

    /**
     * The default constructor of AOIs.
     *
     * @param name   the name of this AOI.
     * @param xcoord an array of X coordinates.
     * @param ycoord an array of Y coordinates.
     */
    public AOIPolygon(String name, int[] xcoord, int[] ycoord) {
        super(name);

        polygon = new Polygon(xcoord, ycoord, xcoord.length);
    }

    /**
     * The default constructor of AOIs.
     *
     * @param name        the name of this AOI.
     * @param description a description of this AOI.
     * @param xcoord      an array of X coordinates.
     * @param ycoord      an array of Y coordinates.
     */
    public AOIPolygon(String name, String description, int[] xcoord, int[] ycoord) {
        super(name, description);

        polygon = new Polygon(xcoord, ycoord, xcoord.length);
    }

    // ------GETTER/SETTER------

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    // ------TOSTRING;EQUALS;HASHCODE------
    @Override
    public String toString() {
        return "AOIPolygon [polygon=" + polygon + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();

        int sum = 0;

        if (polygon != null) {
            for (int element : polygon.xpoints)
                sum += element;

            for (int element : polygon.ypoints)
                sum += element;
        }

        result = prime * result + sum;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AOIPolygon other = (AOIPolygon) obj;
        if (polygon == null) {
            if (other.polygon != null)
                return false;
        } else {

            if (polygon.npoints != other.polygon.npoints) {
                return false;
            }

            if (!Arrays.equals(polygon.xpoints, other.polygon.xpoints)) {
                return false;
            }

            if (!Arrays.equals(polygon.ypoints, other.polygon.ypoints)) {
                return false;
            }
        }

        return true;
    }
}
