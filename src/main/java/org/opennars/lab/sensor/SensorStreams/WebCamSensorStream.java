package org.opennars.lab.sensor.SensorStreams;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.opennars.lab.sensor.SensorEvents;
import org.opennars.storage.Memory;

/**
 * Draft
 *
 * The collection of frames will be done in response to the start of an
 * inference cycle. An event observer will wait for the notice, and when
 * found the callback will grab a frame, package it, and emit an event.
 *
 * The event can then be captured by the autoML plugin.
 */

public class WebCamSensorStream extends SensorStreamPlugin {

    private FrameGrabber grabber;
    private CanvasFrame frame;

    public WebCamSensorStream(String n){
        try {
            name = n;
            grabber = FrameGrabber.createDefault(0);
            grabber.start();
            frame = new CanvasFrame("Camera Feed", CanvasFrame.getDefaultGamma()/grabber.getGamma());
        } catch (FrameGrabber.Exception e) {
            System.out.println("No Camera Found");
            grabber = null;
        }
    }

    // TODO - need to figure out how to call this when the gui dies.
    public void close(){
        frame.dispose();
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Part of the SensorStreamPlugin Interface.
     *
     * SensorStreams need to dictate how they grab their frame.
     * Also a good place to do things like paint a canvas.
     *
     * @param memory
     */
    @Override
    void emitFrame(Memory memory) {
        if(grabber != null){
            try {
                Frame f = grabber.grab();
                frame.showImage(f);

                // Sending the frame itself as an optional value.
                memory.event.emit(SensorEvents.FrameArrived.class, name, f);

            } catch (FrameGrabber.Exception e) {
                // blarg.
            }
        }
    }

    @Override
    public CharSequence name() {
        return "Web Camera Sensor Stream";
    }

}
