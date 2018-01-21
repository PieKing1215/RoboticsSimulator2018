package me.pieking.game.net.packet;

import java.awt.Point;

import me.pieking.game.Game;
import me.pieking.game.ship.component.ActivatableComponent;
import me.pieking.game.ship.component.Component;
import me.pieking.game.world.Player;

public class ShipComponentActivatePacket extends Packet {

	String user;
	int x;
	int y;
	boolean nowActive;
	
	public ShipComponentActivatePacket(String username, String x, String y, String activated) {
		this.user = username;
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.nowActive = Boolean.parseBoolean(activated);
	}

	@Override
	public String format() {
		return user + "|" + x + "|" + y + "|" + nowActive;
	}

	@Override
	public void doAction() {
		if(Game.getWorld().getPlayer(user) != null){
			Player pl = Game.getWorld().getPlayer(user);
			if(pl.ship != null){
    			Component c = pl.ship.getComponent(new Point(x, y));
    			System.out.println(c);
    			if(c != null) {
    				if(c instanceof ActivatableComponent){
    					ActivatableComponent ac = (ActivatableComponent) c;
    					if(nowActive){
    						ac.activate();
    					}else{
    						ac.deactivate();
    					}
    				}
    			}
			}
		}
	}
	
}
