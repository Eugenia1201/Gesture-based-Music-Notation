package reaction;

import graphicsLib.I;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Layer extends ArrayList<I.Show> implements I.Show {
    public String name;
    public static HashMap<String, Layer> byName = new HashMap<>();
    public static Layer ALL = new Layer("ALL");
    //Because of its static methods, it is calling instantiation function itself, so it has to be in order
    public Layer(String name) {
        this.name = name;
        if(!name.equals("ALL")){ALL.add(this);}
        byName.put(name, this); //byName is called here, meaning the byName map needs to be constructed at the first
        // place, so our static variables need to be declared such that byName map first, layer ALL second
    }
    @Override
    public void show(Graphics g) {
        for (I.Show item: this){
            item.show(g);
        }
    }

    public static void nuke(){ //NUKE all layers in preparation for undo
        for(I.Show lay: ALL){//all java knows is only show-able elements (java is smart enough to know that type that is(it's really a layer, not just I.Show) (Then we cast it into a (Layer)))
            ((Layer)lay).clear(); //ALL remains intact but the layers and thus the masses are cleared
        }
    }
}
