package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff; // staff index
    public Staff.Fmt fmt;

    public Staff(int iStaff, Staff.Fmt staffFmt){
        super("BACK");
        this.iStaff = iStaff;
        this.fmt = staffFmt;

        addReaction(new Reaction("S-S"){ //create Bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                if(x < PAGE.margins.left || x > PAGE.margins.right + UC.barToMarginSnap ){return UC.noBid;}
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return d<40? d+UC.barToMarginSnap:UC.noBid; //Allow cycleBarType to outbid this.
            }
            @Override
            public void act(Gesture g) {
                new Bar(Staff.this.sys, g.vs.xM());
            }
        });

        addReaction(new Reaction("S-S") {//toggle BarContinues
            public int bid(Gesture g){
                if(Staff.this.sys.iSys !=0){return UC.noBid;}
                int y1=g.vs.yL(), y2= g.vs.yH();
                int iStaff = Staff.this.iStaff;
                if(iStaff == PAGE.sysFmt.size()-1){return UC.noBid;}
                if(Math.abs(y1-Staff.this.yBot()) > 20){return UC.noBid;}
                Staff nextStaff = sys.staffs.get(iStaff+1);
                if(Math.abs(y2-Staff.this.yBot()) > 20){return UC.noBid;} //force the y2 value to be close to the next staff
                return 10;
            }
            @Override
            public void act(Gesture g) {
                PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
            }
        });

        addReaction(new Reaction("SW-SW"){ //Add note to staff
            public int bid(Gesture g){
                int x= g.vs.xM(), y = g.vs.yM();
                if(x<PAGE.margins.left || x > PAGE.margins.right){return UC.noBid;}
                int H = Staff.this.H(), top = Staff.this.yTop()-H, bot = Staff.this.yBot()+H;
                if(y<top || y>bot){return UC.noBid;}
                return 10;
            }
            public void act(Gesture g){
                new Head(Staff.this, g.vs.xM(), g.vs.yM());
            }
        });

//        addReaction(new Reaction("W-S") {
//            @Override
//            public int bid(Gesture g) {
//                int x = g.vs.xL(), y=g.vs.yM();
//                if(x<PAGE.margins.left || x>PAGE.margins.right){return UC.noBid;}
//                int H = Staff.this.H(), top = Staff.this.yTop()-H, bot = Staff.this.yBot()+H;
//                if(y<top || y>bot){return UC.noBid;}
//                return 10;
//            }
//
//            @Override
//            public void act(Gesture g) {
//                Time t = Staff.this.sys.getTime(g.vs.xL());
//                new Rest(Staff.this, t);
//            }
//        });
    }
    @Override
    public void show(Graphics g){

    }
    public int sysOff(){return sys.fmt.staffOffset.get(iStaff);}
    public int yTop(){return sys.yTop() + sysOff();}
    public int yBot(){return yTop() + fmt.height();}
    public int H() { return fmt.H;}
//    public int yLine(int n){return yTop()+n*H();}
//    public int lineOfY(int y){
//        int H = sys.fmt.get(iStaff).H;
//        int Bias = 100; //Integer truncation rounds toward 0...
//        int top = yTop() - H*Bias; // We move the origin to a number like -100
//        return (y-top + H/2)/H - Bias;// calculate a big number then remove that Bias
//    }

    //public int yLine(int n){return yTop() + n*H();}
    //----------------------------------------STAFF Fmt-------------------------------
    public static class Fmt{
        public int H = UC.defaultStaffH; //half the space between staff lines - Useful because notes land both on lines and on space
        public int nLines = 5;//most music is 5 lines, but there are exceptions
        public boolean barContinues = false; //Control whether the bar continues to the next staff
        public int height(){
            return 2* H *(nLines - 1);
        }
        public void toggleBarContinues(){ barContinues = !barContinues;}
        public void showAt(Graphics g, int y){
            int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right;
            for(int i=0; i<nLines; i++){
                g.drawLine(LEFT, y+2*H*i, RIGHT,y+2*H*i);
            }
        }
    }
    //----------------------------------------STAFF List----------------------------------
    public static class List extends ArrayList<Staff> {

    }
}
