package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

import static music.AppMusicEd.PAGE;

public class Head extends Mass {
    public Staff staff;
    public int line; //line is a y Coordinate in disguise; Line 0 is top line, line 1 is the first space, line 2 is next line down
    public Time time;
    public Head(Staff staff, int x, int y) {
        super("NOTE"); this.staff = staff; this.time = staff.sys.getTime(x);
        int H = staff.H();
        int top = staff.yTop()-H;
        line = (y-top +H/2)/H-1; //snap y to nearest line
        System.out.println("Head Constructor Line: "+line);
    }

    @Override
    public void show(Graphics g){
        int H = staff.H();
        Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line*H);
    }
}
