package graphicsLib;

import java.awt.*;

public class UC {    // Universal Constants, collect magic numbers

    // put all the constants in a single place -> give the name in the sense-making way
    //mainWindowWidth - nice big fat universal name making-sense to all
    public static final int mainWindowWidth = 1000;
    public static final int mainWindowHeight = 700;
    public static int inkBufferMax = 900;
    public static final int normSampleSize = 25;
    public static final int normCoordMax = 1000; //two multiply together -> still be in the range of the screen
    public static Color inkColor = Color.BLACK;
    public static final int noMatchDist = 500000;
    public static final int dotThreshold = 5;
    public static String ShapeDbFileName = "shapeDB.dat";
    public static int noBid = 1000;
    public static int defaultStaffH = 8;
    public static int barToMarginSnap = 40;
    public static int snapTime;
    public static String FontName = "sinfonia";
}
