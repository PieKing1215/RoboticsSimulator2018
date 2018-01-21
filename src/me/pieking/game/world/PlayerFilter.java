package me.pieking.game.world;

import org.dyn4j.collision.Filter;

public class PlayerFilter extends GameObjectFilter{

	public Player pl;
	
	public PlayerFilter(Player p) {
		super(FilterType.SHIP);
		this.pl = p;
	}
	
	@Override
	public boolean isAllowed(Filter arg0) {
		if(pl != null && pl.noClip) return false;
		if(arg0 instanceof PlayerFilter){
			PlayerFilter f = (PlayerFilter) arg0;
			if(f.pl == null || pl == null) return true;
			return !pl.name.equals(f.pl.name);
		}
		return super.isAllowed(arg0);
	}
	
}
