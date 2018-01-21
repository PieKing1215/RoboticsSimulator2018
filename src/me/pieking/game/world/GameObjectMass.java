package me.pieking.game.world;

import org.dyn4j.geometry.Mass;

public class GameObjectMass extends Mass{

	@Override
	public double getMass() {
		return super.getMass() / 10;
	}
	
}
