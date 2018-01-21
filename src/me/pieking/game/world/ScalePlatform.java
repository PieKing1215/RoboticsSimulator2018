package me.pieking.game.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import me.pieking.game.Game;
import me.pieking.game.Location;
import me.pieking.game.gfx.Sprite;
import me.pieking.game.gfx.Spritesheet;
import me.pieking.game.ship.component.Component;
import me.pieking.game.world.GameObject.BodyType;
import me.pieking.game.world.GameObjectFilter.FilterType;

public class ScalePlatform {

	public static Sprite spr = Spritesheet.tiles.subTile(0, 15, 2, 2);

	public GameObject base;
	
	double sizeW = 6.2;
	double sizeH = 5;
	
	public ScalePlatform(double x, double y, double sizeW, double sizeH) {
		
		this.sizeW = sizeW;
		this.sizeH = sizeH;
		
		x *= GameWorld.FIELD_SCALE;
		y *= GameWorld.FIELD_SCALE;
		
		sizeW *= GameWorld.FIELD_SCALE;
		sizeH *= GameWorld.FIELD_SCALE;
		
		base = new GameObject();
		base.type = BodyType.SCALE_PLATFORM;
		base.setAutoSleepingEnabled(false);
		base.color = Color.GREEN;
		
		Rectangle r = new Rectangle(Component.unitSize * sizeW, Component.unitSize * sizeH);
		r.translate(Component.unitSize * (sizeW/2 - 0.5), Component.unitSize * (sizeH/2 - 0.5));
		BodyFixture bf = new BodyFixture(r);
		bf.setFilter(new GameObjectFilter(FilterType.DEFAULT));
		base.setMass(MassType.INFINITE);
		base.addFixture(bf);
		
		r = new Rectangle(Component.unitSize * sizeW, Component.unitSize * 0.25);
		r.translate(Component.unitSize * (sizeW/2 - 0.5), Component.unitSize * (sizeH/2 - 0.5) - (Component.unitSize * sizeH) + (Component.unitSize * (sizeH/2 - 0.5)) + Component.unitSize*0.625);
		bf = new BodyFixture(r);
		bf.setFilter(new GameObjectFilter(FilterType.SCALE_PLATFORM));
		base.setMass(MassType.INFINITE);
		base.addFixture(bf);
		
		r = new Rectangle(Component.unitSize * sizeW, Component.unitSize * 0.25);
		r.translate(Component.unitSize * (sizeW/2 - 0.5), Component.unitSize * (sizeH/2 - 0.5) + (Component.unitSize * sizeH) - (Component.unitSize * (sizeH/2 - 0.5)) - Component.unitSize*0.625);
		bf = new BodyFixture(r);
		bf.setFilter(new GameObjectFilter(FilterType.SCALE_PLATFORM));
		base.setMass(MassType.INFINITE);
		base.addFixture(bf);
		
		r = new Rectangle(Component.unitSize * 0.25, Component.unitSize * sizeH);
		r.translate(Component.unitSize * (sizeW/2 - 0.5) - (Component.unitSize * sizeW/2), Component.unitSize * (sizeH/2 - 0.5) + (Component.unitSize * sizeH) - (Component.unitSize * (sizeH/2 - 0.5)) - Component.unitSize*0.625 - (Component.unitSize * sizeH/2) + (Component.unitSize * 0.25/2));
//		r.rotate(Math.toRadians(90), r2.getCenter());
		bf = new BodyFixture(r);
		bf.setFilter(new GameObjectFilter(FilterType.SCALE_PLATFORM));
		base.setMass(MassType.INFINITE);
		base.addFixture(bf);
		
		r = new Rectangle(Component.unitSize * 0.25, Component.unitSize * sizeH);
		r.translate(Component.unitSize * (sizeW/2 - 0.5) + (Component.unitSize * sizeW/2), Component.unitSize * (sizeH/2 - 0.5) + (Component.unitSize * sizeH) - (Component.unitSize * (sizeH/2 - 0.5)) - Component.unitSize*0.625 - (Component.unitSize * sizeH/2) + (Component.unitSize * 0.25/2));
//		r.rotate(Math.toRadians(-90), r2.getCenter());
		bf = new BodyFixture(r);
		bf.setFilter(new GameObjectFilter(FilterType.SCALE_PLATFORM));
		base.setMass(MassType.INFINITE);
		base.addFixture(bf);
		
		base.setAngularDamping(GameWorld.ANGULAR_DAMPING);
		base.setLinearDamping(GameWorld.LINEAR_DAMPING);
		base.translate(x, y);
	}

	public ScalePlatform(double x, double y) {
		
//		this(x, y, sizeW, sizeH); // TODO: find a better way to do this
		this(x, y, 6.2, 5);
		
//		float speed = 50f;
//		Point2D.Float pt = Utils.polarToCartesian((float) Math.toRadians(Math.toDegrees(angle) + 90), speed);
////		System.out.println(pt);
//		Vector2 vec = base.getWorldCenter().subtract(base.getWorldCenter().copy().add(pt.x, pt.y));
//		base.applyForce(vec);
		
	}
	
	public int numCubes(){
		int ct = 0;
		List<PowerCube> cub = Game.getWorld().getCubes();
		for(PowerCube p : cub) {
			if(base.contains(p.base.getWorldCenter())) {
				ct++;
			}
		}
		
		return ct;
	}
	
	public void tick(){
//		if(numCubes() > 0) {
//			base.color = Color.GREEN;
//		}else {
//			base.color = Color.RED;
//		}
	}

	public Location getLocation() {
		return new Location((float)base.getWorldCenter().x, (float) base.getWorldCenter().y);
	}
	
	public void render(Graphics2D g){
		if(Game.getTime() % 30 >= 15){
			base.render(g, new BasicStroke(1f), spr, Component.unitSize * sizeW * 2, Component.unitSize * sizeH * 2);
		}else{
			base.render(g);
		}
	}
	
}
