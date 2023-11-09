package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Ink;
import reaction.Shape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends Window {
    public static final String UNKNOWN ="<- this name is currently unknown.";
    public static final String ILLEGAL = "<- this name is NOT a legal Shape name.";
    public static final String KNOWN = "<- this is a known shape.";
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public static String curName = ""; //the name that user types in
    public static String curState = ILLEGAL; //current name doesn't have anything in it

    public ShapeTrainer(){
        super("Shape Trainer", UC.mainWindowWidth, UC.mainWindowHeight);
    }

    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLACK);
        g.drawString(curName, 600,30);
        g.drawString(curState, 700,30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if(pList!=null){pList.show(g);} //to Guard the drawing of the pList - because it could be null
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
        Ink ink = new Ink();
        if(pList != null && pList.isShowDelete(ink.vs)){
            pList.showDelete(ink.vs);
            repaint();
            return;
        }
        Shape.DB.train(curName, ink.norm); //this is safe because legal name testing is done in Database
        setState(); //possibly convert previously UNKNOWN to KNOWN
        repaint();
    }

    public void keyTyped(KeyEvent ke){
        char c = ke.getKeyChar();
        System.out.println("Typed:" + c);
        curName = (c == ' ' || c ==0x0D || c==0x0A) ? "" : curName +c; //x0D & x0A are ascii CR (return) & LF (new line)
        if(c == 0x0D || c == 0x0A){
            Shape.saveShapeDB();}
        setState();
        repaint();
    }
    public void setState(){
        //more work needed to actually detect KNOWN
        curState = (curName.isEmpty() || curName.equals("DOT"))? ILLEGAL : UNKNOWN;
        if(curState == UNKNOWN){
            if(Shape.DB.containsKey(curName)){ //look up
                curState = KNOWN;
                pList = Shape.DB.get(curName).prototypes;
            } else {
                pList = null;
            }
        }
    }

    public static void main(String[] args){
        PANEL = new ShapeTrainer();
        Window.launch();
    }
}