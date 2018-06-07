package org.opennars.lab.sensor;

import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.plugin.Plugin;
import org.opennars.storage.Memory;

public class AutoMLPlugin implements Plugin {

    public EventEmitter.EventObserver obs;

//    protected SensorFrame[] keyframes;


    @Override
    public boolean setEnabled(Nar nar, boolean b) {
        final Memory memory = nar.memory;

        if(obs == null){
            obs = (event, a) -> {
                if(event == FrameEvents.FrameArrived.class){
                    // Process frame.
                }

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
                }

            };
            memory.event.set(obs, b, FrameEvents.FrameArrived.class,
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
