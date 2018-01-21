package me.pieking.game.net.packet;

import me.pieking.game.Game;
import me.pieking.game.world.Player;

public class PlayerDiePacket extends Packet{

	String user;
	
	public PlayerDiePacket(String username) {
		this.user = username;
	}

	@Override
	public String format() {
		return user;
	}
	
	@Override
	public void doAction() {
		Player p = Game.getWorld().getPlayer(user);
		if(p != null){
			p.die();
		}
	}
	
}
