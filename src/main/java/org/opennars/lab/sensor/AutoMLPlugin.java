package org.opennars.lab.sensor;

import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.plugin.Plugin;
import org.opennars.storage.Memory;

import java.util.HashMap;

/**
 * Reasoner-guided automatic machine learning.
 *
 * Attempts to build simple frame-to-label classifiers for generic streams of data from arbitrary sensors.
 *
 * Must be loaded BEFORE any SensorStreamPlugins
 */
public class AutoMLPlugin implements Plugin {

    public EventEmitter.EventObserver obs;

    protected HashMap<String, SensorStreamSummary> streams;

    public AutoMLPlugin(){
        streams = new HashMap<>();
    }

    @Override
    public boolean setEnabled(Nar nar, boolean b) {
        final Memory memory = nar.memory;

        if(obs == null){
            obs = (event, a) -> {
                System.out.println(a);
                // New SensorStream plugin loaded - emitted by the loaded plugin.
                if(event == SensorEvents.SensorStreamAdded.class){
                    // a = [String name]
                    String name = (String)a[0];
                    SensorStreamSummary sss = new SensorStreamSummary();
                    streams.put(name, sss);
                }

                // When frames arrive they are just stored in the appropriate buffer.
                if(event == SensorEvents.FrameArrived.class){
                    // a = [String name, Object frame] - Need reflection on the frame type? idk.
                    String name = (String)a[0];

                    if(streams.containsKey(name)){
                        SensorStreamSummary s = streams.get(name);
                        s.addFrameToBuffer(a[1]);
                    } else {
                        streams.put(name, new SensorStreamSummary());
                    }
                }

                // Clear buffers at the end of a multi-cycle
                if(event == Events.CyclesEnd.class){
                    for(SensorStreamSummary s: streams.values()){
                        s.clearBuffer();
                    }
                }

                // the core logic happens when Tasks, Terms, Concepts are manipulated.
                // Being processed during the same multi-cycle will be assumed to indicate
                // that w.e is being processed is a candidate to be associated with the frames
                // currently in all buffers.
                if(event == Events.TaskAdd.class){
                    // NOTE - Note sure what events to watch yet.
                    // I know I want to react when a new task is added,
                    // but i'm not sure if that is the right time.

                    // The perfect place would be whenever I could say
                    // "this chunk of input can all be associated with this frame."

                    // If i'm already using the granularity of the CyclesStart/End, I could
                    // use that as the event.

                    // Is there a way to find all terms that have been 'touched' during a
                    // multi-cycle inference?

                    // For now, assuming that the system is taking input from the language system.
                    // That means there should be plenty of concepts being created. Or added to.

                    // when this happens, the frames should be added to them?
                }

            };
            memory.event.set(obs, b, SensorEvents.FrameArrived.class,
                                     Events.TaskAdd.class,
                                     Events.BeliefReason.class);
        }
        return b;
    }

    @Override
    public CharSequence name() {
        return "Auto Ml";
    }
}
