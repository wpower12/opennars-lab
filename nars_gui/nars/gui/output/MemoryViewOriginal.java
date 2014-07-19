package nars.gui.output;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.WeakHashMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import nars.core.NAR;
import nars.entity.Concept;
import nars.entity.Sentence;
import nars.entity.Task;
import nars.gui.NSlider;
import nars.language.*;
import nars.storage.ConceptBag;
import nars.storage.Memory;
import processing.core.*;
import nars.gui.Window;


class mvoapplet extends PApplet  //(^break,0_0)! //<0_0 --> deleted>>! (--,<0_0 --> deleted>>)!
{

///////////////HAMLIB
//processingjs compatibility layer
    int mouseScroll = 0;

    ProcessingJs processingjs = new ProcessingJs();
//Hnav 2D navigation system   
    Hnav hnav = new Hnav();
//Object
    float selection_distance = 10;
    public float maxNodeSize = 50f;

    Hsim hsim = new Hsim();

    Hamlib hamlib = new Hamlib();

    Vertex lastclicked = null;

    public Button getBack;
    public Button conceptsView;
    public Button memoryView;
    public Button fetchMemory;
    Memory mem = null;

    public int mode = 0;
    
    boolean showBeliefs = false;
    
    float nodeSpeed = 0.1f;
    
    float sx = 800;
    float sy = 800;

    ArrayList<Edge> E = new ArrayList<Edge>();
    ArrayList<Edge> E2 = new ArrayList<Edge>();
    ArrayList<Edge> E3 = new ArrayList<Edge>(); //derivation chain
    ArrayList<Sentence> Sent_s = new ArrayList<Sentence>(); //derivation chain
    ArrayList<Integer> Sent_i = new ArrayList<Integer>(); //derivation chain
    long lasttime = -1;

    boolean autofetch = true;
    private int MAX_UNSELECTED_LABEL_LENGTH = 32;
    private boolean updateNext;
    float nodeSize = 10;
    WeakHashMap<Vertex,float[]> vertexPoints = new WeakHashMap();

    public void mouseScrolled() {
        hamlib.mouseScrolled();
    }

    public void keyPressed() {
        hamlib.keyPressed();
    }

    public void mouseMoved() {
        hamlib.mouseMoved();
    }

    public void mouseReleased() {
        hamlib.mouseReleased();
    }

    public void mouseDragged() {
        hamlib.mouseDragged();
    }

    public void mousePressed() {
        hamlib.mousePressed();
    }

    @Override
    public void draw() {
        take_from_mem();
        hamlib.Update(128, 138, 128);
    }

    void hsim_ElemClicked(Vertex i) {
        lastclicked = i;
    }

    void hsim_ElemDragged(Vertex i) {
    }

    void hrend_DrawBegin() {
    }

    void hrend_DrawEnd() {
        //fill(0);
        //text("Hamlib simulation system demonstration", 0, -5);
        //stroke(255, 255, 255);
        //noStroke();
        if (lastclicked != null) {
            fill(255, 0, 0);
            ellipse(lastclicked.x, lastclicked.y, 10, 10);
        }
    }

    public void hrend_DrawGUI() {
    }

    @Override
    public void setup() {  
        //size((int) sx, (int) sy);
                
        hsim.obj = new ArrayList<Vertex>();
        E = new ArrayList<Edge>();
        E2 = new ArrayList<Edge>();
        Sent_s = new ArrayList<Sentence>(); //derivation chain
        Sent_i = new ArrayList<Integer>(); //derivation chain

        background(0);
        
    }

    public void hsim_Draw(Vertex oi) {
        text(oi.name.toString(), oi.x, oi.y);
    }

    
    //returns the current vertex position (not the target)
    public float[] vertexPosition(Vertex e) {
        float[] p = vertexPoints.get(e);
        if (p == null) {
            p = new float[2];
            vertexPoints.put(e, p);
        }
        return p; 
    }
    
    public void drawit() {
        
        
        long currentTime = mem.getTime();
        
        final ArrayList<Vertex> V = hsim.obj;
        
        background(0, 0, 0);
        
        stroke(13, 13, 4);
        strokeWeight(linkWeight);

        
        for (Vertex elem : V) {
            float[] p = vertexPosition(elem);
            p[0] = ( p[0] * (1.0f - nodeSpeed) + elem.x * (nodeSpeed) );
            p[1] = ( p[1] * (1.0f - nodeSpeed) + elem.y * (nodeSpeed) );
        }
        
        
        final float sizzfloat = (float) E2.size();
        
        for (int i = 0; i < E2.size(); i++) {
            float sizzi = (float) i;

            final Edge lin = E2.get(i);
            final Vertex elem1 = V.get(lin.from);
            float[] p1 = vertexPosition(elem1);
            final Vertex elem2 = V.get(lin.to);
            float[] p2 = vertexPosition(elem1);
            
            fill(225, 225, 225, 50); //transparent

            float mul = 0f;
            try {
                if (mem.currentBelief != null && (elem1.name.containTerm(mem.currentBelief.getContent()) || mem.currentBelief.getContent().containTerm(elem1.name))) {
                    ellipse(p2[0], p2[1], 100, 100);
                    mul = 1.0f;
                }
                if (mem.currentBelief != null && (elem1.name.equals(mem.currentBelief.getContent()) || mem.currentBelief.getContent().equals(elem1.name))) {
                    ellipse(p2[0], p2[1], 200, 200);
                    mul = 1.0f;
                }
                if (mem.currentTask != null && (elem2.name.containTerm(mem.currentTask.getContent()) || mem.currentTask.getContent().containTerm(elem2.name))) {
                    ellipse(p2[0], p2[1], 100, 100);
                    mul = 1.0f;
                }
                if (mem.currentTask != null && (elem2.name.equals(mem.currentTask.getContent()) || mem.currentTask.getContent().equals(elem2.name))) {
                    ellipse(p2[0], p2[1], 200, 200);
                    mul = 1.0f;
                }
            } catch (Exception ex) {
            }
            /*float addi = (128.0f + 64.0f) * mul;
            stroke(255.0f - sizzi / sizzfloat * 255.0f, 64 + addi - sizzi / sizzfloat * 255.0f, 255.0f - sizzi / sizzfloat * 255.0f, lin.alpha);*/
            stroke(200, 200, 200, lin.alpha);

            //ellipse(elem1.x,elem1.y,10,10);
            line(p1[0], p1[1], p2[0], p2[1]);
        }
        
        fill(255);
        

        for (int i = 0; i < E.size(); i++) {
            Edge lin = E.get(i);
            final Vertex elem1 = V.get(lin.from);
            float[] p1 = vertexPosition(elem1);            
            final Vertex elem2 = V.get(lin.to);
            float[] p2 = vertexPosition(elem2);

            //ellipse(elem1.x,elem1.y,10,10);
            stroke(255, 255, 127, 127 + lin.alpha/2);
            line(p1[0], p1[1], p2[0], p2[1]);
        }
        
        stroke(127, 255, 255, 127);

        for (int i = 0; i < Sent_s.size(); i++) {
            final List<Term> deriv = Sent_s.get(i).getStamp().getChain();
            final Vertex elem1 = V.get(Sent_i.get(i));
            float[] p1 = vertexPosition(elem1);            
            
            for (int j = 0; j < Sent_s.size(); j++) {
                
                final Vertex elem2 = V.get(Sent_i.get(j));
                float[] p2 = vertexPosition(elem2);
                
                for (int k = 0; k < deriv.size(); k++) {
                    if (i != j && deriv.get(k) == Sent_s.get(j).getContent()) {
                        
                        line(p1[0], p1[1], p2[0], p2[1]);
                        break;
                    } 
               }
            }
        }
        
        strokeWeight(0);
        textSize(16);
        
        for (int i = 0; i < V.size(); i++) {
            Vertex elem = V.get(i);
            float[] p = vertexPosition(elem);            
            
            String suffix = ".";
            
            float rad = elem.name.getComplexity() * nodeSize;
            float age = elem.creationTime!=-1 ? currentTime - elem.creationTime : -1;
            
                    
            float ageFactor = age == -1? 0 : (1f/(age/100.0f+1.0f));
            int ai = (int)(100.0 * ageFactor);
            
            if (elem.type == 0) {
                fill( 155 + ai, 155 + ai, 155 + ai, 155+ai );
            } else {
                suffix = "?";
                fill(255, 255, 0, 155+ai);
            }
            
            if (suffix.equals("?")) {
                ellipse(p[0], p[1], rad, rad);
            } else {
                ellipse(p[0], p[1], rad, rad);
            }
            
            fill(255, 255, 255);
            
            String label = elem.name.toString() + suffix;
            if (elem != lastclicked) {
                if (label.length() > MAX_UNSELECTED_LABEL_LENGTH)
                    label = label.substring(0, MAX_UNSELECTED_LABEL_LENGTH-3) + "...";
                
            }
            text(label, p[0], p[1]);
            
        }
    }
    
    
    private static final float linkWeight = 4.0f;

    public void take_from_mem() {
        if ((mem.getTime() != lasttime) || (updateNext)) {
            updateNext = false;
            lasttime = mem.getTime();
            
            hsim.obj.clear();
            E.clear();
            E2.clear();
            Sent_s.clear(); //derivation chain
            Sent_i.clear(); //derivation chain
            
            int x = 0;
            int cnt = 0;
            ConceptBag bag = mem.concepts;
            for (int i = bag.levels; i >= 1; i--) {
                if (!bag.emptyLevel(i - 1)) {
                    if (!bag.emptyLevel(i-1)) {
                        try {
                            for (final Concept c : Collections.unmodifiableCollection(bag.getLevel(i-1))) {

                                final Term name = c.getTerm();


                                hsim.obj.add(new Vertex(x++, i, name, 0));
                                cnt++;

                                float xsave = x;

                                int bufcnt = cnt;

                                if (showBeliefs) {
                                    for (int k = 0; k < c.beliefs.size(); k++) {
                                        Sentence kb = c.beliefs.get(k);
                                        Term name2 = kb.getContent();

                                        hsim.obj.add(new Vertex(x++, i, name2, 0, kb.getStamp().creationTime));
                                        E.add(new Edge(bufcnt, cnt, kb.truth.getConfidence()));
                                        Sent_s.add(kb);
                                        Sent_i.add(cnt);
                                        cnt++;

                                    }
                                }

                                for (Task q : c.getQuestions()) {
                                    Term name2 = q.getContent();                            
                                    hsim.obj.add(new Vertex(x++, i, name2, 1));
                                    E.add(new Edge(bufcnt, cnt, q.getPriority()));
                                    cnt++;

                                }
                            }
                        }
                        catch (ConcurrentModificationException e) { }

                    }
                }
                
                if (mode == 1)
                    x = 0;

            }
            for (int i = 0; i < hsim.obj.size(); i++) {
                final Vertex ho = (Vertex)hsim.obj.get(i);
                
                for (int j = 0; j < hsim.obj.size(); j++) {
                    Vertex target = (Vertex) hsim.obj.get(j);
                    try {
                        if ((ho).name.containTerm((target.name))) {
                            int alpha = (ho.name.getComplexity() + target.name.getComplexity())/2;
                            alpha = alpha * 10;
                            alpha += 75;
                            if (alpha > 255) alpha = 255;
                            E2.add(new Edge(i, j, alpha));
                        }
                    } catch (Exception ex) {
                    }
                }
            }
            //autofetch=false;
        }
    }



    void setUpdateNext() {
        updateNext = true;
    }

    class ProcessingJs {

        ProcessingJs() {
            addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                    mouseScroll = -evt.getWheelRotation();
                    mouseScrolled();
                }
            }
            );
        }
    }

    class Hnav {

        private float savepx = 0;
        private float savepy = 0;
        private int selID = 0;
        private float zoom = 1.0f;
        private float difx = 0;
        private float dify = 0;
        private int lastscr = 0;
        private boolean EnableZooming = true;
        private float scrollcamspeed = 1.1f;

        float MouseToWorldCoordX(int x) {
            return 1 / zoom * (x - difx - width / 2);
        }

        float MouseToWorldCoordY(int y) {
            return 1 / zoom * (y - dify - height / 2);
        }
        private boolean md = false;

        void mousePressed() {
            md = true;
            if (mouseButton == RIGHT) {
                savepx = mouseX;
                savepy = mouseY;
            }
        }

        void mouseReleased() {
            md = false;
        }

        void mouseDragged() {
            if (mouseButton == RIGHT) {
                difx += (mouseX - savepx);
                dify += (mouseY - savepy);
                savepx = mouseX;
                savepy = mouseY;
            }
        }
        private float camspeed = 20.0f;
        private float scrollcammult = 0.92f;
        boolean keyToo = true;

        void keyPressed() {
            if ((keyToo && key == 'w') || keyCode == UP) {
                dify += (camspeed);
            }
            if ((keyToo && key == 's') || keyCode == DOWN) {
                dify += (-camspeed);
            }
            if ((keyToo && key == 'a') || keyCode == LEFT) {
                difx += (camspeed);
            }
            if ((keyToo && key == 'd') || keyCode == RIGHT) {
                difx += (-camspeed);
            }
            if (!EnableZooming) {
                return;
            }
            if (key == '-' || key == '#') {
                float zoomBefore = zoom;
                zoom *= scrollcammult;
                difx = (difx) * (zoom / zoomBefore);
                dify = (dify) * (zoom / zoomBefore);
            }
            if (key == '+') {
                float zoomBefore = zoom;
                zoom /= scrollcammult;
                difx = (difx) * (zoom / zoomBefore);
                dify = (dify) * (zoom / zoomBefore);
            }
        }

        void Init() {
            difx = -width / 2;
            dify = -height / 2;
        }

        void mouseScrolled() {
            if (!EnableZooming) {
                return;
            }
            float zoomBefore = zoom;
            if (mouseScroll > 0) {
                zoom *= scrollcamspeed;
            } else {
                zoom /= scrollcamspeed;
            }
            difx = (difx) * (zoom / zoomBefore);
            dify = (dify) * (zoom / zoomBefore);
        }

        void Transform() {
            translate(difx + 0.5f * width, dify + 0.5f * height);
            scale(zoom, zoom);
        }
    }

    public class Vertex {

        public float x;
        public float y;
        public final int type;
        public final Term name;
        private final long creationTime;

        public Vertex(int index, int level, Term Name, int Mode) {
            this(index, level, Name, Mode, -1);
        }

        @Override
        public int hashCode() {
            return name.hashCode() + type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Vertex) {
                Vertex o = (Vertex)obj;
                return (o.type == type) && (o.name.equals(name));
            }
            return false;
        }
        

        
        private int rowHeight = (int)(maxNodeSize);
        private int colWidth = (int)(maxNodeSize);
        
        public Vertex(int index, int level, Term term, int type, long creationTime) {
            
            if (mode == 1) {
                this.y = -200 - (index * rowHeight);
                this.x = 2600 - (level * colWidth);
            }
            else if (mode == 0) {
                float LEVELRAD = maxNodeSize;

                double radius = ((mem.concepts.levels - level)+1);
                float angle = index; //TEMPORARY
                this.x = (float)(Math.cos(angle/3.0) * radius) * LEVELRAD;
                this.y = (float)(Math.sin(angle/3.0) * radius) * LEVELRAD;
            }
            
            this.name = term;
            this.type = type;
            this.creationTime = creationTime;
        }
    }
    
////Object management - dragging etc.

    class Hsim {

        ArrayList obj = new ArrayList();

        void Init() {
            smooth();
        }

        void mousePressed() {
            if (mouseButton == LEFT) {
                checkSelect();
            }
        }
        boolean dragged = false;

        void mouseDragged() {
            if (mouseButton == LEFT) {
                dragged = true;
                dragElems();
            }
        }

        void mouseReleased() {
            dragged = false;
            selected = null;
        }
        Vertex selected = null;

        void dragElems() {
            if (dragged && selected != null) {
                selected.x = hnav.MouseToWorldCoordX(mouseX);
                selected.y = hnav.MouseToWorldCoordY(mouseY);
                hsim_ElemDragged(selected);
            }
        }

        void checkSelect() {
            double selection_distanceSq = selection_distance*selection_distance;
            if (selected == null) {
                for (int i = 0; i < obj.size(); i++) {
                    Vertex oi = (Vertex) obj.get(i);
                    float dx = oi.x - hnav.MouseToWorldCoordX(mouseX);
                    float dy = oi.y - hnav.MouseToWorldCoordY(mouseY);
                    float distanceSq = (dx * dx + dy * dy);
                    if (distanceSq < (selection_distanceSq)) {
                        selected = oi;
                        hsim_ElemClicked(oi);
                        return;
                    }
                }
            }
        }
    }

//Hamlib handlers
    class Hamlib {

        void Init() {
            noStroke();
            hnav.Init();
            hsim.Init();
        }

        void mousePressed() {
            hnav.mousePressed();
            hsim.mousePressed();
        }

        void mouseDragged() {
            hnav.mouseDragged();
            hsim.mouseDragged();
        }

        void mouseReleased() {
            hnav.mouseReleased();
            hsim.mouseReleased();
        }

        public void mouseMoved() {
        }

        void keyPressed() {
            hnav.keyPressed();
        }

        void mouseScrolled() {
            hnav.mouseScrolled();
        }

        void Camera() {
            hnav.Transform();
        }

        void Update(int r, int g, int b) {            
            background(r, g, b);
            //pushMatrix();
            Camera();
            hrend_DrawBegin();
            drawit();
            hrend_DrawEnd();
            //popMatrix();
            hrend_DrawGUI();
        }
    }

    public class Edge {

        public final int from;
        public final int to;
        public final int alpha;

        public Edge(final int from, final int to, int alpha) {
            this.from = from;
            this.to = to;
            this.alpha = alpha;
        }
        public Edge(final int from, final int to, float alpha) {
            this(from, to, (int)(255.0 * alpha));
        }
    }
}

public class MemoryViewOriginal extends Window {

    mvoapplet app = null;

    public MemoryViewOriginal(NAR n) {
        this(n.memory);
    }

    public MemoryViewOriginal(Memory mem) {
        super("\"NARS Concept Graph\"");


        app = new mvoapplet();
        app.init();
        app.mem = mem;
        
        this.setSize(1000, 860);//initial size of the window
        this.setVisible(true);

        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        final JComboBox modeSelect = new JComboBox();
        modeSelect.addItem("Circle");
        modeSelect.addItem("Grid");
        modeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modeSelect.getSelectedIndex() == 0) {
                    app.mode = 0;
                }
                else {
                    app.mode = 1;
                }
                app.setUpdateNext();
            }            
        });

        menu.add(modeSelect);
        
        final JCheckBox beliefsEnable = new JCheckBox("Beliefs");
        beliefsEnable.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                app.showBeliefs = (beliefsEnable.isSelected());        
                app.setUpdateNext();
            }
        });
        menu.add(beliefsEnable);
        
        NSlider nodeSize = new NSlider(app.nodeSize, 1, app.maxNodeSize) {
            @Override
            public void onChange(double v) {
                app.nodeSize = (float)v;
            }          
        };
        nodeSize.setPrefix("Node Size: ");
        nodeSize.setPreferredSize(new Dimension(125, 25));
        menu.add(nodeSize);

        content.add(menu, BorderLayout.NORTH);
        content.add(app, BorderLayout.CENTER);
        

    
    }

    @Override
    protected void close() {
        app.stop();
        app.destroy();
        app.exit();
        getContentPane().removeAll();
        app = null;
    }

    
    
    
}
