package me.pieking.game.scripting;

import java.util.List;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import me.pieking.game.Game;

public class LuaLibInput extends TwoArgFunction {

	public LuaLibInput() {}

	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaValue library = tableOf();
		library.set("isPressed", new isPressed());
		library.set("pressed", new isPressed());
		
		LuaValue mouse = tableOf();
		mouse.set("left", new isLeftMousePressed());
		mouse.set("right", new isRightMousePressed());
		mouse.set("middle", new isMiddleMousePressed());
		
		library.set("mouse", mouse);
		
		env.set("input", library);
		return library;
	}

	static class isPressed extends OneArgFunction {
		public LuaValue call(LuaValue key) {
			return LuaValue.valueOf(Game.keyHandler().isPressed(key.checkint()));
		}
	}
	
	static class getPressed extends ZeroArgFunction {
		public LuaValue call() {
			List<Integer> pressed = Game.keyHandler().getPressed();
			
			LuaValue[] vals = new LuaValue[pressed.size()];
			
			for(int i = 0; i < pressed.size(); i++){
				vals[i] = valueOf(pressed.get(i));
			}
			
			return listOf(vals);
		}
	}
	
	
	static class isLeftMousePressed extends ZeroArgFunction {
		public LuaValue call() {
			return LuaValue.valueOf(Game.mouseHandler().isLeftPressed());
		}
	}
	
	static class isRightMousePressed extends ZeroArgFunction {
		public LuaValue call() {
			return LuaValue.valueOf(Game.mouseHandler().isRightPressed());
		}
	}
	
	static class isMiddleMousePressed extends ZeroArgFunction {
		public LuaValue call() {
			return LuaValue.valueOf(Game.mouseHandler().isMiddlePressed());
		}
	}
}