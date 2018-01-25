package me.pieking.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import me.pieking.game.Gameplay.GameState;
import me.pieking.game.gfx.Fonts;
import me.pieking.game.gfx.Render;
import me.pieking.game.menu.SelectScriptMenu;
import me.pieking.game.robot.Robot;
import me.pieking.game.scripting.LuaScript;
import me.pieking.game.sound.Sound;
import me.pieking.game.sound.SoundClip;
import me.pieking.game.world.Balance.Team;
import me.pieking.game.world.GameWorld;
import me.pieking.game.world.Player;
import me.pieking.game.world.PowerCube;

public class Gameplay {

	private static SoundClip s_matchEnd = Sound.loadSound("Match End_normalized.wav");
	private static SoundClip s_matchPause = Sound.loadSound("Match Pause_normalized.wav");
	private static SoundClip s_startAuton = Sound.loadSound("Start Auto_normalized.wav");
	private static SoundClip s_startEndgame = Sound.loadSound("Start of End Game_normalized.wav");
	private static SoundClip s_startTeleop = Sound.loadSound("Start Teleop_normalized.wav");
	
	private static final Point2D[] redSpawns;
	static{
		redSpawns = new Point2D[3];
		redSpawns[0] = new Point2D.Double(10.2, 6);
		redSpawns[1] = new Point2D.Double(10.2, 16);
		redSpawns[2] = new Point2D.Double(10.2, 20);
	}
	private static final Point2D[] blueSpawns;
	static{
		blueSpawns = new Point2D[3];
		blueSpawns[0] = new Point2D.Double(54.6, 6);
		blueSpawns[1] = new Point2D.Double(54.6, 11.5);
		blueSpawns[2] = new Point2D.Double(54.6, 20);
	}

	public List<Player> redPlayers = new ArrayList<Player>();
	public List<Player> bluePlayers = new ArrayList<Player>();

	public List<Player> voted = new ArrayList<Player>();
	
	private int gameTime = ((2 * 60) + 30) * 60;
	
	private GameState state;
	{
		setState(GameState.WAITING_FOR_PLAYERS);
	}
	
	public void tick() {
		
		switch (state) {
			case WAITING_FOR_PLAYERS:
//				if(gameTime <= 0){
//					setState(GameState.SETUP);
//				}
				
				int numVoted = 0;
				List<Player> vote = new ArrayList<Player>();
				vote.addAll(voted);
				for(Player p : vote){
					if(Game.getWorld().getPlayers().contains(p)){
						numVoted++;
					}else{
						voted.remove(p);
					}
				}
				
				if(numVoted / (double)Game.getWorld().getPlayers().size() >= 0.5){
					setState(GameState.SETUP);
				}
				
				break;
			case SETUP:
				if(gameTime <= 0){
					setState(GameState.AUTON);
				}
				break;
			case AUTON:
				if(gameTime <= 0){
					setState(GameState.TELEOP);
				}
				break;
			case TELEOP:
				if(gameTime <= 0){
					setState(GameState.MATCH_END);
				}
				
				if(gameTime == 30 * 60) {
					s_startEndgame.stop();
					s_startEndgame.start();
				}
				
				break;
			case MATCH_END:
				if(gameTime <= 0){
					setState(GameState.WAITING_FOR_PLAYERS);
				}
				break;
			default:
				break;
		}
		
		// decrease the remaining game time if its more than 0
		if(gameTime > 0) gameTime--;
	}
	
	/**
	 * Renders the HUD, including game time, scores, active power ups, power cube storage, etc.
	 * @param g - the {@link Graphics2D} to render onto.
	 */
	public void renderHUD(Graphics2D g) {
		
		if(Robot.buildMode){
			g.setFont(Fonts.gamer.deriveFont(40f));
			g.setColor(Color.WHITE);
			g.drawString("BUILD MODE", 10, 44);
			
			g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
			g.fillRect(Game.getWidth() - 100, Game.getHeight() - 100, 100, 100);
			
			Player p = Game.getWorld().getSelfPlayer();
			if(p != null){
				if(p.buildSelected != null && p.buildPreview != null){
					AffineTransform trans = g.getTransform();
					g.translate(Game.getWidth() - 50, Game.getHeight() - 50);
					p.buildPreview.renderScaled(g);
					g.setTransform(trans);
					
					String num = "" + p.inventory.get(p.buildSelected);
					
					g.setFont(Fonts.pixelmix.deriveFont(20f));
					int x = Game.getWidth() - g.getFontMetrics().stringWidth(num) - 8;
					
					GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), num);
					Shape shape = gv.getOutline();
					g.setStroke(new BasicStroke(4.0f));
					g.setColor(Color.BLACK);
					g.translate(x, Game.getHeight() - 10);
					g.draw(shape);
					
					g.setStroke(new BasicStroke(1.0f));
					g.setColor(Color.WHITE);
					g.drawString(num, 0, 0);
					
					g.setTransform(trans);
				}
				
			}
		}
		
		if(state == GameState.WAITING_FOR_PLAYERS){
			g.setColor(new Color(0f, 0f, 0f, 0.7f));
			g.fillRect(0, 0, Game.getWidth(), Game.getHeight());
			g.setColor(Color.RED);
			g.setFont(Fonts.pixelmix.deriveFont(40f));
			String msg = "Waiting for Players...";
			g.drawString(msg, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg)/2, Game.getHeight()/2);
			g.setFont(Fonts.pixelmix.deriveFont(32f));
			String msg2 = "(" + Game.getWorld().getPlayers().size() + "/6)";
			g.drawString(msg2, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg2)/2, Game.getHeight()/2 + 40);
		
			int numVoted = 0;
			List<Player> vote = new ArrayList<Player>();
			vote.addAll(voted);
			for(Player p : vote){
				if(Game.getWorld().getPlayers().contains(p)){
					numVoted++;
				}else{
					voted.remove(p);
				}
			}
			
			g.setFont(Fonts.pixelmix.deriveFont(16f));
			g.setColor(new Color(170, 120, 0));
			String msg3 = "Press [V] to vote for force start.";
			g.drawString(msg3, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg3)/2, Game.getHeight()/2 + 80);
			String msg4 = "" + numVoted + " of " + (int)Math.ceil(Game.getWorld().getPlayers().size() / 2d) + " needed.";
			g.drawString(msg4, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg4)/2, Game.getHeight()/2 + 100);
		
		}else if(state == GameState.SETUP){
			g.setColor(new Color(0f, 0f, 0f, 0.5f));
			g.fillRect(0, 0, Game.getWidth(), Game.getHeight());
			g.setColor(Color.GREEN);
			g.setFont(Fonts.pixelmix.deriveFont(40f));
			String msg = "Setup (" + (gameTime / 60) + ")";
			g.drawString(msg, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg)/2, Game.getHeight()/2);
		}else if(state == GameState.MATCH_END){
			g.setColor(new Color(0f, 0f, 0f, 0.5f));
			g.fillRect(0, 0, Game.getWidth(), Game.getHeight());
			
			int timer = (10 * 60) - gameTime;
			
			g.setColor(Color.GREEN);
			g.setFont(Fonts.pixelmix.deriveFont(64f));
			if(timer < 60 * 2){
				String msg = "Match Over!";
				g.drawString(msg, Game.getWidth()/2 - g.getFontMetrics().stringWidth(msg)/2, Game.getHeight()/2);
			}
			
			if(timer > 60 * 4){
				
				float thru = ((timer - (60 * 4f)) / (60 * 1f));
				if(thru > 1f) thru = 1f;
				
				int scoreBoardY = Game.getHeight()/2 + (int)(50 * thru);
				
				g.setFont(Fonts.pixelLCD.deriveFont(150f * thru));
	    		
	    		int colonWidth = g.getFontMetrics().stringWidth(":")/2;
				
				g.setColor(Color.DARK_GRAY);
	    		int redScore = Game.getWorld().getScoreWithPenalites(Team.RED);
	    		int blueScore = Game.getWorld().getScoreWithPenalites(Team.BLUE);
	    		g.drawString("" + redScore, Game.getWidth()/2 - (colonWidth) - g.getFontMetrics().stringWidth("" + redScore) - 3, scoreBoardY - 3);
	    		g.setColor(Team.RED.color);
	    		g.drawString("" + redScore, Game.getWidth()/2 - (colonWidth) - g.getFontMetrics().stringWidth("" + redScore), scoreBoardY);
	    		
	    		g.setColor(Color.DARK_GRAY);
	    		g.drawString(":", Game.getWidth()/2 - g.getFontMetrics().stringWidth(":")/2 - 3, scoreBoardY - 3);
	    		g.setColor(blueScore > redScore ? Team.BLUE.color : Team.RED.color);
	    		g.drawString(":", Game.getWidth()/2 - g.getFontMetrics().stringWidth(":")/2, scoreBoardY);
	    		
	    		g.setColor(Color.DARK_GRAY);
	    		g.drawString("" + blueScore, Game.getWidth()/2 + (colonWidth) - 3, scoreBoardY - 3);
	    		g.setColor(Team.BLUE.color);
	    		g.drawString("" + blueScore, Game.getWidth()/2 + (colonWidth), scoreBoardY);
	    		
			}
		}else if(state == GameState.AUTON || state == GameState.TELEOP){
		
    		g.setFont(Fonts.pixelmix.deriveFont(20f));
    		
    		if(Game.getWorld().power_boost != Team.NONE){
    			g.setColor(Game.getWorld().power_boost.color);
    			String s = "boost (" + (Game.getWorld().power_boost_timer / 60) + ")";
    			if(Game.getWorld().power_boost_queued != Team.NONE) s += " (" + Game.getWorld().power_boost_queued + " queued)";
    			g.drawString(s, 10, Game.getHeight() - 20);
    		}
    		
    		if(Game.getWorld().power_force != Team.NONE){
    			g.setColor(Game.getWorld().power_force.color);
    			String s = "force (" + (Game.getWorld().power_force_timer / 60) + ")";
    			if(Game.getWorld().power_force_queued != Team.NONE) s += " (" + Game.getWorld().power_force_queued + " queued)";
    			g.drawString(s, 10, Game.getHeight() - 40);
    		}
    
    		
    		int scoreBoardY = 80;
    		
    		g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.7f));
    		g.fillRoundRect(Game.getWidth()/2 - 150, -50, 300, 140, 50, 50);
    		g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
    		g.setStroke(new BasicStroke(2f));
    		g.drawRoundRect(Game.getWidth()/2 - 150, -50, 300, 140, 50, 50);
    		g.setStroke(new BasicStroke(1f));
    		
    //		g.drawLine(Game.getWidth()/2, 0, Game.getWidth()/2, 100);
    		
    		
    		g.setFont(Fonts.pixelLCD.deriveFont(40f));
    		
    		// game time
    
    		double time = gameTime / 60;
    		
    		int seconds = (int) (time % 60);
    		String secondsStr = (seconds < 10 ? "0" : "") + seconds; 
    		int minutes = (int) (time / 60);
    		String minutesStr = "" + minutes; 
    		
    		String timeS = minutesStr + ":" + secondsStr;
    		g.setColor(Color.DARK_GRAY);
    		g.drawString(timeS, Game.getWidth()/2 - g.getFontMetrics().stringWidth(timeS)/2 - 3, scoreBoardY - 34 - 3);
    		g.setColor(Game.getWorld().getSelfPlayer().team.color);
    		g.drawString(timeS, Game.getWidth()/2 - g.getFontMetrics().stringWidth(timeS)/2, scoreBoardY - 34);
    		
    		g.setFont(Fonts.pixelLCD.deriveFont(28f));
    		
    		// scoreboard
    		
    		int colonWidth = g.getFontMetrics().stringWidth(":")/2;
    		
    		g.setColor(Color.DARK_GRAY);
    		int redScore = Game.getWorld().getScoreWithPenalites(Team.RED);
    		int blueScore = Game.getWorld().getScoreWithPenalites(Team.BLUE);
    		g.drawString("" + redScore, Game.getWidth()/2 - (colonWidth) - g.getFontMetrics().stringWidth("" + redScore) - 3, scoreBoardY - 3);
    		g.setColor(Team.RED.color);
    		g.drawString("" + redScore, Game.getWidth()/2 - (colonWidth) - g.getFontMetrics().stringWidth("" + redScore), scoreBoardY);
    		
    		g.setColor(Color.DARK_GRAY);
    		g.drawString(":", Game.getWidth()/2 - g.getFontMetrics().stringWidth(":")/2 - 3, scoreBoardY - 3);
    		g.setColor(blueScore > redScore ? Team.BLUE.color : Team.RED.color);
    		g.drawString(":", Game.getWidth()/2 - g.getFontMetrics().stringWidth(":")/2, scoreBoardY);
    		
    		g.setColor(Color.DARK_GRAY);
    		g.drawString("" + blueScore, Game.getWidth()/2 + (colonWidth) - 3, scoreBoardY - 3);
    		g.setColor(Team.BLUE.color);
    		g.drawString("" + blueScore, Game.getWidth()/2 + (colonWidth), scoreBoardY);
    		
    		
    		// power cube storage
    		
    		g.drawImage(PowerCube.spr.getImage(), Game.getWidth() - 80, Game.getHeight() - 80, 60, 60, null);
    		int numCubes = Game.getWorld().getProperties(Game.getWorld().getSelfPlayer().team).getCubeStorage();
    		g.setFont(Fonts.pixeled.deriveFont(14f));
    		g.setColor(Color.DARK_GRAY);
    		g.drawString("" + numCubes, Game.getWidth() - 80 + 53, Game.getHeight() - 80 + 65);
    		g.setColor(Color.WHITE);
    		g.drawString("" + numCubes, Game.getWidth() - 80 + 55, Game.getHeight() - 80 + 67);
		}
		
		g.setFont(Fonts.pixeled.deriveFont(16f));
		g.setColor(Color.GREEN);
		g.drawString("" + state, 6, 24);
		
	}
	
	/**
	 * @return the time remaining in the game, in ticks.
	 */
	public int getGameTime() {
		return gameTime;
	}

	/**
	 * Sets the remaining game time.
	 * @param gameTime - the remaining game time, in ticks.
	 */
	public void setGameTime(int gameTime) {
		this.gameTime = gameTime;
	}
	
	public void setState(GameState state){
		this.state = state;
		
		switch (state) {
			case WAITING_FOR_PLAYERS:
				gameTime = 2 * 60;
				Robot.setAllEnabled(false);
				resetField();
				voted.clear();
				Game.getWorld().setCameraCentered(true);
				break;
			case SETUP:
				gameTime = 2 * 60;
				Robot.setAllEnabled(false);
				Game.getWorld().setCameraCentered(false);
				
				redPlayers.clear();
				bluePlayers.clear();
				
				for(Player p : Game.getWorld().getPlayers()){
					if(p.team == Team.RED){
						redPlayers.add(p);
					}else if(p.team == Team.BLUE){
						bluePlayers.add(p);
					}
				}
				
				for(int i = 0; i < Math.min(redPlayers.size(), 3); i++){
					Player p = redPlayers.get(i);
					p.setLocation(redSpawns[i], Math.toRadians(90));
				}
				for(int i = 0; i < Math.min(bluePlayers.size(), 3); i++){
					Player p = bluePlayers.get(i);
					p.setLocation(blueSpawns[i], Math.toRadians(-90));
				}
				
				SelectScriptMenu ssm = new SelectScriptMenu(Game.getWorld().getSelfPlayer().getRobot());
				Render.showMenu(ssm);
				new Thread(() -> {
					while(ssm.isFocused()) {
						gameTime = 2 * 60;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
				}).start();
				
				break;
			case AUTON:
				gameTime = 15 * 60; // 15s
				Robot.setAllEnabled(false);
				LuaScript ls = Game.getWorld().getSelfPlayer().getRobot().getAutonScript();
				if(ls != null) ls.run();
				
				s_startAuton.stop();
				s_startAuton.start();
				
				break;
			case TELEOP:
				gameTime = 135 * 60; // 2m 15s
				Robot.setAllEnabled(true);
				LuaScript ls2 = Game.getWorld().getSelfPlayer().getRobot().getAutonScript();
				if(ls2 != null) ls2.stop();
				
				s_startTeleop.stop();
				s_startTeleop.start();
				break;
			case MATCH_END:
				gameTime = 10 * 60; // 2m 15s
				Robot.setAllEnabled(false);
				
				s_matchEnd.stop();
				s_matchEnd.start();
				break;
			default:
				break;
		}
		
	}
	
	private void resetField() {
		Game.getWorld().reset();
	}

	public boolean isEndGame(){
		return state == GameState.TELEOP && gameTime <= 30*60;
	}
	
	public static enum GameState {
		WAITING_FOR_PLAYERS,
		SETUP,
		AUTON,
		TELEOP,
		MATCH_END;
	}

	public GameState getState() {
		return state;
	}

	public void voteToStart(Player player) {
		if(!voted.contains(player)) voted.add(player);
	}
	
}
