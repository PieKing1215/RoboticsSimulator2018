package me.pieking.game.world;

import java.awt.Color;
import java.awt.Graphics2D;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import me.pieking.game.Utils;
import me.pieking.game.ship.component.Component;
import me.pieking.game.world.GameObject.BodyType;
import me.pieking.game.world.GameObjectFilter.FilterType;
import me.pieking.game.world.Switch.Team;

public class Switch {

	protected ScalePlatform red;
	protected ScalePlatform blue;
	protected Team ownership = Team.NONE;
	protected Team ownership_override = Team.NONE;
	
	public GameObject walls;
	
	public Switch(double x, double y, boolean blueTop) {
		red = new ScalePlatform(x-.19, y - 3.5 - .25, 7, 5.5);
		blue = new ScalePlatform(x-.19, y + 3.5, 7, 5.5);
		
		if(blueTop){
			ScalePlatform temp = red;
			red = blue;
			blue = temp;
		}
		
		x *= GameWorld.FIELD_SCALE;
		y *= GameWorld.FIELD_SCALE;
		
		walls = new GameObject();
//		walls.type = BodyType.DEFAULT;
		walls.color = Color.GREEN;
		
		Rectangle r = new Rectangle(Component.unitSize * (red.sizeW + 0.9), Component.unitSize * (red.sizeH*2));
		r.translate(Component.unitSize * (red.sizeW/2 - 0.58), Component.unitSize * (red.sizeH/2 - 0.5));
		BodyFixture bf = new BodyFixture(r);
//		bf.setFilter(new GameObjectFilter(FilterType.DEFAULT));
		walls.setMass(MassType.INFINITE);
		walls.addFixture(bf);
		
		walls.setAngularDamping(GameWorld.getAngularDamping());
		walls.setLinearDamping(GameWorld.getLinearDamping());
		walls.translate(x, y);
		
	}
	
	public void render(Graphics2D g) {
		red.render(g);
		blue.render(g);
	}

	public void tick() {
		if(red.numCubes() == blue.numCubes()) {
			ownership = Team.NONE;
		}else if(red.numCubes() > blue.numCubes()) {
			ownership = Team.RED;
		}else {
			ownership = Team.BLUE;
		}
		
		red.base.color = Utils.fade(Color.GRAY, Team.RED.color, 0.2f);
		blue.base.color = Utils.fade(Color.GRAY, Team.BLUE.color, 0.2f);
		
		if(getOwner() == Team.RED) red.base.color = Team.RED.color;
		if(getOwner() == Team.BLUE) blue.base.color = Team.BLUE.color;
	}

	public ScalePlatform getRedPlatform() {
		return red;
	}
	
	public ScalePlatform getBluePlatform() {
		return blue;
	}
	
	public Team getOwner() {
		return ownership_override != Team.NONE ? ownership_override : ownership;
	}
	
	public void setOwnerOverride(Team team){
		ownership_override = team;
	}
	
	public static enum Team {
		NONE (Color.GRAY),
		BLUE (Color.decode("#0066b3")),
		RED  (Color.decode("#ed1c24"));
		
		Color color;
		private Team(Color col) {
			color = col;
		}
		public Team getOpposite() {
			return this == BLUE ? RED : (this == RED ? BLUE : NONE);
		}
		
	}
	
}
