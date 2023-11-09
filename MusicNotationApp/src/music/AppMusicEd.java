package music;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AppMusicEd extends Window {

    static {new Layer("BACK"); new Layer("NOTE"); new Layer("FORE"); }
    public static Page PAGE;
    public AppMusicEd() {
        super("Music Editor", UC.mainWindowWidth, UC.mainWindowHeight);
        Reaction.initialReactions.addReaction(new Reaction("E-W"){
            public int bid(Gesture g){return 10;}
            public void act(Gesture g){
                int y = g.vs.yM();
                Sys.Fmt sysFmt = new Sys.Fmt();
                PAGE = new Page(sysFmt);
                PAGE.margins.top = 7;
                PAGE.addNewSys();
                PAGE.addNewStaff(0);
                this.disable();
            }
        });
    }
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLACK);
        Layer.ALL.show(g); //List of all layers that has a show routine
//        Glyph.CLEF_G.showAt(g,8,100,100);
        if(PAGE != null){// Don't call this line until the Page is ready
            Glyph.CLEF_G.showAt(g,8,100,PAGE.margins.top + 4*8);
//            Glyph.HEAD_HALF.showAt(g,8,200, PAGE.margins.top + 4*8);
//            Glyph.HEAD_Q.showAt(g,8,200,PAGE.margins.top+4*8);
        }
        Ink.BUFFER.show(g);
    }

    public static void main (String[] args){
        PANEL = new AppMusicEd();
        Window.launch();
    }

    public void mousePressed(MouseEvent me){Gesture.AREA.dn(me.getX(), me.getY()); repaint();}
    public void mouseDragged(MouseEvent me){Gesture.AREA.drag(me.getX(), me.getY()); repaint();}
    public void mouseReleased(MouseEvent me){Gesture.AREA.up(me.getX(), me.getY()); repaint();}
}
