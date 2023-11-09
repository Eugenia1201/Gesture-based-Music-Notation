package reaction;

import graphicsLib.I;

import java.awt.*;


public abstract class Mass extends Reaction.List implements I.Show{
    //All masses that live inside of this layer
    public Layer layer;
    public Mass (String layerName){
        this.layer = Layer.byName.get(layerName);
        if(layer!=null){
            layer.add(this);
        } else {
            System.out.println("BAD LAYER NAME -" + layerName);
        }
    }
    public void delete(){
        clearAll(); // clears all reactions from this list AND from the Reaction byShape Map
        //A mass is primarily a list of all reactions
        layer.remove(this); // remove self from layers.
    }
    public void show(Graphics g){}
}
