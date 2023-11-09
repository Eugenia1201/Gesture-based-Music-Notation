//package music;
//
//import graphicsLib.UC;
//import reaction.Gesture;
//import reaction.Reaction;
//
//import java.awt.*;
//
//public class Rest extends Duration{
//    public Staff staff;
//    public Time time;
//    int line = 4;// this is the default location of any rest
//
//    private Rest(Staff staff, Time time){
//        super();
//        this.time = time;
//        this.staff = staff;
//        addReaction (new Reaction("E-E"){ //Add FLAG to rest
//
//            @Override
//            public int bid(Gesture g) {
//                int y=g.vs.yM(), x1=g.vs.xL(), x2=g.vs.xH(), x = Rest.this.time.x;
//                if(x1 >x || x2<x){
//                    return UC.noBid;
//                }
//                return Math.abs(y-Rest.this.staff.yLine(4));
//            }
//
//            @Override
//            public void act(Gesture g) {
//                Rest.this.incFlag();
//            }
//        });
//
//        addReaction(new Reaction("DOT"){
//            public int bid(Gesture g){
//                int xr=Rest.this.time.x, yr = Rest.this.y();
//                int x = g.vs.xM(), y=g.vs.yM();
//                if(x<xr || x>xr+40 || x<yr-40 || y>yr+40){
//                    return UC.noBid;
//                }
//                return Math.abs(x-xr)+Math.abs(y-yr);
//            }
//            public void act(Gesture g){
//                Rest.this.cycleDot();
//            }
//        });
//    }
//    @Override
//    public void show(Graphics g){
////        int H = staff.H(), y = y();
//        if(nFlag == -2){Glyph.REST_W.showAt(g, H, time.x, y);}
//        if(nFlag == -1){Glyph.REST_H.showAt(g, H, time.x, y);}
//        if(nFlag == 0){Glyph.REST_Q.showAt(g, H, time.x, y);}
//        if(nFlag == 1){Glyph.REST_1F.showAt(g, H, time.x, y);}
//        if(nFlag == 2){Glyph.REST_2F.showAt(g, H, time.x, y);}
//        if(nFlag == 3){Glyph.REST_3F.showAt(g, H, time.x, y);}
//        if(nFlag == 4){Glyph.REST_4F.showAt(g, H, time.x, y);}
//    }
//}
