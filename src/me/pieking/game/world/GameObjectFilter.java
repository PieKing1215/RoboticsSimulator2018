package me.pieking.game.world;

import org.dyn4j.collision.Filter;

public class GameObjectFilter implements Filter{

	public FilterType type = FilterType.DEFAULT;
	
	public GameObjectFilter(FilterType type) {
		this.type = type;
	}
	
	@Override
	public boolean isAllowed(Filter arg0) {
		//System.out.println(arg0);
		if(arg0 instanceof GameObjectFilter){
			GameObjectFilter f = (GameObjectFilter) arg0;
			
			if(f instanceof PlayerFilter){
				Player pl = ((PlayerFilter) f).pl;
				if(pl != null && pl.noClip) return false;
			}
//			if(this.type != FilterType.FIRE){
//				System.out.println(this.type + " collides with " + f.type + "? " + this.type.collidesWith(f.type));
//			}
			if(this.type.collidesWith(f.type)){
				return true;
			}else{
				return false;
			}
			
		}else{
			return true;
		}
	}

	public enum FilterType{
		DEFAULT	(), 
		POWER_CUBE	("SHIP", "POWER_CUBE", "SCALE_PLATFORM", "POWER_CUBE_HOLDING"),
		POWER_CUBE_HOLDING	("SHIP", "POWER_CUBE"),
		SHIP	("SHIP", "POWER_CUBE"),
		PARTICLE(),
		SCALE_PLATFORM ("SHIP", "POWER_CUBE");
		
		static {
			for(FilterType f : values()){
				f.finish();
			}
		}
		
		FilterType[] coll;
		String[] collS;
		
		private FilterType(String... coll) {
			this.collS = coll;
		}
		
		private void finish(){
			coll = new FilterType[collS.length];
			for(int i = 0; i < collS.length; i++){
				coll[i] = FilterType.valueOf(collS[i]);
				System.out.println(this + " collides with " + coll[i]);
			}
		}
		
		public boolean collidesWith(FilterType f){
			for(FilterType fi : coll){
				if(fi == f){
					return true;
				}
			}
			return false;
		}
		
	}
	
}
