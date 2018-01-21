package me.pieking.game.gfx;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Disp extends Canvas{

	private static final long serialVersionUID = 1L;

	private BufferedImage im;
	
	public int realWidth;
	public int realHeight;
	
	public Disp(int resWidth, int resHeight, int realWidth, int realHeight) {
		im = new BufferedImage(resWidth, resHeight, BufferedImage.TYPE_INT_RGB);
		this.realHeight = realHeight;
		this.realWidth = realWidth;
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(im, 0, 0, realWidth, realHeight, null);
	}
	
	public Graphics2D getRenderGraphics(){
		return (Graphics2D) im.getGraphics();
	}
	
	public Point getMousePositionScaled(){
		
		try{
			if(getMousePosition() == null) return null;
			
			int x = getMousePosition().x;
			int y = getMousePosition().y;
			
			float xs = ((float)im.getWidth() / (float)realWidth);
			float ys = ((float)im.getHeight() / (float)realHeight);
			
			x *= xs;
			y *= ys;
			
			//System.out.println(xs + " " + ys + " " + x + " " + y);
			
			return new Point(x, y);
		}catch(Exception e){
			return null;
		}
	}
	
}
