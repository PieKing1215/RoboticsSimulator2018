package me.pieking.game.ship.component;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;

import me.pieking.game.gfx.Sprite;
import me.pieking.game.gfx.Spritesheet;
import me.pieking.game.world.GameObject;
import me.pieking.game.world.Player;
import me.pieking.game.world.PlayerFilter;

public class StructureComponentSquare extends Component {

	public static Sprite spr = Spritesheet.tiles.subTile(1, 5);
	
	public StructureComponentSquare(int x, int y, int rot) {
		super(x, y, 1, 1, rot, 500);
		sprite = spr;
	}
	
	@Override
	public GameObject createBody(Player player){
		
		GameObject base = new GameObject();
		base.setAutoSleepingEnabled(false);
		base.color = Color.GRAY;
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
	
	@Override
	public String getDisplayName() {
		return "Metal Plate";
	}

}
