package mwdetection.eyetracking.objects;


import mwdetection.base.abstracts.io.Loggable;

/**
 * This class represents an Area of Interest (AOI), which can have a name and
 * description. However, only the name is mandatory and the AOIs object's unique
 * identifier.
 * <p>
 * Like all {@link Loggable} class it has a a timestamp (automatically set to
 * current system-time).
 * <p>
 * Note on uniqueness: we check for uniqueness by local attributes and ignore
 * inherited ones (timestamp)!
 * <p>
 * Created by rudid on 18.03.2016.
 */
public abstract class AOI extends Loggable {
    /**
     * The name of the AOI
     */
    private String name = null;

    /**
     * A description of the AOI
     */
    private String description = null;

    /**
     * The default constructor of AOIs, containing only the AOI name.
     *
     * @param name the name of this AOI.
     */
    public AOI(String name) {
        this.name = name;
    }

    /**
     * The AOI constructor taking both the name and description as parameters.
     *
     * @param name        the name of this AOI.
     * @param description a description of this AOI.
     */
    public AOI(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // ------GETTER/SETTER------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ------TOSTRING;EQUALS;HASHCODE------

    @Override
    public String toString() {
        return "AOI{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AOI aoi = (AOI) o;

        if (name != null ? !name.equals(aoi.name) : aoi.name != null) return false;
        return description != null ? description.equals(aoi.description) : aoi.description == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
