package reaction;

import graphicsLib.G;
import graphicsLib.I;
import graphicsLib.UC;

import java.awt.*;
import java.util.ArrayList;

public class Ink implements I.Show {
//This class is a place-holder for what we want to build; eventually it will be a class that keeps a list of ink objects that we can show out to the screen
    public static Buffer BUFFER = new Buffer();
    //public static final int K = UC.normSampleSize;// how many points we want in the subsampling
    //public static G.VS TEMP = new G.VS(100,100,100,100); //use for debugging

    public Norm norm;
    public G.VS vs;
    public Ink(){
//        super(K); //we need to call super to create the PL that is this Ink
//        BUFFER.subSample(this);
//        G.V.T.set(BUFFER.bBox,TEMP);
//        this.transform(); //this is the Ink, which is a PolyLIne
        norm = new Norm();
        vs = BUFFER.bBox.getNewVS(); // get the bounding box and wrapping in a single rectangle.
    }


    @Override
    public void show(Graphics g) {
        //stub code: print something out to see the wrong code
        //g.setColor(Color.RED);
        //g.fillRect(100, 100, 100, 100);
        //draw(g);
        g.setColor(UC.inkColor);
        norm.drawAt(g,vs);
    }
    // -----------------------------------------NORM------------------------------------------
    //Normalized Coordinate System
    //This is a PL that has been reduced to a normalized coordinate system so that comparison is possible
    public static class Norm extends G.PL{
        public static final int N = UC.normSampleSize, MAX = UC.normCoordMax; //the number of point in the polyline
        public static final G.VS NCS = new G.VS(0,0, MAX, MAX); // Normalised Coordinated System; Coordinate Box for transform
        public Norm(){
            super(N); // How big the poly-lines are
            BUFFER.subSample(this); //sub-sample the ink in the buffer down to 25 points and normalize them
            G.V.T.set(BUFFER.bBox,NCS);
            this.transform();
        }

        public void drawAt(Graphics g, G.VS vs){
            //Set up the transformer
            G.V.T.set(NCS,vs);
            for(int i = 1; i <N; i++){
                g.drawLine(points[i-1].tx(), points[i-1].ty(),points[i].tx(), points[i].ty());
            }
        }

        public int dist(Norm norm){ //built inside of Norm Class: one norm is this; the other is the norm we passed in
            int res = 0;
            for (int i=0; i<N; i++){
                int dx = points[i].x - norm.points[i].x, dy = points[i].y - norm.points[i].y; //computing the delta(Euclidean distance)
                res += dx*dx + dy*dy;
            }
            return res;
        }
        public void blend(Norm norm, int nBlend){
            for (int i =0; i< N; i++){
                points[i].blend(norm.points[i], nBlend);
            } //Now that a Norm can get blended, a prototype that knows its blend count can be blended.
        }

    }

    // ----------------------------------------LIST-----------------------------------
    public static class List extends ArrayList<Ink> implements I.Show {

        @Override
        public void show(Graphics g) {
            for (Ink ink : this) { // this refers to the list
                ink.show(g);
            }
        }
        //'this' is also a parameter to the function, the object itself this.
        //define functions with "parameter", when calling a function, passing in "values/arguments"
        //this is a parameter name referring the object calling this.
        //in this case, this refers to the calling objects which are lists
        //parameters are names, arguments are values -> no confusion!
    }

    // -------------------------------------BUFFER---------------------------------------
    // Buffer is used to hold sth for a while
    // Buffer: computer holds something for a while before they pass onto something else
    public static class Buffer extends G.PL implements I.Show, I.Area {
        public static final int MAX = UC.inkBufferMax; // maximum size of buffer
        public int n; // how many points are actually in the buffer
        public G.BBox bBox = new G.BBox(); //the actual bounding box for our actual drawing

        private Buffer() {
            super(MAX); //Super is always who we are extending
            // -> in order to construct a Buffer, we have to construct a super too
        } // create the PL with MAX points

        //We made this constructor private, indicating there going to be a singleton. We just need a single buffer.
        //we only want one of this class (the only reason to make a constructor PRIVATE!) my constructor is for me only.
        //We do not want other classes creating Ink.Buffers.

        public void show(Graphics g) {this.drawN(g, n);} //bBox.draw(g);} // Only draw existing points
        public void add(int x, int y) {
            if (n < MAX) {
                points[n++].set(x, y); //adds new point to buffer
                bBox.add(x,y);
            }
        } // adds new point to buffer
        public void clear() {
            n = 0;
        } //if written as int n = 0; meaning creating a local variable int n which is not what we intended.

        public boolean hit(int x, int y) {
            return true;
        } // any point COULD go into ink

        //pre-fixing ++n (self-increase first, use this value later)
        //post-fixing n++ (use the value first, then self-increase later)
        public void dn(int x, int y) {clear(); add(x, y); bBox.set(x,y);}//add first point and reset bbox

        public void up(int x, int y) {}

        public void drag(int x, int y) {add(x,y);bBox.add(x, y);}//add each point as it comes it
        public void subSample(G.PL pl){//Subsampling is the trick of taking some variable number of data points and
            // either throwing some of them out or adding some new ones in to get the number of data points to
            // a single exact number like 20.
            int K = pl.points.length;
            for(int i =0; i<K; i++){
                pl.points[i].set(points[i*(n-1)/(K-1)]); //nice feature of linear function; if it works at two points, then it's linear line
                //and it works on any other points on this line.
                //last point in the buffer: i = K-1 and result if n-1; starting point 0.
            }
        }



    }



}


