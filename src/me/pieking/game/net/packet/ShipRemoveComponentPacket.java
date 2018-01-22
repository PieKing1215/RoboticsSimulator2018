package me.pieking.game.net.packet;

import java.awt.Point;

import me.pieking.game.Game;
import me.pieking.game.robot.component.ActivatableComponent;
import me.pieking.game.robot.component.Component;
import me.pieking.game.world.Player;

public class ShipRemoveComponentPacket extends Packet {

	String user;
	int x;
	int y;
	
	public ShipRemoveComponentPacket(String username, String x, String y) {
		this.user = username;
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}

	@Override
	public String format() {
		return user + "|" + x + "|" + y;
	}

	@Override
	public void doAction() {
		if(Game.getWorld().getPlayer(user) != null){
			Player pl = Game.getWorld().getPlayer(user);
			Component c = pl.robot.getComponent(new Point(x, y));
			if(c != null) {
				
				if(c instanceof ActivatableComponent){
					((ActivatableComponent) c).deactivate();
				}
				
				pl.robot.removeComponent(c);
				pl.constructShip();
			}
		}
	}
	
}
