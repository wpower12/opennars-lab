/**
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
package org.opennars.lab.grid2d.main;

import static org.opennars.lab.grid2d.main.Hauto.DOWN;
import static org.opennars.lab.grid2d.main.Hauto.DOWNLEFT;
import static org.opennars.lab.grid2d.main.Hauto.LEFT;
import static org.opennars.lab.grid2d.main.Hauto.RIGHT;
import static org.opennars.lab.grid2d.main.Hauto.UP;
import static org.opennars.lab.grid2d.main.Hauto.UPLEFT;
import static org.opennars.lab.grid2d.main.Hauto.UPRIGHT;

/**
 * Defines an action that may or may not be allowed by the game engine.
 * A corresponding Effect will be returned to the agent's buffer
 */
abstract public class Action {
    
    long createdAt; //when created
    int expiresAt = -1; //allows an agent to set a time limit on the action
    

    public Effect process(Grid2DSpace p, GridAgent a) { return null; }

    //generates a string that can be inserted into a NARS judgment
    abstract public String toParamString();
    
    
    
    public static class Forward extends Action {
        public final int steps;
        public Forward(int steps) { this.steps = steps;        }        

        @Override
        public String toParamString() {
            return "n" + steps;
        }
        
        
        
        /** rounds to the nearest cardinal direction and moves. steps can be postive or negative */
        @Override public Effect process(Grid2DSpace p, GridAgent a) {
            int tx = a.x;
            int ty = a.y;
            int heading = a.heading;
            
            boolean allowDiagonal = false;
            switch (heading) {
                case LEFT: tx-=steps; break;
                case RIGHT: tx+=steps; break;
                case UP: ty+=steps; break;
                case DOWN: ty-=steps; break;
                default:
                    if (allowDiagonal) {
                        switch (heading) {
                            case UPLEFT: tx-=steps; ty+=steps; break;
                            case UPRIGHT: tx+=steps; ty+=steps; break;
                            case DOWNLEFT: tx-=steps; ty-=steps; break;
                            //case DOWNRIGHT: x+=steps; y+=steps;  break;
                        }
                    }
                    break;                
            }
            
            Effect e = p.getMotionEffect(a, this, a.x, a.y, tx, ty);
            if (e.success) {
                a.x = tx;
                a.y = ty;
            }
            return e;
        }
    
        //public void forward(int angle, int steps, boolean allowDiagonal) {    }
    
    }
    
    public static class Turn extends Action {
        public final int angle;
        public Turn(int angle) { this.angle = angle;        }

        @Override
        public String toParamString() {
            return "n" + angle;
        }
        
        @Override
        public Effect process(Grid2DSpace p, GridAgent a) {
            a.heading = angle;
            return new Effect(this, true, p.getTime());
        }
        
        
    }
    public static class Pickup extends Action {
        public final Object o;
        
        @Override public String toParamString() {
            return o.getClass().getSimpleName();
        }
        
        public Pickup(Object o) { this.o = o;        }        
    }
    public static class Drop extends Action {
        public final Object o;

        @Override public String toParamString() {
            return o.getClass().getSimpleName();
        }
        
        public Drop(Object o) { this.o = o;        }        
    }
    public static class Door extends Action {
        public final int x, y;
        public final boolean open;

        @Override public String toParamString() {
            return String.valueOf(open) + ", n" + x + ", n" + y;
        }
        
        public Door(int x, int y, boolean open) { this.x = x;  this.y = y;  this.open = open; }
    }

    

}
