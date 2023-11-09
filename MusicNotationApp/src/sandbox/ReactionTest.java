package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static java.awt.SystemColor.window;

public class ReactionTest extends Window {
    static {new Layer("BACK"); new Layer("FORE");} //create the layers that his class will need

    public ReactionTest() {
        super("Simple Reaction Test", UC.mainWindowWidth, UC.mainWindowHeight);

        //Have a reaction to get everything started! initialReactions list to do that
        Reaction.initialReactions.addReaction(new Reaction ("SW-SW"){
            @Override
            public int bid(Gesture g) {return 0;}
            @Override
            public void act(Gesture g) {new Box(g.vs);}
        });
    }
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }
    public void mousePressed(MouseEvent me){Gesture.AREA.dn(me.getX(),me.getY()); repaint();}
    public void mouseDragged(MouseEvent me){Gesture.AREA.drag(me.getX(),me.getY()); repaint();}
    public void mouseReleased(MouseEvent me){Gesture.AREA.up(me.getX(),me.getY()); repaint();}

    public static void main(String[] args){
        PANEL = new PaintInk();
        Window.launch();
    }
    //------------------------------BOX---------------------------
    public static class Box extends Mass {
        public G.VS vs;
        public Color c = G.rndColor();
        public boolean isOval = false; //is to emphasize on the boolean type
        public Box(G.VS vs){
            super("BACK"); this.vs = vs;
            // passing the parameter name vs to match the class member variables G.VS vs (Shadowing)

            addReaction(new Reaction ("S-S"){
                public int bid(Gesture g){
                    //the class is embedded in the Box class
                    int x = g.vs.xM(), y=g.vs.yL(); //get the x,y from the Gesture (in order to get the x and y and locate the box)
                    if(Box.this.vs.hit(x,y)){ //this class has access to the bounding box (the class it is defined in)
                        //without Box.this. it is just referring to the reaction object upon which it is called. With Box.this it is able to refer to the box class
                        return Math.abs(x-Box.this.vs.xM()); // to deal with the case where two boxes is overlapped, the one with further distance is deleted
                    } else {
                        return UC.noBid;
                    }
                }
                @Override
                public void act(Gesture g) {
                    Box.this.delete();
                }
            });
            // Static: no relationship to this class, it's a helper
            // Non-static nested class//inner classes (vs. outer classes)
            // Top-level class vs. nested class
            // A non-static nested class has full access to the members of the class within which it is nested.
            // A static nested class does not have a reference to a nesting instance, so a static nested class cannot
            // invoke non-static methods or access non-static fields of an instance of the class within which it is nested.

            addReaction(new Reaction ("DOT"){
                public int bid(Gesture g){
                    //the class is embedded in the Box class
                    int x = g.vs.xM(), y=g.vs.yL(); //get the x,y from the Gesture (in order to get the x and y and locate the box)
                    if(Box.this.vs.hit(x,y)){ //this class has access to the bounding box (the class it is defined in)
                        //without Box.this. it is just referring to the reaction object upon which it is called. With Box.this it is able to refer to the box class
                        return Math.abs(x-Box.this.vs.xM()); // to deal with the case where two boxes is overlapped, the one with further distance is deleted
                    } else {
                        return UC.noBid;
                    }
                }
                @Override
                public void act(Gesture g) {
                    Box.this.c = G.rndColor();
                }
            });

            addReaction(new Reaction ("E-E"){
                public int bid(Gesture g){
                    //the class is embedded in the Box class
                    int x = g.vs.xL(), y=g.vs.yM();
                    if(Box.this.vs.hit(x,y)){
                        return Math.abs(x-Box.this.vs.xM());
                    } else {
                        return UC.noBid;
                    }
                }
                @Override
                public void act(Gesture g) {
                    Box.this.isOval = !Box.this.isOval;
                } //to negate/toggle on and off
            });
        }
        public void show(Graphics g){
            if(isOval){
                g.setColor(c);
                g.fillOval(vs.loc.x, vs.loc.y, vs.size.x, vs.size.y);
            }else {
                vs.fill(g, c);
            }
        }
    }
}
