package me.pieking.game.menu;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import me.pieking.game.Game;

public class ChoiceMenu extends Menu{

	protected ChoiceListener listener;
	protected String[] choices;
	
	protected boolean realInitted = false;
	
	public ChoiceMenu(String[] choices, ChoiceListener l) {
		this.listener = l;
		this.choices = choices;
		System.out.println(choices);
	}
	
	@Override
	protected void render(Graphics2D g) {
		
	}

	@Override
	protected void tick() {
		if(!realInitted){
			realInit();
			realInitted = true;
		}
	}

	private void realInit() {
		init();
		int numChoices = choices.length;
		
		int w = 100;
		int padding = 50;
		
		for(int i = 0; i < numChoices; i++){
			int index = i;
			
			int x = Game.getWidth()/2 - (w * numChoices) - (padding * (numChoices - 1)) + (w * i) + (i > 0 ? padding : 0);
			
			addButton(new Rectangle(x, 100, w, 50), new Runnable() {
				public void run() {
					listener.choose(index);
				}
			});
		}
	}

	@Override
	public void init() {
		buttons.clear();
		
		if(Game.debug()){
    		addButton(new Rectangle(10, 10, 20, 20), new Runnable() {
    			public void run() {
    				reload();
    			}
    		});
		}
	}

}
