package me.pieking.game.scripting;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaScript {

	public Globals globals;
	public LuaThread thread;
	public String name;
	public CustomDebugLib cdl;
	public LuaValue chunk;
	
	public LuaScript(String name, Globals g, LuaValue chunk, CustomDebugLib cdl) {
		this.globals = g;
		this.name = name;
		this.cdl = cdl;
		this.chunk = chunk;
	}
	
	public void run(){
		
		if(isRunning()) stop();
		
		cdl.interrupted = false;
		thread = new LuaThread(globals, chunk);
		
		new Thread(() -> {
    		Varargs result = thread.resume(LuaValue.NIL);
    		System.out.println("[" + name + "] Error: " + result.arg(2));
    		if(!result.arg(1).checkboolean()){
    			cdl.interrupted = true;
    		}
    		thread = null;
		}).start();
	}

	public void stop(){
		if(!isRunning()) return;
		
		cdl.interrupted = true;
	}
	
	public boolean isRunning(){
		return thread != null && !cdl.interrupted;
	}
	
}
