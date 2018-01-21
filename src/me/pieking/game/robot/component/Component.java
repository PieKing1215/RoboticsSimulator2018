package me.pieking.game.robot.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;

import me.pieking.game.Utils;
import me.pieking.game.gfx.Sprite;
import me.pieking.game.world.GameObject;
import me.pieking.game.world.Player;
import me.pieking.game.world.PlayerFilter;

public class Component {

	public static float unitSize = 0.5f;
	
	public Rectangle bounds = new Rectangle();
	public Rectangle origBounds = new Rectangle();
	public Point renderOfs = new Point();
	public int rot = 0;
	public GameObject lastBody;
	
	public double health = 100;
	public double maxHealth = 100;
	
	public Sprite sprite;
	
	public Component(int x, int y, int width, int height, int rot, int maxHealth) {
		bounds = new Rectangle(x, y, width, height);
		this.origBounds = (Rectangle) bounds.clone();
		this.rot = rot;
		this.maxHealth = maxHealth;
		this.health = this.maxHealth;
		
//		System.out.println(rot + " " + x + " " + y + " " + width + " " + height);
		Rectangle2D r2d = new Float(0, 0, width, height);
		AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(rot), 0.5, 0.5);
		Rectangle unitBounds = rotate.createTransformedShape(r2d).getBounds2D().getBounds();
//		System.out.println(bounds);
		bounds.width = unitBounds.width;
		bounds.height = unitBounds.height;
		bounds.x += unitBounds.x;
		bounds.y += unitBounds.y;
		renderOfs = new Point(unitBounds.x, unitBounds.y);
		
//		System.out.println(unitBounds);
	}
	
	public GameObject createBody(Player player){
		
		GameObject base = new GameObject();
		base.setAutoSleepingEnabled(false);
		org.dyn4j.geometry.Rectangle r = new org.dyn4j.geometry.Rectangle(bounds.width * unitSize, bounds.width * unitSize);
		BodyFixture bf = new BodyFixture(r);
		bf.setFilter(new PlayerFilter(player));
		base.addFixture(bf);
		base.setMass(new Mass(base.getMass().getCenter(), 0, 0));
		base.setMass(MassType.NORMAL);
		
		base.setAngularDamping(0);
		base.setLinearDamping(0);
		
		return base;
	}
	
	public void renderScaled(Graphics2D g){
		Color c = lastBody.color;
		lastBody.color = Utils.fade(c, Color.RED, (1-(float) (health / maxHealth))/2f);
		AffineTransform trans = g.getTransform();
		
		int max = Math.max(bounds.width, bounds.height);
		g.scale(1f/max, 1f/max);
		
		g.translate(Component.unitSize/2 * lastBody.personalScale, Component.unitSize/2 * lastBody.personalScale);
		g.translate(Component.unitSize * lastBody.personalScale * -bounds.width/2, Component.unitSize * lastBody.personalScale * -bounds.height/2);
		g.translate(Component.unitSize * lastBody.personalScale * -renderOfs.x , Component.unitSize * lastBody.personalScale * -renderOfs.y);
		
		if(sprite != null) {
			double sca = lastBody.personalScale;
			lastBody.render(g, new BasicStroke(1f), sprite, sprite.getWidth()/20, sprite.getHeight()/20);
			lastBody.personalScale = sca;
		}else{
			lastBody.render(g);
		}
		
//		g.setColor(Color.BLUE);
//		g.drawRect(0, 0, 10, 10);
		lastBody.color = c;
		g.setTransform(trans);
		
	}
	
	public void render(Graphics2D g){
		Color c = lastBody.color;
		lastBody.color = Utils.fade(c, Color.RED, (1-(float) (health / maxHealth))/2f);
		AffineTransform trans = g.getTransform();
//		if(correctionOffset) g.translate((unitSize * -renderOfs.x) * GameObject.SCALE, (unitSize * -renderOfs.y) * GameObject.SCALE);
		if(sprite != null) {
			Color co = Utils.fade(new Color(1f, 0f, 0f, 0f), Color.RED, (1-(float) (health / maxHealth))/2f);
			lastBody.render(g, new BasicStroke(1f), sprite, sprite.getWidth()/20, sprite.getHeight()/20, co);
		}else{
			lastBody.render(g);
		}
		lastBody.color = c;
		g.setTransform(trans);
		
//		g.drawRect(bounds.x * 10, bounds.y * 10, bounds.width * 10, bounds.height * 10);
		
	}

	public void tick(Player pl) {
//		if(Game.getTime() % 30 == 0) if(health < maxHealth) health = Math.min(maxHealth, health + 1);
	}
	
	public String getDisplayName(){
		return "Component";
	}
	
	public String getTooltip(){
		return null;
	}
	
}
