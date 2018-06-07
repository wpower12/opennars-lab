package org.opennars.lab.sensor;

import org.bytedeco.javacv.*;

import static org.bytedeco.javacpp.opencv_core.*;

public class CVDemo {
    public static void main(String[] args) throws Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, or OpenCV).
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage grabbedImage = converter.convert(grabber.grab());

        int width  = grabbedImage.width();
        int height = grabbedImage.height();

        CanvasFrame frame = new CanvasFrame("Camera Feed", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            frame.showImage(grabber.grab());
        }

        frame.dispose();
        grabber.stop();
    }
}
