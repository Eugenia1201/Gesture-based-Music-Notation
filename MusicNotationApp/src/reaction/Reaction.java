package reaction;

import graphicsLib.I;
import graphicsLib.UC;

import java.util.*;

import static java.lang.reflect.Array.get;
import static javax.swing.UIManager.put;

public abstract class Reaction implements I.React {
    public Shape shape;
    private static Map byShape = new Map(); //do the reactions by shape
    public static List initialReactions = new List();
    public Reaction(String shapeName){
        shape = Shape.DB.get(shapeName);
        if(shape == null) {System.out.println("WTF? - Shape DB does not have: " + shapeName);}
    }
    public void enable(){
        //put it in the shape list if it's not already there
        List list = byShape.getList(shape);
        if(!list.contains(this)){
            list.add(this);
        }
    }
    public void disable(){
        List list = byShape.getList(shape);
        list.remove(this);
    }
    public static void nuke(){ //used to reset for UNDO
        byShape = new Map(); //throw away all the reactions  (The only one is not connected to anything anymore and eventually garbage collection will take care of it)
        initialReactions.enable();//enable insures that that reaction is in the byShape Map
    }
    public static Reaction best(Gesture g){return byShape.getList(g.shape).lowBid(g);}

    // ----------------------------LIST------------------------------
    public static class List extends ArrayList<Reaction> {
        //Adding and removing is done to TWO lists, the one in a Mass and the one in the byShape Map
        public void addReaction(Reaction r){add(r); r.enable();}
        public void enable(){for(Reaction r: this){r.enable();}}
        public void removeReaction(Reaction r){remove(r); r.disable();}
        // this next routine is tricky - to avoid concurrent array mods you first remove all from shape map, then clear
        public void clearAll(){for(Reaction r : this){r.disable();} this.clear();}
        //concurrent modification problem; the other way (removing first) breaking the way the iterator going through
        //the whole list,which will throw an exception
        public Reaction lowBid(Gesture g){ // can return null - list is Empty or no one wants to bid.
            Reaction res = null;
            int bestSoFar = UC.noBid;
            for(Reaction r : this){
                int b = r.bid(g);
                if(b < bestSoFar){
                    bestSoFar = b;
                    res = r;}
            }
            return res;
        }
    }
    // ------------------------------MAP------------------------------
    public static class Map extends HashMap<Shape, List> {
        public List getList(Shape s){ //always succeeds
            List res = get(s);
            if(res == null){
                res = new List();
                put(s, res);} //put something into a map
            return res;
        }
    }
}
