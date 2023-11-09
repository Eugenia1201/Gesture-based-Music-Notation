package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

import static music.AppMusicEd.PAGE;

public class Bar extends Mass {
    private static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8; //bits in barType and write in hex number -> thinking in binary
    public Sys sys;
    public int x, barType = 0; //use an integer to indicate 0/1/2/4/8 bits in bar Types
    public Bar(Sys sys, int x){
        super("BACK");
        this.sys=sys;
        this.x=x;
        if(Math.abs(PAGE.margins.right - x) < UC.barToMarginSnap){
            this.x=PAGE.margins.right;
        }

        addReaction(new Reaction("S-S"){ //cycle the existing Bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                if(Math.abs(x - Bar.this.x) > UC.barToMarginSnap){return UC.noBid;} // the number returns should be less than the barToMarginSnap
                if(y1 < Bar.this.sys.yTop()-20 || y2 > Bar.this.sys.yBot()+20){return UC.noBid;} // y1 && y2 both in sys range
                return Math.abs(x - Bar.this.x);
            }
            @Override
            public void act(Gesture g) {
                Bar.this.cycleType();
            }
        });

        addReaction(new Reaction("DOT"){ //Put a dot on the bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM();
                int y = g.vs.yM();
                if(y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()){ return UC.noBid; }
                int dist = Math.abs(x - Bar.this.x);
                if(dist > 3*PAGE.sysFmt.maxH){ return UC.noBid; }
                return dist;
            }
            @Override
            public void act(Gesture g) {
                if(g.vs.xM() < Bar.this.x){
                    Bar.this.toggleLeft();
                }else {
                    Bar.this.toggleRight();
                }
            }
        });
    }
    public void show (Graphics g){
        int sysTop = sys.yTop(), y1=0, y2=0, bot; //y1, y2 top and bottom of connected component
        boolean justSawBreak = true;
        for(int i =0; i<sys.fmt.size(); i++){
            Staff.Fmt sf = sys.fmt.get(i); //local variable
            Staff staff = sys.staffs.get(i);//another local variable
            if(justSawBreak){y1 = staff.yTop();}// remember start of connected component
            y2 = staff.yBot();
            if(!sf.barContinues){
                drawLines(g,x,y1,y2);
            }
            justSawBreak = !sf.barContinues;
            if(barType>3){
                drawDots(g, x, staff.yTop());
            }
        }
    }
    public void cycleType(){barType++; if(barType > 2) {barType = 0;}}
    public void toggleLeft(){barType = barType ^ LEFT;} //Exclusive OR (only One could be true, not both) to toggle bits
    public void toggleRight(){barType = barType ^ RIGHT;} // toggle bits with exclusive or

    public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy){
        g.drawLine(x, y1, x+dx, y1-dy);
        g.drawLine(x, y2, x+dx, y2+dy);
    }
    public static void fatBar(Graphics g, int x, int y1, int y2, int dx){
        g.fillRect(x,y1,dx,y2-y1);
    }
    public static void thinBar(Graphics g, int x, int y1, int y2) {
        g.drawLine(x, y1, x, y2);
    }

    public void drawLines(Graphics g, int x, int y1, int y2){
        int H = PAGE.sysFmt.maxH;
        if(barType == 0){thinBar(g,x,y1,y2);}
        if(barType == 1){thinBar(g,x,y1,y2); thinBar(g,x-H, y1, y2);}
        if(barType == 2){fatBar(g,x-H, y1,y2,H); thinBar(g,x-2*H, y1, y2);}
        if(barType >= 4){
            fatBar(g,x-H, y1,y2,H);
            if((barType & LEFT)!=0){ thinBar(g,x-2*H, y1,y2); wings(g,x-2*H, y1,y2,-H,H);}
            if((barType & RIGHT)!=0){ thinBar(g,x+H, y1,y2); wings(g,x+H, y1,y2,H,H);}
        }
    }

    public void drawDots(Graphics g, int x, int top){
        int H = PAGE.sysFmt.maxH;
        if((barType & LEFT) != 0){
            g.fillOval(x-3*H, top + 11*H/4, H/2, H/2);
            g.fillOval(x-3*H, top + 19*H/4, H/2, H/2);
        }
        if((barType & RIGHT) !=0){
            g.fillOval(x+3*H/2, top + 11*H/4, H/2, H/2);
            g.fillOval(x+3*H/2, top + 19*H/4, H/2, H/2);
        } // to let two dots around line 3
    }
}

//MVC Model View Controller
// Model - control all the data your app has
// View - Show routine that your show your work to the screen
// In this application: viewer built into the model app cause it is so simple;
// other apps could potentially have several diff views. (Simply one view -> put into the model)
// Gesture events->part of controller. toggler/show/cycle were put into Bar class as part of controllers

// Code if just the side effect of writing the document and the whole design idea.
