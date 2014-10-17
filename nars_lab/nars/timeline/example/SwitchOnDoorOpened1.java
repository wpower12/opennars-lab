/*
 * Copyright (C) 2014 sue
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nars.timeline.example;

import java.io.File;
import java.io.IOException;
import nars.core.NAR;
import nars.core.build.DefaultNARBuilder;
import nars.grid2d.TestChamber;
import nars.grid2d.operator.Activate;
import nars.grid2d.operator.Deactivate;
import nars.grid2d.operator.Goto;
import nars.grid2d.operator.Pick;
import nars.grid2d.operator.Say;
import nars.gui.NWindow;
import nars.gui.output.chart.TimeSeries.FirstOrderDifferenceTimeSeries;
import nars.io.TextInput;
import nars.io.TextOutput;
import nars.timeline.Timeline2DCanvas;
import nars.timeline.Timeline2DCanvas.*;
import nars.util.NARTrace;

/**
 *
 */
public class SwitchOnDoorOpened1 extends TimelineExample {
    
    public static void main(String[] args) throws Exception {
        int cycles = 3000;
        int inputDelay = 50;
        
        NAR nar = new DefaultNARBuilder().build();
        new TestChamber(nar, false);
        
        NARTrace t = new NARTrace(nar);
        
        TextInput i = new TextInput(new File("nal/TestChamber/TestChamberIndependentExperience/switch_on_door_opened.nal")) {
            int c = 0;
            @Override public Object next() throws IOException {
                if (c++ % 2 == 0)
                    return super.next();
                else
                    return inputDelay+ "\n";
            }
        };
        TextOutput o = new TextOutput(nar, System.out) {

            @Override
            public synchronized void output(Class channel, Object o) {
                if (channel == EXE.class)
                    super.output(channel, o);
            }
            
        }; 
        
        nar.addInput(i);
        nar.finish(cycles);
        
        System.out.println(t.time.size());

        new NWindow("_", new Timeline2DCanvas(
            new EventChart(t, true, false, false).height(3),
            new BarChart(new FirstOrderDifferenceTimeSeries("d(concepts)", t.charts.get("concept.count"))),
            
            new StackedPercentageChart(t, "concept.priority.hist.0", "concept.priority.hist.1", "concept.priority.hist.2", "concept.priority.hist.3").height(2),
            new LineChart(t, "concept.priority.mean").height(1),

            //new EventChart(t, false, true, false).height(3),
            
            new LineChart(t, "task.novel.add", "task.immediate_processed").height(3),
            new LineChart(t, "task.goal.process", "task.question.process", "task.judgment.process").height(3),
            new BarChart(t, "task.executed").height(3),
            new LineChart(t, "task.new.add").height(3),
            new LineChart(t, "emotion.busy").height(1)
            //new EventChart(t, false, false, true).height(3)
        )).show(800, 800, true);
    }
    
}
