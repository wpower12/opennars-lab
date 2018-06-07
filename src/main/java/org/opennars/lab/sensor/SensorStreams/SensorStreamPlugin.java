package org.opennars.lab.sensor.SensorStreams;

import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.plugin.Plugin;
import org.opennars.storage.Memory;

abstract class SensorStreamPlugin implements Plugin {

    public EventEmitter.EventObserver obs;

    public boolean setEnabled(Nar nar, boolean b) {
        final Memory memory = nar.memory;

        if(obs == null){
            obs = (event, a) -> {
                // TODO - make more options for when/how the frames should be collected and emitted.
                if(event == Events.CycleStart.class){
                    emit(memory);
                }
            };
            memory.event.set(obs, b, Events.CycleStart.class);
        }
        return b;
    }

    abstract void emit(Memory memory);
}
