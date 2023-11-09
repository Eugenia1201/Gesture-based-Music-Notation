package music;

import graphicsLib.UC;
import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Sys extends Mass {
    public Staff.List staffs = new Staff.List(); //need to be initialized
    public Page page = PAGE;
    public int iSys;
    public Sys.Fmt fmt;
    public Time.List times;

    public Sys(int iSys, Sys.Fmt sysFmt) {
        super("BACK");
        this.iSys = iSys;
        this.fmt = sysFmt;;
        this.times = new Time.List(this);
        for(int i = 0; i<sysFmt.size(); i++){
            addStaff(new Staff(i, sysFmt.get(i))); //change staff.add to addStaff make the staff non-null
        }
    }
    public Time getTime(int x){return times.getTime(x);}
    public int yTop(){return page.sysTop(iSys);}
    public int yBot(){return staffs.get(staffs.size()-1).yBot();} //That bottom line is the bottom of the whole system
    @Override
    public void show(Graphics g){ int y = yTop(), x = PAGE.margins.left; g.drawLine(x,y,x,y+fmt.height());}
    public void addStaff(Staff s){staffs.add(s); s.sys = this;}
    // Null Pointer Exception:
    // Multithreading race condition/painting routine/drawing routine/show routine sharing data between two different threads.
    // data has been changed along the way
    // What causes the race condition: all sys class extend Mass. Constructor creating staff (as a extension of Mass)
    // -> Mass set up first then loaded into the layers when the bar has really set up yet.
    // One Fix: When setting up the staff, the last thing is to add itself to the layers ( as opposed to rely on Mass to put it into the layers )
    // Another Fix: guard all the show routines. Let me test if there is any null values.
    // Third options: ignore it. it happens once a year.
    //------------------------SYS FMT----------------------
    public static class Fmt extends ArrayList<Staff.Fmt>{
        public ArrayList<Integer> staffOffset = new ArrayList<>();
        public int maxH = UC.defaultStaffH;
        public int height(){
            int last = size()-1;
            return staffOffset.get(last) + get(last).height();
        }
        public void showAt(Graphics g, int y){
            for(int i=0; i<size(); i++){
                get(i).showAt(g, y+staffOffset.get(i));
            }
        }
        public Staff.Fmt getLast(){
            return get(size() - 1);
        }

        public void addStaffFmt(Staff.Fmt sf, int yOff) {
            add(sf);
            staffOffset.add(yOff);
            if(maxH < sf.H){
                maxH = sf.H;
            }
        }

//        public void addNewStaff(int y){
//            new Staff.Fmt(y-PAGE.top);
//            for(Sys s: SYSTEMS){
//                s.makeStaffsMatchSysfmt();
//            }
//        }
    }
    //-------------------------List------------------------
    public static class List extends ArrayList<Sys>{
    } //?????
}
