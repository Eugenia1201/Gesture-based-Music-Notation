package reaction;

import graphicsLib.G;
import graphicsLib.I;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Gesture {
    public Shape shape;
    public G.VS vs;
    private Gesture(Shape shape, G.VS vs){ // factory methods
        this.shape = shape;
        this.vs = vs;
    } //factory methods can return nulls, and sometimes it is what you want.
    // whereas regular constructors has to return a new object -> it has to return a object.

    //Standard thing for factory pattern -> factory methods; Two reasons to build factory methods:
    // 1/not fail when return null 2/When you have cached examples that you don't want to build over and over again
    // to case 2: case when the outside world doesn't know that's been constructed
    // but the class itself knows what has been constructed (String Class works like that.)
    // Call factory methods (very like constructor methods)
    private static List UNDO = new List();
    public static Gesture getNew(Ink ink){ //can return null
        Shape s= Shape.recognize(ink);
        return (s==null)? null:new Gesture(s, ink.vs);
    }
    private void doGesture(){
        Reaction r = Reaction.best(this);
        if(r!=null){r.act(this);}
    }
    private void undoGesture(){
        Reaction r = Reaction.best(this);
        if(r!=null){
            UNDO.add(this);
            r.act(this);
        }
    }
    public static void undo(){
        if(UNDO.size() > 0){
            UNDO.remove(UNDO.size()-1); //remove the last element
            Layer.nuke(); //eliminates all the masses
            Reaction.nuke(); //clears byShape mapthen reloads initial reactions
            UNDO.redo();
        }
    }

    public static I.Area AREA = new I.Area(){
        //Java protects you from creating something without giving implementations for interfaces.
        //It's actually an anonymous class
        //to split up how the system is dealing with mouse
        public boolean hit(int x, int y){return true;}

        public void dn(int x, int y){Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y){Ink.BUFFER.drag(x,y);}
        public void up(int x, int y) {
            //when lift the mouse up, let go all the inks just leave the gesture that just has been recognized
            Ink.BUFFER.add(x, y);
            Ink ink = new Ink();
            Gesture g = Gesture.getNew(ink); //can fail if unrecognized
            Ink.BUFFER.clear();
            if (g != null) {
                if(g.shape.name.equals("N-N")){
                    undo();
                } else{
                    g.doGesture();
                }
            }
        }
    };
    //------------------------LIST--------------------------
    public static class List extends ArrayList<Gesture> {
        private void redo(){
            for(Gesture g: this){
                g.doGesture();
            }
        }
    }
}
