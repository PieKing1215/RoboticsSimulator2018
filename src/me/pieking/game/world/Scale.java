package me.pieking.game.world;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import me.pieking.game.ship.component.Component;

public class Scale extends Switch {

	public Scale(double x, double y, boolean blueTop) {
		super(x, y, blueTop);
		red = new ScalePlatform(x, y - 4.75);
		blue = new ScalePlatform(x, y + 4.75);
		
		if(blueTop){
			ScalePlatform temp = red;
			red = blue;
			blue = temp;
		}
		
		walls = new GameObject();
//		walls.type = BodyType.DEFAULT;
		walls.color = Color.GREEN;
		
		Rectangle r = new Rectangle(Component.unitSize * (2), Component.unitSize * (red.sizeH*3 + 1));
		r.translate(Component.unitSize * (red.sizeW/2 + 5.4), Component.unitSize * (red.sizeH/2 + 2));
		BodyFixture bf = new BodyFixture(r);
//		bf.setFilter(new GameObjectFilter(FilterType.DEFAULT));
		walls.setMass(MassType.INFINITE);
		walls.addFixture(bf);
		
		walls.setAngularDamping(GameWorld.getAngularDamping());
		walls.setLinearDamping(GameWorld.getLinearDamping());
		walls.translate(x, y);
		
	}

}
