package org.opennars.lab.sensor;

import java.util.ArrayList;

public class SensorStreamSummary<T> {

    // buffer of recent frames - to be purged between cycles?
    ArrayList buffer;
    // background frame - TODO - figure out what I mean by this. Can this be abstracted, or done on a stream by stream basis?

    // list of candidate terms and their supporting frames

    // list of results - IF this stream has learned models assocaited with them.

    public SensorStreamSummary(){
        buffer = new ArrayList();
    }

    public void addFrameToBuffer(Object o) {
        buffer.add(o);
    }

    public void clearBuffer() {
        buffer.clear();
    }
}
