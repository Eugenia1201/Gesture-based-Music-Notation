package sandbox;

import graphicsLib.G;
import graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends Window {
    public static int clicks = 0;
    public static Path path = new Path();
    public static Pic pic = new Pic();
    //basically calling the constructor of the Path class(if we don't write a path class,
    //we get a default constructor) -> ArrayList has a default constructor;
    //Default constructor behavior is good enough to use multiple other functions.

    public Paint(){super("Paint", 1000, 700);}



    @Override
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(G.rndColor());
        g.fillOval(100, 100, 200, 100);
        g.drawLine(300, 200, 500, 100);
        g.setColor(Color.BLACK);
        String msg = "Hello World"; int x = 150; int y = 400;
        g.drawString(msg + clicks, x, y);
        g.setColor(Color.RED);
        pic.draw(g);
        g.fillOval(x-1, y-1, 2, 2);
        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent();
        int d = fm.getDescent();
        int w = fm.stringWidth(msg);
        g.drawRect(x, y - a, w, a + d);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        clicks++;
        // path.clear();
        //path.clear(); //if comment out, then the lines are not separated from each other (pen never got lifted up)
        //Ideally, when the drawing of one path is done, we should just leave it there.(And been added into that ArrayList)
        path = new Path();
        pic.add(path);
        path.add(me.getPoint());
        repaint();
    }
    //Garbage Collection: In java variables have pointers to some objects, Java realized when it is not in use, GC will collect those objects with no reference(in use). They work by entirely reference-counting.


    //technically we could also do a mouseRelease
    @Override
    public void mouseDragged(MouseEvent me) {
        path.add(me.getPoint());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new Paint();
        Window.launch();
    }

    //--------------------------------Path--------------------------------------------
    //Helper class intents to assist with PaintComponent method so that's why we nested it inside Paint Class (Alternatively we could create a new class file for this method too. )
    //Arraylist <> is called generic class
    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g) {
            for(int i = 1; i<size(); i ++) {
                Point p = get(i - 1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }
    }
    public static class Pic extends ArrayList<Path> {
        public void draw(Graphics g) {
            for(Path p: this) {
                p.draw(g);
            }
        }//this one line function, when put into the same line, even reads like JavaScript 😅
        //Tip from Marlin: whoever pays you, follow their style.
    }
}

//Class is to define a specific data type, followed by constructors
//then you have member functions being the most important pots


