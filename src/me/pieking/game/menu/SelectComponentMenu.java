package me.pieking.game.menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.pieking.game.Game;
import me.pieking.game.gfx.Fonts;
import me.pieking.game.ship.component.Component;
import me.pieking.game.world.Player;

public class SelectComponentMenu extends Menu {

protected List<EToggle> toggles = new ArrayList<EToggle>();

	List<Component> comp = null;
	Component selected = null;
	public boolean wasLeftPressed = true;
	
	public Point hover = new Point(0, 0);
	
	public SelectComponentMenu(List<Component> comp) {
		super(new Color(0, 0, 0, 0));
		this.comp = comp;
	}
	
	@Override
	protected void render(Graphics2D g) {
		
		int gridW = 8;
		int gridH = (comp.size() / 8) + 1;
		
		int gridSize = 47;
		int gridPadding = 4;
		
		int boxPadding = 10;
		
		int titleHeight = 32;
		
		int width = (gridW * (gridSize + gridPadding) - gridPadding) + boxPadding*2;
		int height = (gridH * (gridSize + gridPadding) - gridPadding) + boxPadding*2 + titleHeight;
		int mx = Game.getWidth()/2 - width/2; 
		int my = Game.getHeight()/2 - height/2; 
		
		g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
		g.fillRoundRect(mx, my, width, height, 20, 20);
		g.setStroke(new BasicStroke(2f));
		g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
		g.drawRoundRect(mx, my, width, height, 20, 20);
		g.setStroke(new BasicStroke(1f));
		
		AffineTransform trans = g.getTransform();
		
		g.translate(mx, my);
		
		
		int titleYOfs = 24;
		g.setColor(Color.WHITE);
		g.setFont(Fonts.pixelmix.deriveFont(16f));
		String s = "Select a Componenet";
		g.drawString(s, width/2 - g.getFontMetrics().stringWidth(s)/2, titleYOfs);

		g.drawLine(width/2 - g.getFontMetrics().stringWidth(s)/2 - 4, titleYOfs + 4, width/2 + g.getFontMetrics().stringWidth(s)/2 + 4, titleYOfs + 4);
		
		
		g.translate(boxPadding, boxPadding + titleHeight);
		
		g.setTransform(trans);
		
		selected = null;
		hover = new Point(0, 0);
		for(int x = 0; x < gridW; x++){
			for(int y = 0; y < gridH; y++){
				
				int index = x + y*gridW;
				
				boolean locked = true;
				
				boolean item = false;
				
				if(index < comp.size()){
					Component c = comp.get(index);
					if(c != null){
						Integer ctInteger = Game.getWorld().getSelf().inventory.get(c.getClass());
						int ct = ctInteger == null ? 0 : ctInteger;
						
						locked = ct <= 0;
						
						item = true;
					}
				}
				
				Rectangle r = new Rectangle(x * (gridSize + gridPadding) + mx + boxPadding, y * (gridSize + gridPadding) + my + boxPadding + titleHeight, gridSize, gridSize);
				
				boolean hover = r.contains(Game.mouseLoc());
				if(hover){
					g.setColor(new Color(0.6f, 0.6f, 0.6f, 0.5f));
				}else{
					g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.5f));
				}
				
				g.setStroke(new BasicStroke(2f));
				g.fill3DRect(r.x, r.y, r.width, r.height, true);
				g.fill3DRect(r.x+1, r.y+1, r.width-2, r.height-2, true);
				g.setStroke(new BasicStroke(1f));
				
				if(hover) this.hover = new Point(x, y);
				
				if(locked && !item){
					g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));
					g.fill(r);
				}
				
				if(index < comp.size()){
					Component c = comp.get(index);
					if(c != null){
						Integer ctInteger = Game.getWorld().getSelf().inventory.get(c.getClass());
						int ct = ctInteger == null ? 0 : ctInteger;
						
						AffineTransform t = g.getTransform();
						g.translate(r.x + r.width/2, r.y + r.height/2);
						c.renderScaled(g);
						g.setTransform(t);
						
						String num = "" + ct;
						GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), num);
						Shape shape = gv.getOutline();
						g.setStroke(new BasicStroke(4.0f));
						g.setColor(Color.BLACK);
						g.translate(r.x + r.width + 4 - g.getFontMetrics().stringWidth(num), r.y + r.height);
						g.draw(shape);
						g.setTransform(t);
						
						g.setStroke(new BasicStroke(1.0f));
						
						if(locked){
							g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));
							g.fill(r);
						}
						
						g.setColor(locked ? new Color(0.6f, 0.4f, 0.4f, 1f) : Color.WHITE);
						g.setFont(Fonts.pixelmix.deriveFont(16f));
						g.drawString(num, r.x + r.width + 4 - g.getFontMetrics().stringWidth(num), r.y + r.height);
						
						if(hover && !locked) selected = c;
					}
				}
				
			}
		}
		
//		hover = new Point(1, 0);
		if(selected != null){
    		
    		List<String> info = new ArrayList<String>();
    		
    		info.add(selected.getDisplayName());
    		info.add("HP: " + Math.round(selected.maxHealth));
    		info.add("Size: " + selected.bounds.width + "x" + selected.bounds.height);
    		
    		g.setFont(Fonts.pixelmix.deriveFont(16f));
    		
    		int maxW = 0;
    		
    		for(String st : info){
    			maxW = Math.max(maxW, g.getFontMetrics().stringWidth(st));
    		}
    		
    		int titleGap = 10;
    		
    		int iPadding = 10;
    		
    		int lineSpacing = 20;
    		
    		int iWidth = maxW + iPadding*2;
    		int iHeight = info.size() * lineSpacing + iPadding*2 + titleGap;
    		
    		int ix = mx + (hover.x * (gridSize + gridPadding) + boxPadding) + gridSize/2 - iWidth/2;
    		int iy = my + (hover.y * (gridSize + gridPadding) + boxPadding + titleHeight) + gridSize;
    		
    		
    		g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
    		g.fillRoundRect(ix, iy, iWidth, iHeight, 10, 10);
    		
    		g.setStroke(new BasicStroke(2f));
    		g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
    		g.drawRoundRect(ix, iy, iWidth, iHeight, 10, 10);
    		
    		g.setColor(Color.WHITE);
    		g.setStroke(new BasicStroke(1f));

    		g.drawLine(ix + iWidth/2 - g.getFontMetrics().stringWidth(info.get(0))/2 - 4, iy + iPadding + g.getFont().getSize() + 4, ix + iWidth/2 + g.getFontMetrics().stringWidth(info.get(0))/2 + 4, iy + iPadding + g.getFont().getSize() + 4);
    		
    		for(int i = 0; i < info.size(); i++){
    			String st = info.get(i);
    			
    			g.drawString(st, ix + iWidth/2 - g.getFontMetrics().stringWidth(st)/2, iy + iPadding + lineSpacing*i + g.getFont().getSize() + (i > 0 ? titleGap : 0));
    		}
    		
		}
		
//		int textYOfs = 22;
//		int textSpacing = 24;
//		
//		g.setColor(Color.WHITE);
//		g.setFont(Fonts.pixelmix.deriveFont(16f));
//		String s = comp.getDisplayName();
//		g.drawString(s, width/2 - g.getFontMetrics().stringWidth(s)/2, Game.getHeight() - height + textYOfs);
//
//		g.drawLine(width/2 - g.getFontMetrics().stringWidth(s)/2 - 4, Game.getHeight() - height + textYOfs + 4, width/2 + g.getFontMetrics().stringWidth(s)/2 + 4, Game.getHeight() - height + textYOfs + 4);
//		
//		int prevWidth = 100;
//		
//		Rectangle r = new Rectangle(width/2 - prevWidth/2, Game.getHeight() - height + textYOfs + 14, prevWidth, prevWidth);
//		
//		Shape cl = g.getClip();
//		g.setClip(r);
//		
//		g.setColor(Color.BLACK);
//		g.fill(r);
//		
//		g.setColor(Color.WHITE);
//		g.setFont(Fonts.pixelmix.deriveFont(12f));
//		
//		AffineTransform tr = g.getTransform();
////		g.scale(10, 10);
//		g.translate(r.getCenterX(), r.getCenterY());
////		comp.lastBody.personalScale = 100;
////		comp.lastBody.renderNoTranslate(g, new BasicStroke(1f));
////		comp.lastBody.personalScale = Float.MAX_VALUE;
//		
//		double scale = Math.pow(zoom / 6f, 2) + 15;
//		
//		double xOffset = -comp.lastBody.getWorldCenter().x * scale;
//		double yOffset = -comp.lastBody.getWorldCenter().y * scale;
//		
//		Game.getWorld().render(g, xOffset, yOffset, scale);
//		
//		g.setTransform(tr);
//		
//		g.setClip(cl);
//		
//		g.setColor(Color.WHITE);
//		g.setFont(Fonts.pixelmix.deriveFont(20f));
//		//(int)r.getMaxX() + 4, r.y, 20, 20
//		g.drawString("+", (int)r.getMaxX() + 8, r.y + 18);
//		g.drawString("-", (int)r.getMaxX() + 8, r.y + 44);
//		
//		int infoIndex = 2;
//		
//		g.setColor(Color.WHITE);
//		g.setFont(Fonts.pixelmix.deriveFont(16f));
//		double hp = comp.health / comp.maxHealth;
//		s = "HP: " + Math.round(comp.health) + "/" + Math.round(comp.maxHealth) + " (" + Math.round(hp * 100) + "%)";
//		g.drawString(s, width/2 - g.getFontMetrics().stringWidth(s)/2, Game.getHeight() - height + textYOfs + prevWidth + (textSpacing * (infoIndex++)));
//		
//		if(comp instanceof ThrusterComponent){
//			ThrusterComponent tc = (ThrusterComponent) comp;
//			
//    		s = "Force = " + Math.round(tc.force * 10.) / 10. + " MN";
//    		g.drawString(s, width/2 - g.getFontMetrics().stringWidth(s)/2, Game.getHeight() - height + textYOfs + prevWidth + (textSpacing * (infoIndex++)));
//		}
		
		
//		g.setFont(Fonts.pixelmix.deriveFont(14f));
//		g.setColor(buttons.get(0).getBounds().contains(Game.mouseLoc()) ? Color.YELLOW : Color.WHITE);
//		g.drawString("<-", 16, 32);
		
	}

	@Override
	protected void tick() {
		boolean nowLeftPressed = Game.mouseHandler().isLeftPressed();
		
		if(nowLeftPressed && !wasLeftPressed){
			close();
			System.out.println(selected);
			if(Game.getWorld().getSelf() != null){
				Player p = Game.getWorld().getSelf();
				if(selected != null){
    				try {
    					p.selectBuildItem(selected.getClass(), 0);
    				}catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    					e.printStackTrace();
    				}
				}else{
					p.buildSelected = null;
					p.buildPreview = null;
				}
			}
		}
		
		wasLeftPressed = nowLeftPressed;
	}

	@Override
	public void init() {
		buttons.clear();
		
		addButton(new Rectangle(10, 10, 10, 10), new Runnable() {
			public void run() {
				close();
			}
		});
		
	}
	
	public EToggle addToggle(Rectangle bounds, ToggleRunnable t){
		EToggle tog = new EToggle(bounds);
		tog.addToggleListener(t);
		addButton(tog);
		return tog;
	}
	
	public EToggle addToggle(Rectangle bounds, ToggleRunnable t, boolean toggled){
		EToggle tog = addToggle(bounds, t);
		tog.state = toggled ? 1 : 0;
		tog.toggled = toggled;
		return tog;
	}
	
	public ESwitch addSwitch(Rectangle bounds, ToggleRunnable t, int maxState){
		ESwitch tog = new ESwitch(bounds, maxState);
		tog.addToggleListener(t);
		addButton(tog);
		return tog;
	}
	
	public ESwitch addSwitch(Rectangle bounds, ToggleRunnable t, int maxState, int currState){
		ESwitch tog = addSwitch(bounds, t, maxState);
		tog.state = currState;
		return tog;
	}
	
	public boolean insideMenu(Point p){
		int height = 400;
		int width = 300;
		
		return new Rectangle(0, Game.getHeight() - height, width, height).contains(p);
	}
	
}
