package reaction;

import graphicsLib.G;
import graphicsLib.I;
import graphicsLib.UC;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class Shape implements Serializable {
    //A shape is just a concept like the letter "a" or "L". There may be several different ways to draw a single shape so it is a list of Norms
    public static Database DB = Database.load();
    public static Collection<Shape> shapeList = DB.values();// this collection backed by DB, so changes to DB show up here
    public static Shape DOT = DB.get("DOT");
    public Prototype.List prototypes = new Prototype.List();
    public String name;
    public Shape(String name){
        this.name = name;
    }//Constructor
    public static void saveShapeDB(){
        Database.save();
    }
    public static Shape recognize(Ink ink){
        //can return null
        if(ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDist; //assume no match
        for (Shape s: shapeList){
            int d = s.prototypes.bestDist(ink.norm);
            if(d < bestSoFar){
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    // -----------------------------------PROTOTYPE----------------------------------
    public static class Prototype extends Ink.Norm implements Serializable {
        // A single Prototype is basically a Norm
        int nBlend = 1; // for doing the blending average
        public void blend(Ink.Norm norm){
            // take a single new input Norm and compare it distance-wise to all the prototypes
            // in all the shapes that we have and find the best match
            blend(norm, nBlend);  // this blend function is calling on Ink.Norm
            nBlend++;
        }
        // ----------------------------------PROTOTYPE.LIST-------------------------------------
        public static class List extends ArrayList<Prototype> implements I.Show, Serializable{
            public static Prototype bestMatch; // side effect when running bestDist
            //how close the best match distance was;
            //what are the two objects that are the best matches (which prototypes giving the best match)
            private static int m=10, w=60; //m for margin
            private static G.VS showBox = new G.VS(m, m, w, w);

            //A piece of debugging function
            @Override
            public void show(Graphics g) { //draw a list of boxes across top of screen
                g.setColor(Color.ORANGE);
                for(int i =0; i<size(); i++){
                    Prototype p = get(i);
                    int x = m+i*(m+w);
                    showBox.loc.set(x,m);
                    p.drawAt(g,showBox);
                    g.drawString(" " + p.nBlend, x,20);} //cheapest way to convert an int to a string -> creating an empty one and add number to it
            }

            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int bestSoFar = UC.noMatchDist; //assume no match
                for(Prototype p: this){
                    int d=p.dist(norm);
                    if(d<bestSoFar){
                        bestMatch = p;
                        bestSoFar = d;
                    }
                }
                return bestSoFar;
            }

            public void train(Ink.Norm norm){
                if(bestDist(norm) < UC.noMatchDist){ //we found a match so blend
                    bestMatch.blend(norm);
                } else {
                    add(new Shape.Prototype());// didn't match so add a new one(from Ink.BUFFER)
                }
            }
            public int showNdx(int x){ return x/(m+w);} //Ndx -> index
            public boolean isShowDelete(G.VS vs){
                return vs.loc.y < m+w && showNdx(vs.loc.x) < size();
            }

            public void showDelete(G.VS vs){remove(showNdx(vs.loc.x));}
        }
    }
    //--------------------------------------DATABASE-------------------------------
    public static class Database extends HashMap<String, Shape> implements Serializable{
        private Database() {
            super();
            String dot = "DOT";
            put(dot, new Shape(dot)); //make sure DOT exist
        }
        private static Database load(){
            Database db = null; //db is a local variable
            try{
                System.out.println("Attempting DB load...");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.ShapeDbFileName));
                db = (Database) ois.readObject(); //() for casting
                System.out.println("successful load");
                ois.close();
            }catch(Exception e){
                System.out.println("load fail \n" + e);
                db = new Database(); //use default empty if load failed
            }
            return db;
        }

        public static void save(){
            String filename = UC.ShapeDbFileName;
            try{
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
                oos.writeObject(DB);
                System.out.println("Saved: " + filename);
                oos.close();
            } catch(Exception e){
                System.out.println("Database save failed \n" + e);
            }
        }
        private Shape forceGet(String name){ //always returns Shape
            if(!DB.containsKey(name)) {
                DB.put(name, new Shape(name));
            }
            return DB.get(name);
        }
        public void train(String name, Ink.Norm norm){
            if(isLegal(name)){
                forceGet(name).prototypes.train(norm);
            }
        }
        public static boolean isLegal(String name) {
            return !name.isEmpty() && !name.equals("DOT");//we don't want to train a null or a dot
        }

    }
}
