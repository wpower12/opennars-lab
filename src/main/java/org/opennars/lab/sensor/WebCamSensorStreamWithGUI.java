package org.opennars.lab.sensor;

import org.opennars.gui.NARSwing;
import org.opennars.main.Nar;

/**
 * Example of a webcam interfacing with the system.
 *
 */

public class WebCamSensorStreamWithGUI {

    static Nar reasoner;

    public WebCamSensorStreamWithGUI(){
        reasoner = new Nar();
        reasoner.addPlugin(new AutoMLPlugin());
        reasoner.addPlugin(new WebCamSensorStream());

        NARSwing gui = new NARSwing(reasoner);
        reasoner.start(0);
    }

    public static void main(String args[]) {
        NARSwing.themeInvert();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WebCamSensorStreamWithGUI();
            }
        });
    }
}
