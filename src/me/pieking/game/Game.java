package me.pieking.game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.pieking.game.events.KeyHandler;
import me.pieking.game.events.MouseHandler;
import me.pieking.game.gfx.Disp;
import me.pieking.game.gfx.Fonts;
import me.pieking.game.gfx.Render;
import me.pieking.game.gfx.ShipFileAccessory;
import me.pieking.game.gfx.ShipFileView;
import me.pieking.game.menu.Menu;
import me.pieking.game.net.ClientStarter;
import me.pieking.game.net.ServerStarter;
import me.pieking.game.net.packet.JoinPacket;
import me.pieking.game.net.packet.LeavePacket;
import me.pieking.game.net.packet.Packet;
import me.pieking.game.net.packet.ShipDataPacket;
import me.pieking.game.scripting.LuaTest;
import me.pieking.game.ship.Ship;
import me.pieking.game.sound.Sound;
import me.pieking.game.world.GameObject;
import me.pieking.game.world.GameWorld;
import me.pieking.game.world.Player;
import me.pieking.game.world.Switch.Team;

public class Game {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	private static String name = "GameTemplate";
	
	private static boolean running = false;
	
	private static int fps = 0;
	private static int tps = 0;
	
	private static JFrame frame;
	
	private static Disp disp;
	private static int time = 0;
	
	private static KeyHandler keyHandler;
	private static MouseHandler mouseHandler;
	
	private static GameWorld gw;
	
	public static void runGame(String[] args) {
		run();
	}

	private static void run(){
		init();
		
		long last = System.nanoTime();
		long now = System.nanoTime();
		
		double delta = 0d;
		
		double nsPerTick = 1e9 / 60d;
		
		long timer = System.currentTimeMillis();
		
		int frames = 0;
		int ticks = 0;
		
		running = true;
		
		while(running){
			now = System.nanoTime();
			
			long diff = now - last;
			
			delta += diff / nsPerTick;
			
			boolean shouldRender = true;
			
			while(delta >= 1){
				delta--;
				tick();
				ticks++;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {}
			
			if(shouldRender){
				render();
				frames++;
			}
			
			last = now;
			
			if(System.currentTimeMillis() - timer >= 1000){
				timer = System.currentTimeMillis();
				fps = frames;
				tps = ticks;
				frames = 0;
				ticks = 0;
			}
			
			
		}
		
	}
	
	private static void init(){
		
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {}
		
		frame = new JFrame(name + " v" + version + " | " + fps + " FPS " + tps + " TPS");
		JPanel jp = new JPanel();
		jp.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.add(jp);
		frame.pack();
		
		jp.setVisible(false);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(!isServer() && ClientStarter.clientStarter.getClient().isConnected()){
    				LeavePacket pack = new LeavePacket(gw.getSelf().name);
    				doPacketNoMe(pack);
				}
				
				System.exit(0);
			}
		});
		frame.setLocationRelativeTo(null);
		
		disp = new Disp(WIDTH, HEIGHT, WIDTH, HEIGHT);
		
		keyHandler = new KeyHandler();
		disp.addKeyListener(keyHandler);
		
		mouseHandler = new MouseHandler();
		disp.addMouseListener(mouseHandler);
		disp.addMouseWheelListener(mouseHandler);
		
		frame.add(disp);
		
		frame.setVisible(true);
		
		LuaTest.init();
		Sound.init();
		Fonts.init();
		
		gw = new GameWorld();
		
		while(!ClientStarter.hasEnteredIp){
			try {
				Thread.sleep(100);
			}catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		if(!isServer()){
			ClientStarter.clientStarter.getClient().connect();
			if (ClientStarter.clientStarter.getClient().isConnected()) {
				System.out.println("Connected to the server.");
				JoinPacket pack = new JoinPacket("Player " + System.currentTimeMillis(), "1", "1");
				Game.doPacket(pack);
				System.out.println("packarino " + pack.getCreated());
				Game.getWorld().selfPlayer = pack.getCreated();
				
				Ship s = gw.selfPlayer.selectShip();
			    
			    try {
					ShipDataPacket sdp = new ShipDataPacket(Game.getWorld().getSelf().name, s.saveDataString());
					Game.doPacket(sdp);
				}catch (IOException e1) {
					e1.printStackTrace();
				}
				
			} else {
				System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
				long start = System.currentTimeMillis();
				gw.selfPlayer = new Player("Cool boi", 900f / GameObject.SCALE * GameWorld.FIELD_SCALE, 500f / GameObject.SCALE * GameWorld.FIELD_SCALE, Team.RED);
				System.out.println("time = " + (System.currentTimeMillis() - start));

				Ship s = gw.selfPlayer.selectShip();
				gw.selfPlayer.loadShip(s);
				
				gw.addPlayer(gw.selfPlayer);
				
			}
		}
		
	}
	
	private static void tick(){
		//System.out.println(fps + " " + tps);
		frame.setTitle(name + (isServer() ? " (Server) " : "") + " v" + version + " | " + fps + " FPS " + tps + " TPS");
		
		try{
			gw.tick();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(!isServer()){
			Point p = disp.getMousePositionScaled();
    		if(p != null) keyHandler.lastMousePos = p;
    		
    		List<Menu> menus = Render.getMenus();
    		for(int i = 0; i < menus.size(); i++){
    			Menu m = menus.get(i);
    			m.iTick();
    		}
		}
		
		time++;
	}
	
	private static void render(){
		Render.render(disp);
		disp.paint(disp.getGraphics());
	}
	
	public static String getName(){
		return name;
	}

	public static String getVersion(){
		return version;
	}

	public static int getTime() {
		return time ;
	}
	
	public static void stop(int status){
		System.exit(status);
	}
	
	public static KeyHandler keyHandler(){
		return keyHandler;
	}
	
	public static MouseHandler mouseHandler(){
		return mouseHandler;
	}
	
	public static int getWidth(){
		return WIDTH;
	}
	
	public static int getHeight(){
		return HEIGHT;
	}
	
	public static Disp getDisp(){
		return disp;
	}

	public static GameWorld getWorld() {
		return gw;
	}
	
	public static boolean isServer(){
		return ServerStarter.isServer;
	}
	
	public static void doPacket(Packet p){
		if(!isServer()) ClientStarter.clientStarter.writePacket(p);
		p.doAction();
	}
	
	public static void doPacketNoMe(Packet p){
		if(!isServer()) ClientStarter.clientStarter.writePacket(p);
	}
	
	public static Point mouseLoc() {
		return keyHandler.lastMousePos == null ? new Point(0, 0) : keyHandler.lastMousePos;
	}

	public static boolean debug() {
		return false;
	}
	
}
