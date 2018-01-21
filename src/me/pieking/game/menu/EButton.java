package me.pieking.game.menu;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import me.pieking.game.Game;

public class EButton {

    private Rectangle bounds;
    private List<Runnable> listeners = new ArrayList<Runnable>();
    
    private boolean enabled = true;
    
    boolean wasClicked = false;
    
    //private int createTime = Game.getTime();
    
    public EButton(int x, int y, int w, int h){
        this(new Rectangle(x, y, w, h));
    }
    
    public EButton(Rectangle bounds){
        this.bounds = bounds;
        wasClicked = Game.mouseHandler().isLeftPressed();
    }
    
    public Rectangle getBounds(){
        return bounds;
    }
    
    public void setBounds(Rectangle r){
        this.bounds = r;
    }
    
    public boolean addListener(Runnable r){
        return listeners.add(r);
    }
    
    public boolean removeListener(Runnable r){
        return listeners.remove(r);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void tick(){
    	
    	//if(Game.getTime() - createTime > 1){
        	Point mouse = Game.keyHandler().lastMousePos;
        	
        	if(mouse != null){
            	if(bounds.contains(mouse)){
            		if(!wasClicked && Game.mouseHandler().isLeftPressed() && enabled){
            			wasClicked = true;
            			for(Runnable r : listeners){
            				r.run();
            			}
            		}
            	}
            	
            	if(wasClicked && !Game.mouseHandler().isLeftPressed()){
            		wasClicked = false;
            	}
        	}
    	//}
    	
    }
    
}
