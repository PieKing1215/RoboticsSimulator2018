package me.pieking.game.net.packet;

import java.awt.Point;

import me.pieking.game.Game;
import me.pieking.game.robot.component.Component;
import me.pieking.game.world.Player;

public class ShipComponentHealthPacket extends Packet {

	String user;
	int x;
	int y;
	double health;
	
	public ShipComponentHealthPacket(String username, String x, String y, String health) {
		this.user = username;
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.health = Double.parseDouble(health);
	}

	@Override
	public String format() {
		return user + "|" + x + "|" + y + "|" + health;
	}

	@Override
	public void doAction() {
		if(Game.getWorld().getPlayer(user) != null){
			Player pl = Game.getWorld().getPlayer(user);
			if(pl.robot != null){
    			Component c = pl.robot.getComponent(new Point(x, y));
    			System.out.println(c);
    			if(c != null) {
    				c.health = health;
    			}
			}
		}
	}
	
}
