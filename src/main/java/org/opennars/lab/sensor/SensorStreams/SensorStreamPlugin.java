package org.opennars.lab.sensor.SensorStreams;

import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.lab.sensor.SensorEvents;
import org.opennars.main.Nar;
import org.opennars.plugin.Plugin;
import org.opennars.storage.Memory;

abstract class SensorStreamPlugin implements Plugin {

    public EventEmitter.EventObserver obs;
    public String name;

    public boolean setEnabled(Nar nar, boolean b) {
        final Memory memory = nar.memory;

        if(obs == null){
            obs = (event, a) -> {
                // TODO - make more options for when/how the frames should be collected and emitted.
                if(event == Events.CycleStart.class){
                    emitFrame(memory);
                }
            };
            memory.event.set(obs, b, Events.CycleStart.class);
            memory.event.emit(SensorEvents.SensorStreamAdded.class, name);
        }
        return b;
    }

    abstract void emitFrame(Memory memory);
}
