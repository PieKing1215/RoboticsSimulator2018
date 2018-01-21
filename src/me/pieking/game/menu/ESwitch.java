package me.pieking.game.menu;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class ESwitch extends EButton {

	public int state = 0;
	public int maxState = 0;
	
	private List<ToggleRunnable> toggListeners = new ArrayList<ToggleRunnable>();
	
	public ESwitch(int x, int y, int w, int h, int maxState) {
		this(new Rectangle(x, y, w, h), maxState);
	}
	
	public ESwitch(Rectangle r, int maxState) {
		super(r);
		
		this.maxState = maxState;
		
		addListener(() -> doSwitch()); //oooooo sicc :}
		
	}

	public void doSwitch() {
		if(state < maxState){
			state++;
		}else{
			state = 0;
		}
		
		for(ToggleRunnable t : toggListeners){
			t.toggle(state);
		}
	}

	public void addToggleListener(ToggleRunnable r){
		toggListeners.add(r);
	}
	
}

interface ToggleRunnable {
	public void toggle(int nowState);
}
