package graphicsLib;


import reaction.Gesture;

import java.awt.*;

public interface I {
    public interface Act{public void act(Gesture g);}

    public interface React extends Act{public int bid(Gesture g);}
    public interface Show{public void show(Graphics g);}
    public interface Area{ //an area object is actually a mouseListener public boolean hit(int x, int y);
        // function signature with no body no implementation
        public void dn(int x, int y);    // dn for down
        public void up(int x, int y);
        public void drag(int x, int y);
//        public void press(int x, int y);
//        public void release(int x, int y);
    }
}



//Interface: full of abstract functions, implementation done in classes  (something common but not necessary need to implement )bookkeeping stuff, but expected someone else to do
//Abstract class - half interface half abstract classes (keeping something abstract, yet implementing some other stuff )