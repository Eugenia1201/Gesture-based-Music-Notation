package music;

import reaction.Mass;

import java.awt.*;

public abstract class Duration extends Mass { //used for Rests and for Stems //Making it abstract for book-keeping
    public int nFlag = 0, nDot = 0;
    public Duration() {
        super("NOTE");
    }

    //This is part of our model. but comes with several controllers within themselves.
    abstract public void show(Graphics g);

    public void incFlag() { if (nFlag < 4) {nFlag++;}}
    public void decFlag() { if (nFlag > -2) {nFlag--;}} //Take a whole note to quarter note; from a quarter note to half note
    // decrease the duration or increase the duration
    public void cycleDot(){ nDot++; if(nDot > 3){nDot = 0;}} // reset to zero
}
