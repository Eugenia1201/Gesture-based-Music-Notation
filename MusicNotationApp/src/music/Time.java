package music;

import graphicsLib.UC;

import java.util.ArrayList;

public class Time {
    public int x;
    public ArrayList<Head> heads = new ArrayList<>();

    private Time(Sys sys, int x){
        this.x = x;
        sys.times.add(this); //factory-apps call getTime(x) //Adding this time to sys.times list
    }
    //Factory methods: instead of calling a constructor to build a new one, call something else to get an existing one; If not, then build a private method to get it
//    public void unStemHeads(int y1, int y2){
//        for(Head h: heads){
//            int y = h.y();
//            if(y> y1 && y<y2){
//                h.unStem();
//            }
//        }
//    }
    //----------------------TIME.LIST----------------
    public static class List extends ArrayList<Time>{
        public Sys sys;
        public List(Sys sys){this.sys = sys;}
        public Time getTime(int x){
            if(size() == 0){
                return new Time(sys, x);
            }
            Time t = getClosetTime(x);
            return (Math.abs(x-t.x) < UC.snapTime)?t: new Time(sys, x);
        }

        public Time getClosetTime(int x){
            Time res= get(0);
            int bestSoFar = Math.abs(x - res.x);
            for(Time t: this){
                int dist = Math.abs(x-t.x);
                if(dist<bestSoFar){
                    res = t;
                    bestSoFar = dist;
                }
            }
            return res;
        }
    }
}
