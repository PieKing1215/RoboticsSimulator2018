package me.pieking.game.net.packet;

import me.pieking.game.Game;
import me.pieking.game.world.Player;

public class LeavePacket extends Packet{

	String user;
	
	public LeavePacket(String username) {
		this.user = username;
	}

	@Override
	public String format() {
		return user;
	}
	
	@Override
	public void doAction() {
		Player p = Game.getWorld().getPlayer(user);
		System.out.println("remove " + p);
		Game.getWorld().removePlayer(p);
	}
	
}
