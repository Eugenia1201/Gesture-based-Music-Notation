package graphicsLib;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;

//G is the Generic bucket where we put functions that are so simple that they can be reused in many places
public class G {
    public static Random RND = new Random();
    public static int rnd(int max) {return RND.nextInt(max);}
    public static Color rndColor() {return new Color(rnd(256), rnd(256), rnd(256));}
    public static void fillBack(Graphics g) {g.setColor(Color.WHITE); g.fillRect(0, 0, 5000, 5000);}
    public static void drawCircle(Graphics g, int x, int y, int r){
        g.drawOval(x-r,y-r,r+r, r+r);
    }

    // ---------------------------------------------VECTOR-----------------------------------------------------------------
    public static class V implements Serializable {
        public int x, y; //x,y coordinates
        public V(int x, int y) {this.set(x, y);}
        public V (V v) {this.set(v);}//copy existing V
        public void set(int x, int y) { this.x = x; this.y = y;}
        public void set(V v){x = v.x; y = v.y;}
        public void add(V v){x += v.x; y += v.y;}
        public int tx(){return x*T.n/T.d + T.dx;} //Scaling factor and translating factor; "/" is integer division
        //in integer division 5*(2/3) vs. 5*2/3 is different -> 3 vs. 0
        // -> do the multiple first to get the biggest possible number then do the division later
        public int ty(){return y*T.n/T.d + T.dy;}
        //helpers such as .tx() and .ty() allow setT to apply the Transform T to the single point V.
        public void blend(V v, int k){
            set((k*x + v.x)/(k+1), (k*y + v.y)/(k+1)); //Once do it for a single V, we can di oit for every V in the list of points in a Norm
        } //taking a single value and blend in an existing avg. value
        public static Transform T = new Transform();
        public void setT(V v){set(v.tx(), v.ty());}

        // ----------------------------------------------TRANSFORM--------------------------------
        // Two geometric operations: Scaling(multiplication) and Translating(addition of coordinates)
        // The purpose of transforms is to transform vectors we will make this a nested class of V
        public static class Transform{
            public Transform(){}
            private int dx=0, dy=0, n=1, d=1; // dx for delta x, n for numerator, d for denominator
            private void setScale(int oW, int oH, int nW, int nH){
                n = Math.max(nW, nH);
                d = Math.max(oW, oH);
                d = (d==0)? 1:d;
            }
            public int setOff(int oX, int oW, int nX, int nW){
                return (-oX - oW/2) * n/d + nX +nW/2;
            }
            public void set(VS oVS, VS nVS){
                //setting up the scale factor
                setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
                dx = setOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
                dy = setOff(oVS.loc.y, oVS.size.y, nVS.loc.y, nVS.size.y);
            }
            public void set(BBox from, VS to){
                setScale(from.h.size(), from.v.size(), to.size.x, to.size.y);
                dx = setOff(from.h.lo, from.h.size(), to.loc.x, to.size.x);
                dy = setOff(from.v.lo, from.v.size(), to.loc.y, to.size.y);
            }
        }
    }
    //"isomorphic scaling" which uses a single scale factor for BOTH x and y
    // the other is call "anisomorphic scaling" which allows x to scale differently from y.

    // ----------------------------------------VECTOR & SIZE-------------------------------------------
    // Vector Size (Rectangles)
    public static class VS implements Serializable {
        public V loc, size;
        public VS(int x, int y, int w, int h) {loc = new V(x, y); size = new V(w, h);}
        public void fill(Graphics g, Color c) {
            g.setColor(c);
            g.fillRect(loc.x, loc.y, size.x, size.y);
        }
        public boolean hit(int x, int y) {
            return loc.x<=x && loc.y<=y && x<=(loc.x+size.x) && y<=(loc.y+size.y);
        }
        //Detect if a mouse click was within the bounds of the rectangle
        public int xL(){return loc.x;}
        public int xH(){return loc.x + size.x;}
        public int xM(){return loc.x + size.x/2;}
        public int yL(){return loc.y;}
        public int yH(){return loc.y + size.y;}
        public int yM(){return loc.y + size.y/2;}
    }

    // ---------------------------------------------LOHI--RANGE--------------------------------------------------------------
    // Two points sorted by lowest then highest; To keep track of the min x max x and min y and max y,
    // which represents the endpoints of a range of values
    // It's a helper function, mostly for building Bounding Boxes
    public static class LoHi implements Serializable{
        public int lo, hi;
        public LoHi(int min, int max){
            lo = min;
            hi = max;
        }
        public void set(int x) { lo = x; hi = x;} //only use comma when declaring the variable.
        public void add(int x){
            if(x < lo) {lo = x;}
            if(x > hi) {hi = x;}
        }

        public int size(){
            return (hi-lo == 0) ? 1 : (hi-lo); //conditional expression in Java; initially C found ? useful
        }
        public int constrain(int v){
            if(v<lo) {return lo;} else return(v<hi)? v:hi;
        }
    }

    // ---------------------------------------------BBox--------------------------------------------------------------
    // Bounding Box (rectangle just to bound the box with lowest/highest etc.)
    public static class BBox implements Serializable {
        public LoHi h,v; //h for horizontal range; v for vertical range
        public BBox(){
            h = new LoHi(0,0);
            v = new LoHi(0,0);
        }
        public void set(int x, int y) {
            h.set(x);
            v.set(y);
        }
        public void add(int x, int y){
            h.add(x);
            v.add(y);
        }
        public void add(V v){
            add(v.x,v.y); //unpack the value of v;
        }
        //Taking a bounding box and repack it as a VS(rectangle)
        public VS getNewVS(){
            return new VS(h.lo, v.lo, h.size(), v.size());
        } //mostly for debugging
        public void draw(Graphics g){
            g.drawRect(h.lo, v.lo, h.size(), v.size());
        }
    }


    // ---------------------------------------------PL----------------------------------------------------------------
    // PolyLine, just a sequence of xy-coordinates drawn on screen from mouse down to mouse up
    public static class PL implements Serializable {
        public V[] points; //we keep an array of points

        public PL (int count){
            points = new V[count];    //create an array big enough to hold counts and set them to null
            for(int i = 0; i < count; i++){
                points[i] = new V(0, 0); //populate it with V objects
            }
        }
        public int size(){
            return points.length;
        }
        public void drawN(Graphics g, int n){
            for(int i = 1; i < n; i++){
                g.drawLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y);
            } //used to draw an initial portion of the full array
            drawNDots(g, n); // when comment out, the actual drawing won't have any dots in it.
        }
        public void draw(Graphics g){drawN(g, size());
        }//draw the whole array
        public void drawNDots(Graphics g, int n){
            g.setColor(Color.BLUE);
            for(int i=0; i<n; i++){
                drawCircle(g,points[i].x, points[i].y,4);
            } //Top-down programming: calling functions that haven't been created yet. design from the top, creating functions later
            // good to save the thing from forgetting writing helper function, coz can always go back and write them.
        }
        public void transform(){
            for (V point : points) {
                point.setT(point);
            }
        }
    }
}

//whereas bottom-up easy for testing, Top-down allows to build the building blocks first and add in helpers later
//modern programming is a combination of both styles.
//Reaction Architecture: the framework that will help us build gesture-based applications - 28 classes of helper functions
//Context decisions should be done in a probabilistic manner.
//The one simple notion - using a distance metric for all your context evaluation and allowing the best context match to win the rights
//to interpret the stroke - is the fundamental idea that underlies the reaction architecture.

//Mass
//In Java term, each visible item is a Mass. Abstract class Mass implements I.Show. Showable items are generally masses.
//Each different mass is essentially a pile of reactions (these are state transitions that that single mass could possibly perform)
// that cloud of reactions (possible transition) are essentially the state of the mass, just hiding behind the scenes and waiting for an input character.
