package me.pieking.game.net.packet;

import java.io.UnsupportedEncodingException;

import me.pieking.game.Game;
import me.pieking.game.robot.component.Component;
import me.pieking.game.world.Player;

public class ShipAddComponentPacket extends Packet {

	String user;
	int x;
	int y;
	int rot;
	Class<? extends Component> cls;
	
	@SuppressWarnings("unchecked")
	public ShipAddComponentPacket(String username, String componentClass, String x, String y, String rot) throws UnsupportedEncodingException, ClassNotFoundException {
		this.cls = (Class<? extends Component>) Class.forName("me.pieking.game.ship.component." + componentClass);
		this.user = username;
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.rot = Integer.parseInt(rot);
	}

	@Override
	public String format() {
		return user + "|" + cls.getSimpleName() + "|" + x + "|" + y + "|" + rot;
	}

	@Override
	public void doAction() {
		if(Game.getWorld().getPlayer(user) != null){
			Player pl = Game.getWorld().getPlayer(user);
			try{
    			Component c = pl.createComponent(cls, x, y, rot);
    			
    			if(pl.robot.addComponent(c)) pl.constructShip();
    			
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
}
