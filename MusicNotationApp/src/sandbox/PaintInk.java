package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Ink;
import reaction.Shape;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    //static {inkList.add(new Ink());} //add one single ink item so we can test showing a list
    public static String recognized = "";
    public PaintInk(){
        super("PaintInk", UC.mainWindowWidth, UC.mainWindowHeight);
    }

    public void paintComponent(Graphics g){
        G.fillBack(g);
        inkList.show(g);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        g.drawString("points:"+Ink.BUFFER.n, 600, 30);
        g.drawString(recognized, 700, 40);

        //Two shapes that are supposed to be the same should be close to one another
        if(inkList.size()>1){
            int last = inkList.size()-1;
            int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm);//compute the distance between last 2 norms
            g.setColor(dist>UC.noMatchDist? Color.RED: Color.BLACK);
            g.drawString("Dist: " + dist, 600,60);
        }
        pList.show(g);//when creating a method, specifying the type(Graphics g); when actual calling just calling on the name (g)

    }
    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Ink ink = new Ink(); //Introducing a local variable
        Shape s = Shape.recognize(ink); recognized = "Recog:" + ((s!=null)?s.name : "UN-RECOGNIZED");
        Shape.Prototype proto;
        inkList.add(ink);
        //Ink.BUFFER.up(me.getX(), me.getY());
        if(pList.bestDist(ink.norm) < UC.noMatchDist){ //we found a match so blend
            //Shape.Prototype.List.bestMatch.blend(ink.norm);
            proto = pList.bestMatch;
            proto.blend(ink.norm); //replace the norm that was in the ink object with the norm from a prototype.
        } else {
            proto = new Shape.Prototype();
            pList.add(proto); // new prototype
        }
        ink.norm = proto;
        repaint();
    }

    public static void main(String[] args){
        PANEL = new PaintInk();
        Window.launch();
    }
}

// Transform: using scaling factor to proportionally scale up/down a certain shape;
// A shape is essentially a collection of vector points (with relevant distance to each other)
// Bounding Box : give you the rightmost/leftmost/highest/lowest point of a shape. What is the extends of a particular shape.
// Feature extraction: throwing away the stuff you don't need
// Neural nets figure out what the features/prototype are, saving programmer from doing feature extraction manually.
// isomorphic scale vs. anisomorphic scale 同构的 vs. 异构的