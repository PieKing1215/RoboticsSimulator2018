package me.pieking.game.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import me.pieking.game.Game;
import me.pieking.game.Location;
import me.pieking.game.gfx.Sprite;
import me.pieking.game.gfx.Spritesheet;
import me.pieking.game.robot.component.Component;
import me.pieking.game.world.GameObject.BodyType;
import me.pieking.game.world.GameObjectFilter.FilterType;

public class PowerCube {

	public static final double SIZE = 1.8 * Component.unitSize;
	
	public static Sprite spr = Spritesheet.tiles.subTile(0, 12, 2, 2);

	public GameObject base;
	
	public PowerCube(double x, double y, double angle) {
		
		x *= GameWorld.FIELD_SCALE;
		y *= GameWorld.FIELD_SCALE;
		
		base = new GameObject();
//		base.setBullet(true);
		base.type = BodyType.BULLET;
		base.setAutoSleepingEnabled(false);
		base.color = Color.YELLOW;
		Rectangle r = new Rectangle(SIZE, SIZE);
		r.translate(Component.unitSize/2, Component.unitSize/2);
		r.rotateAboutCenter(angle);
		BodyFixture bf = new BodyFixture(r);
		bf.setDensity(0.5);
		bf.setFilter(new GameObjectFilter(FilterType.POWER_CUBE));
//		bf.setFilter(new BulletFilter(this));
		base.setMass(new Mass(base.getMass().getCenter(), 0, 0));
		base.addFixture(bf);
		base.setMass(MassType.NORMAL);
		
		base.setAngularDamping(GameWorld.getAngularDamping());
		base.setLinearDamping(GameWorld.getLinearDamping());
		base.translate(x, y);
		
//		float speed = 50f;
//		Point2D.Float pt = Utils.polarToCartesian((float) Math.toRadians(Math.toDegrees(angle) + 90), speed);
////		System.out.println(pt);
//		Vector2 vec = base.getWorldCenter().subtract(base.getWorldCenter().copy().add(pt.x, pt.y));
//		base.applyForce(vec);
		
	}
	
	public void tick(){
		
	}

	public Location getLocation() {
		return new Location((float)base.getWorldCenter().x, (float) base.getWorldCenter().y);
	}
	
	public void render(Graphics2D g){
		if(Game.getTime() % 30 >= 15 || true){
			base.render(g, new BasicStroke(1f), spr, (int)(Component.unitSize * 4), (int)(Component.unitSize * 4));
		}else{
			base.render(g);
		}
	}
	
}
