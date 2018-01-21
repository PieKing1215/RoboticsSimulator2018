package me.pieking.game.menu;

import java.awt.Rectangle;

public class EToggle extends ESwitch {

	public boolean toggled = false;
	
	public EToggle(int x, int y, int w, int h) {
		this(new Rectangle(x, y, w, h));
	}
	
	public EToggle(Rectangle r) {
		super(r, 1);
		
		EToggle tog = this;
		addToggleListener(new ToggleRunnable() {
			@Override
			public void toggle(int nowState) {
				tog.toggle();
			}
		});
		
	}

	public void toggle() {
		toggled ^= true;
	}
	
}

