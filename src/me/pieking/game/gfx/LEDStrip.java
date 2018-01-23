package me.pieking.game.gfx;

import java.awt.Color;

import me.pieking.game.Game;

public class LEDStrip {

	int numPixels;
	Color[] ledColors;
	StripMode mode;
	
	int timer = 0;
	
	public LEDStrip(int numPixels) {
		this.numPixels = numPixels;
		this.ledColors = new Color[numPixels];
	}
	
	public void setMode(StripMode mode){
		if(this.mode != mode) timer = 0;
		this.mode = mode;
	}
	
	public void tick(){
		
		timer++;
		
		switch(mode){
			case FILL_BLUE:
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = timer > (10 * i) ? Color.BLUE : new Color(0f, 0f, 0f, 0f);
				}
				if(timer > 10 * numPixels) timer = 0;
			case SOLID_BLUE_FULL:
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = Color.BLUE;
				}
				break;
			case SOLID_BLUE_WEAK:
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = new Color(0f, 0f, 1f, 0.25f);
				}
				break;
			case PULSE_BLUE:
				Color bPulse = new Color(0.2f, 0.2f, 1f, (float)((Math.sin(Game.getTime() / 10f) + 1) / 2f));
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = bPulse;
				}
				break;
			case PULSE_BLUE_RED_CORNERS:
				Color bPulse2 = new Color(0f, 0f, 1f, (float)((Math.sin(Game.getTime() / 10f) + 1) / 2f));
				for(int i = 0; i < numPixels; i++){
					if((i >= 6 && i <= 8) || (i >= 17 && i <= 19) || (i >= 30 && i <= 32) || (i >= 41 && i <= 43)){
						ledColors[i] = Color.RED;
					}else{
						ledColors[i] = bPulse2;
					}
				}
				break;
			case CHASE_BLUE:
				for(int i = 0; i < numPixels; i++){
					int time = Game.getTime()*3 + (10 * i);
					ledColors[i] = time % 60 >= 20 ? Color.BLUE : new Color(0f, 0f, 1f, 0.2f);
				}
				break;
			case SOLID_RED_FULL:
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = Color.RED;
				}
				break;
			case SOLID_RED_WEAK:
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = new Color(1f, 0f, 0f, 0.25f);
				}
				break;
			case PULSE_RED:
				Color rPulse = new Color(1f, 0f, 0f, (float)((Math.sin(Game.getTime() / 10f) + 1) / 2f));
				for(int i = 0; i < numPixels; i++){
					ledColors[i] = rPulse;
				}
				break;
			case PULSE_RED_BLUE_CORNERS:
				Color rPulse2 = new Color(1f, 0f, 0f, (float)((Math.sin(Game.getTime() / 10f) + 1) / 2f));
				for(int i = 0; i < numPixels; i++){
					if((i >= 6 && i <= 8) || (i >= 17 && i <= 19) || (i >= 30 && i <= 32) || (i >= 41 && i <= 43)){
						ledColors[i] = Color.BLUE;
					}else{
						ledColors[i] = rPulse2;
					}
				}
				break;
			case CHASE_RED:
				for(int i = 0; i < numPixels; i++){
					int time = Game.getTime()*3 + (10 * i);
					ledColors[i] = time % 60 >= 20 ? Color.RED : new Color(1f, 0f, 0f, 0.2f);
				}
			default:
				break;
		}
	}
	
	public static enum StripMode {
		FILL_BLUE,
		SOLID_BLUE_FULL,
		SOLID_BLUE_WEAK,
		PULSE_BLUE,
		PULSE_BLUE_RED_CORNERS,
		CHASE_BLUE,
		FILL_RED,
		SOLID_RED_FULL,
		SOLID_RED_WEAK,
		PULSE_RED,
		PULSE_RED_BLUE_CORNERS,
		CHASE_RED;
	}

	public int length() {
		return numPixels;
	}

	public Color getColor(int i) {
		return ledColors[i];
	}
	
}
