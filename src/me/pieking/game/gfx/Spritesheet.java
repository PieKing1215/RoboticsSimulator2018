package me.pieking.game.gfx;

import java.awt.Point;

public class Spritesheet {

	public static Spritesheet tiles = new Spritesheet(Images.getSprite("tileset.png"));
	
	private Sprite base;
	
	private int tileW = 20;
	private int tileH = 20;
	
	public Spritesheet(Sprite base) {
		this.base = base;
	}
	
	public Spritesheet(Sprite base, int tileW, int tileH){
		this.base = base;
		this.tileW = tileW;
		this.tileH = tileH;
	}

	public int width(){
		return base.getWidth();
	}
	
	public int height(){
		return base.getHeight();
	}
	
	public Sprite subImage(int x, int y, int w, int h){
		return new Sprite(base.getImage().getSubimage(x, y, w, h));
	}
	
	public Sprite subTile(int tx, int ty){
		return new Sprite(base.getImage().getSubimage(tx * tileW, ty * tileH, tileW, tileH));
	}
	
	public Sprite subTile(int tx, int ty, int tw, int th){
		return new Sprite(base.getImage().getSubimage(tx * tileW, ty * tileH, tw * tileW, th * tileH));
	}
	
	public AnimatedImage animatedTile(Point... points){
		return animatedTile(1, 1, points);
	}
	
	public AnimatedImage animatedTile(int tw, int th, Point... points){
		Sprite[] spr = new Sprite[points.length];
		
		for(int i = 0; i < points.length; i++){
			spr[i] = subTile(points[i].x, points[i].y, tw, th);
			//System.out.println(i + " " + (spr[i] == Images.errorS));
		}
		
		return new AnimatedImage(spr);
	}

	public int widthTiles() {
		return base.getWidth()/tileW;
	}
	
	public int heghtTiles() {
		return base.getHeight()/tileH;
	}
	
}
