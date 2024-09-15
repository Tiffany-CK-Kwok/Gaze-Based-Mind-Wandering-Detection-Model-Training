package mwdetection.base.abstracts.io;

import java.io.Serializable;

/**
 * This abstract class represents any object that we log and thus contains a timestamp.
 * <p>
 * Furthermore the class implements {@link Serializable} to be able to do store the child-objects as binaries if necessary.
 * <p>
 * Created by rudid on 05.04.2016.
 */
public abstract class Loggable implements Serializable {
    /**
     * Increase this counter if you change this class to "verify that the sender and receiver of a
     * serialized object have loaded classes for that object that are compatible with respect to
     * serialization".
     */
    private static final long serialVersionUID = 1L;

    /**
     * Each loggable objects has a timestamp.
     */
    protected long timestamp = 0;

    /**
     * The default constructor logs the current system time automatically to the local timestamp attribute.
     */
    public Loggable() {
        this.timestamp = System.currentTimeMillis();
    }

    // ---> GETTER/SETTER
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
