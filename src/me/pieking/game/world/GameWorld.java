package me.pieking.game.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

import me.pieking.game.Game;
import me.pieking.game.Rand;
import me.pieking.game.Scheduler;
import me.pieking.game.gfx.Fonts;
import me.pieking.game.gfx.Images;
import me.pieking.game.gfx.Sprite;
import me.pieking.game.ship.Ship;
import me.pieking.game.ship.component.Component;
import me.pieking.game.world.Switch.Team;

public class GameWorld {
	public static final int BOOST_TIME = 10;
	
	public static final double ANGULAR_DAMPING = 50;
	public static final double LINEAR_DAMPING = 16;

	public static boolean shipAligned = false;
	
	public static Sprite field = Images.getSprite("field_temp.png");

	public World world;
	
	public double xOffset = 0;
	public double yOffset = 0;
	
	public List<GameObject> toRemove = new ArrayList<GameObject>();
	public List<GameObject> walls = new ArrayList<GameObject>();
	public List<GameObject> particles = new ArrayList<GameObject>();
	public List<PowerCube> cubes = new ArrayList<PowerCube>();
	public List<ScalePlatform> scalePlatforms = new ArrayList<ScalePlatform>();
	public List<Switch> scales = new ArrayList<Switch>();
	
	public Switch blueSwitch;
	public Switch redSwitch;
	
	public GameObject redExchangeSensor;
	public GameObject blueExchangeSensor;
	
	@SuppressWarnings("serial")
	public HashMap<Team, Integer> cubeStorage = new HashMap<Switch.Team, Integer>(){{
		put(Team.BLUE, 0);
		put(Team.RED, 0);
		put(Team.NONE, 0);
	}};
	
	public Player selfPlayer;
	public List<Player> players = new ArrayList<Player>();

	@SuppressWarnings("serial")
	public HashMap<Team, HashMap<Pentalty, Integer>> penalties = new HashMap<Team, HashMap<Pentalty, Integer>>(){{
		put(Team.BLUE, new HashMap<>());
		put(Team.RED, new HashMap<>());
		put(Team.NONE, new HashMap<>());
	}};
	
	public GameWorld(){
		initializeWorld();
		world.addListener(new GameListener());
	}
	
	private GameObject floor;
	private GameObject floor2;
	private GameObject floor3;
	private GameObject floor4;
	
	public static float FIELD_SCALE = 1.1f;
	double fieldXofs = 28.1;
	double fieldYofs = 11;
	
	public int blueScoreModSwitch = 1;
	public int blueScoreModScale = 1;
	public int redScoreModSwitch = 1;
	public int redScoreModScale = 1;
	
	@SuppressWarnings("serial")
	public HashMap<Team, Integer> score = new HashMap<Team, Integer>(){{
		put(Team.BLUE, 0);
		put(Team.RED, 0);
		put(Team.NONE, 0);
	}};
	
	private Scale scale;
	
	public static Vector2 mouseWorldPos = new Vector2();
	
	public Team power_boost = Team.NONE;
	public Team power_boost_queued = Team.NONE;
	public List<Team> power_boost_used = new ArrayList<>();
	public int power_boost_timer = 0;
	public int power_boost_level = 0;
	public int power_boost_level_red = 0;
	public int power_boost_level_blue = 0;
	
	public Team power_force = Team.NONE;
	public Team power_force_queued = Team.NONE;
	public List<Team> power_force_used = new ArrayList<>();
	public int power_force_timer = 0;
	public int power_force_level = 0;
	public int power_force_level_red = 0;
	public int power_force_level_blue = 0;
	
	public List<Team> power_levitate_used = new ArrayList<>();
	
	public int gameTime = ((2 * 60) + 30) * 60;

	private List<PowerCube> beingDeleted = new ArrayList<PowerCube>();
	
	public void initializeWorld() {
		// create the world
		this.world = new World();
		// create all your bodies/joints
		world.setGravity(new Vector2(0, 0));
		//world.setGravity(new Vector2(0, 0));
		
//		for(int i = 0; i < 20; i++){
//			Planet p;
//			
//			int size = 2;
//			if(i == 0){
//				p = new Planet(200 * size, world, 0, 130, Atmosphere.OXYGEN);
//			}else{
//				
//				p = new Planet(Rand.range(50 * size, 300 * size), world, ((i % 5) * 2000 * size) + Rand.range(-500 * size, 500 * size), ((i / 5) * 2000 * size) + Rand.range(-500 * size, 500 * size));
//				p.atm = Atmosphere.values()[Rand.range(0, Atmosphere.values().length-1)];
//			}
//
//			if(i == 0){
//				homePlanet = p;
//			}
//			planets.add(p);
//		}
		//planets.add(new Planet(100, world, 400, 400));
		//planets.add(new Planet(300, world, -600, -700));
		
		// create the floor
		
		floor = new GameObject();
		floor.color = new Color(0f, 0.5f, 0f, 1f);
		double w = (field.getWidth() * GameObject.SCALE * 0.05) / GameObject.SCALE * FIELD_SCALE;
		double h = (field.getHeight() * GameObject.SCALE * 0.05) / GameObject.SCALE * FIELD_SCALE;
		Rectangle floorRect = new Rectangle(w, 40 / GameObject.SCALE * FIELD_SCALE);
		floorRect.translate(w/2, Component.unitSize * 2 * FIELD_SCALE);
		BodyFixture f1 = new BodyFixture(floorRect);
		f1.setDensity(0.5f);
		floor.addFixture(f1);
		
		world.addBody(floor);
		
		floor2 = new GameObject();
		floor2.color = new Color(0f, 0.5f, 0f, 1f);
		Rectangle floor2Rect = new Rectangle(w, 40 / GameObject.SCALE * FIELD_SCALE);
		floor2Rect.translate(w/2, h - Component.unitSize * 2 * FIELD_SCALE);
		BodyFixture f2 = new BodyFixture(floor2Rect);
		f2.setDensity(0.5f);
		floor2.addFixture(f2);
		
		world.addBody(floor2);
		
		double holePos = 0.5;
		
		floor3 = new GameObject();
		floor3.color = new Color(0f, 0.5f, 0f, 1f);
		Rectangle floor3Rect = new Rectangle(40 / GameObject.SCALE * FIELD_SCALE, h * holePos);
		floor3Rect.translate(Component.unitSize * 16 * FIELD_SCALE, (h * 0.3)/2);
		BodyFixture f3 = new BodyFixture(floor3Rect);
		f3.setDensity(0.5f);
		floor3.addFixture(f3);
		world.addBody(floor3);
		
		GameObject floor3B = new GameObject();
		floor3B.color = new Color(0f, 0.5f, 0f, 1f);
		Rectangle floor3BRect = new Rectangle(40 / GameObject.SCALE * FIELD_SCALE, h * holePos);
		floor3BRect.translate(Component.unitSize * 16 * FIELD_SCALE, (h * 0.3)/2 +  h * holePos + (PowerCube.SIZE * 1.2));
		BodyFixture f3b = new BodyFixture(floor3BRect);
		f3b.setDensity(0.5f);
		floor3B.addFixture(f3b);
		world.addBody(floor3B);
		walls.add(floor3B);
		
		redExchangeSensor = new GameObject();
		redExchangeSensor.color = new Color(0.5f, 0.2f, 0.2f, 0.5f);
		Rectangle redExchangeSensorRect = new Rectangle(Component.unitSize * 5 * FIELD_SCALE, Component.unitSize * 5 * FIELD_SCALE);
		redExchangeSensorRect.translate(Component.unitSize * 13.5 * FIELD_SCALE, (h * 0.3)/2 + (h * holePos)/2);
		BodyFixture res = new BodyFixture(redExchangeSensorRect);
		res.setSensor(true);
		res.setDensity(0.5f);
		redExchangeSensor.addFixture(res);
		world.addBody(redExchangeSensor);
		walls.add(redExchangeSensor);
		
		GameObject slopeUL = new GameObject();
		Triangle t1 = new Triangle(new Vector2(-1 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(-1 * FIELD_SCALE, 2 * FIELD_SCALE));
		t1.translate(Component.unitSize * 16 * FIELD_SCALE + (40 / GameObject.SCALE * FIELD_SCALE), 40 / GameObject.SCALE * FIELD_SCALE + (Component.unitSize * 2 * FIELD_SCALE));
		BodyFixture tbf1 = new BodyFixture(t1);
		slopeUL.addFixture(tbf1);
		world.addBody(slopeUL);
		walls.add(slopeUL);
		
		GameObject slopeBL = new GameObject();
		Triangle t2 = new Triangle(new Vector2(-1 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(-1 * FIELD_SCALE, -4 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, -1 * FIELD_SCALE));
		t2.translate((Component.unitSize * 16 * FIELD_SCALE + (40 / GameObject.SCALE * FIELD_SCALE)), h - (40 / GameObject.SCALE * FIELD_SCALE + (Component.unitSize * 2 * FIELD_SCALE)) + (2 * FIELD_SCALE));
		BodyFixture tbf2 = new BodyFixture(t2);
		slopeBL.addFixture(tbf2);
		world.addBody(slopeBL);
		walls.add(slopeBL);
		
		GameObject slopeUR = new GameObject();
		Triangle t3 = new Triangle(new Vector2(-1 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, 2 * FIELD_SCALE));
		t3.translate(Component.unitSize * 16 * FIELD_SCALE + (40 / GameObject.SCALE * FIELD_SCALE), 40 / GameObject.SCALE * FIELD_SCALE + (Component.unitSize * 2 * FIELD_SCALE));
		t3.translate(w * 0.669, 0);
		BodyFixture tbf3 = new BodyFixture(t3);
		slopeUR.addFixture(tbf3);
		world.addBody(slopeUR);
		walls.add(slopeUR);
		
		GameObject slopeBR = new GameObject();
		Triangle t4 = new Triangle(new Vector2(-1 * FIELD_SCALE, -1 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, -4 * FIELD_SCALE), new Vector2(3 * FIELD_SCALE, -1 * FIELD_SCALE));
		t4.translate((Component.unitSize * 16 * FIELD_SCALE + (40 / GameObject.SCALE * FIELD_SCALE)), h - (40 / GameObject.SCALE * FIELD_SCALE + (Component.unitSize * 2 * FIELD_SCALE)) + (2 * FIELD_SCALE));
		t4.translate(w * 0.669, 0);
		BodyFixture tbf4 = new BodyFixture(t4);
		slopeBR.addFixture(tbf4);
		world.addBody(slopeBR);
		walls.add(slopeBR);
		
		double holePos2 = 0.82;
		
		floor4 = new GameObject();
		floor4.color = new Color(0f, 0.5f, 0f, 1f);
		Rectangle floor4Rect = new Rectangle(40 / GameObject.SCALE * FIELD_SCALE, h * holePos2);
		floor4Rect.translate(w - Component.unitSize * 16.5 * FIELD_SCALE, (h * 0.3)/2);
		BodyFixture f4 = new BodyFixture(floor4Rect);
		f4.setDensity(0.5f);
		floor4.addFixture(f4);
		world.addBody(floor4);
		
		GameObject floor4B = new GameObject();
		floor4B.color = new Color(0f, 0.5f, 0f, 1f);
		Rectangle floor4BRect = new Rectangle(40 / GameObject.SCALE * FIELD_SCALE, h * holePos2);
		floor4BRect.translate(w - Component.unitSize * 16.5 * FIELD_SCALE, (h * 0.3)/2 +  h * holePos2 + (PowerCube.SIZE * 1.2));
		BodyFixture f4b = new BodyFixture(floor4BRect);
		f4b.setDensity(0.5f);
		floor4B.addFixture(f4b);
		world.addBody(floor4B);
		walls.add(floor4B);
		
		blueExchangeSensor = new GameObject();
		blueExchangeSensor.color = new Color(0.2f, 0.2f, 0.5f, 0.5f);
		Rectangle blueExchangeSensorRect = new Rectangle(Component.unitSize * 5 * FIELD_SCALE, Component.unitSize * 5 * FIELD_SCALE);
		blueExchangeSensorRect.translate(w - Component.unitSize * 14 * FIELD_SCALE, (h * 0.3)/2 + (h * holePos2)/2);
		BodyFixture bes = new BodyFixture(blueExchangeSensorRect);
		bes.setSensor(true);
		bes.setDensity(0.5f);
		blueExchangeSensor.addFixture(bes);
		world.addBody(blueExchangeSensor);
		walls.add(blueExchangeSensor);
		
		walls.add(floor);
		walls.add(floor2);
		walls.add(floor3);
		walls.add(floor4);
		
		
//		addPowerCube(new PowerCube(0 + fieldXofs, 0 + fieldYofs, 0));
		
		for(int i = 0; i < 6; i++){
			addPowerCube(new PowerCube(-6.8 + fieldXofs, ((i-2) * 1.825) + fieldYofs - 0.15, 0));
			addPowerCube(new PowerCube(9.0 + fieldXofs, ((i-2) * 1.825) + fieldYofs - 0.15, 0));
		}
		
//		addPowerCube(new PowerCube(5 + fieldXofs, 0 + fieldYofs, 0));
//		addPowerCube(new PowerCube(5 + fieldXofs, 1 + fieldYofs, 0));
//		addPowerCube(new PowerCube(5 + fieldXofs, 2 + fieldYofs, 0));
//		addPowerCube(new PowerCube(5 + fieldXofs, -1 + fieldYofs, 0));
//		addPowerCube(new PowerCube(5 + fieldXofs, -2 + fieldYofs, 0));
		
//		addPowerCube(new PowerCube(-5 + fieldXofs, 0 + fieldYofs, 0));
//		addPowerCube(new PowerCube(-5 + fieldXofs, 1 + fieldYofs, 0));
//		addPowerCube(new PowerCube(-5 + fieldXofs, 2 + fieldYofs, 0));
//		addPowerCube(new PowerCube(-5 + fieldXofs, -1 + fieldYofs, 0));
//		addPowerCube(new PowerCube(-5 + fieldXofs, -2 + fieldYofs, 0));
		
		
		// actual
		
		addPowerCube(new PowerCube(-13 + fieldXofs, 0.79 + fieldYofs, 0));
		addPowerCube(new PowerCube(-13 + PowerCube.SIZE + fieldXofs, 0.79 - PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(-13 + PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(-13 + PowerCube.SIZE + PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 + PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(-13 + PowerCube.SIZE + PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 - PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(-13 + PowerCube.SIZE + PowerCube.SIZE + fieldXofs, 0.79 - PowerCube.SIZE/2 - PowerCube.SIZE/2 + fieldYofs, 0));
		
		addPowerCube(new PowerCube(15.3 + fieldXofs, 0.79 + fieldYofs, 0));
		addPowerCube(new PowerCube(15.3 - PowerCube.SIZE + fieldXofs, 0.79 - PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(15.3 - PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(15.3 - PowerCube.SIZE - PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 + PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(15.3 - PowerCube.SIZE - PowerCube.SIZE + fieldXofs, 0.79 + PowerCube.SIZE/2 - PowerCube.SIZE/2 + fieldYofs, 0));
		addPowerCube(new PowerCube(15.3 - PowerCube.SIZE - PowerCube.SIZE + fieldXofs, 0.79 - PowerCube.SIZE/2 - PowerCube.SIZE/2 + fieldYofs, 0));
		
		boolean[] switchOrientation = getRandomSwitchOrientation();
		
		blueSwitch = new Switch(10.15 + fieldXofs, 0 + fieldYofs, switchOrientation[0]);
		addScale(blueSwitch);
		scale = new Scale(0.07 + fieldXofs, 0 + fieldYofs, switchOrientation[1]);
		addScale(scale);
		redSwitch = new Switch(-10.15 + fieldXofs, 0 + fieldYofs, switchOrientation[2]);
		addScale(redSwitch);
		
//		GameObject go = new GameObject();
		
//		Rectangle r = new Rectangle(Component.unitSize * 2, Component.unitSize / 8);
//		r.translate(Component.unitSize/2, Component.unitSize/2);
//		BodyFixture bf = new BodyFixture(r);
//		bf.setFilter(new PlayerFilter(selfPlayer));
//		go.addFixture(bf);
//		
//		r = new Rectangle(Component.unitSize / 8, Component.unitSize * .75);
//		r.translate(-Component.unitSize/2, Component.unitSize / 8);
//		bf = new BodyFixture(r);
//		bf.setFilter(new PlayerFilter(selfPlayer));
//		go.addFixture(bf);
//		
//		r = new Rectangle(Component.unitSize / 8, Component.unitSize * .75);
//		r.translate(Component.unitSize/2 + Component.unitSize, Component.unitSize / 8);
//		bf = new BodyFixture(r);
//		bf.setFilter(new PlayerFilter(selfPlayer));
//		go.addFixture(bf);
//		
//		go.translate(19.5, 12);
//		
//		go.setMass(MassType.NORMAL);
//		go.applyForce(new Vector2(0, -80));
//		world.addBody(go);
//		particles.add(go);
		
//		GameObject floor2 = new GameObject();
//		floor2.color = Color.GRAY;
//		
//		Rectangle floorRect2 = new Rectangle(150 / GameObject.SCALE, 100 / GameObject.SCALE);
//		floorRect2.translate(75 / GameObject.SCALE, (Game.getHeight() - 50 - 40) / GameObject.SCALE);
//		BodyFixture f2 = new BodyFixture(floorRect2);
//		f2.setDensity(0.5f);
//		floor2.addFixture(f2);
//		
//		world.addBody(floor2);
//		
//		
//		GameObject floor3 = new GameObject();
//		floor3.color = Color.GRAY;
//		
//		Rectangle floorRect3 = new Rectangle(300 / GameObject.SCALE, 100 / GameObject.SCALE);
//		floorRect3.translate((Game.getWidth() - 150 + (150)) / GameObject.SCALE, (Game.getHeight() - 50 - 40) / GameObject.SCALE);
//		BodyFixture f3 = new BodyFixture(floorRect3);
//		f3.setDensity(0.5f);
//		floor3.addFixture(f3);
//		
//		world.addBody(floor3);
		
		
//		for(int i = 0; i < 6; i++){
//			GameObject block = new GameObject();
//			block.color = Color.DARK_GRAY;
//			
//			Rectangle blockRect = new Rectangle(30 / GameObject.SCALE, 30 / GameObject.SCALE);
//			blockRect.translate((660 + (5 * i)) / GameObject.SCALE, (Game.getHeight() - 50 - 40 - 200 - (5 * i)) / GameObject.SCALE);
//			BodyFixture fx = new BodyFixture(blockRect);
//			fx.setDensity(0.5f);
//			block.addFixture(fx);
//			block.setMass(MassType.NORMAL);
//			block.magnetable = true;
//			block.magneticLevel = 0.5f;
//			world.addBody(block);
//		}
		
//		Rectangle floorRect2 = new Rectangle(20 / GameObject.SCALE, 300 / GameObject.SCALE);
//		BodyFixture f2 = new BodyFixture(floorRect2);
//		f2.setDensity(0.5f);
//		floor.addFixture(f2);
//		floorRect2.translate(-250 / GameObject.SCALE, -150 / GameObject.SCALE);
//		
//		Rectangle floorRect3 = new Rectangle(20 / GameObject.SCALE, 300 / GameObject.SCALE);
//		BodyFixture f3 = new BodyFixture(floorRect3);
//		f3.setDensity(0.5f);
//		floor.addFixture(f3);
//		floorRect3.translate(250 / GameObject.SCALE, -150 / GameObject.SCALE);
//		
//		floor.setMass(MassType.INFINITE);
//		// move the floor down a bit
//		//floor.translate(0.0, -4.0);
//		floor.translate(300 / GameObject.SCALE, 400 / GameObject.SCALE);
//		this.world.addBody(floor);
//		
//		
//		GameObject water = new GameObject();
//		water.color = new Color(0.2f, 0.2f, 1f, 0.5f);
//		
//		Rectangle waterR = new Rectangle(500 / GameObject.SCALE, 200 / GameObject.SCALE);
//		BodyFixture fx = new BodyFixture(waterR);
//		fx.setSensor(true);
//		water.addFixture(fx);
//		//water.translate(0, -100 / GameObject.SCALE);
//		waterR.translate(300 / GameObject.SCALE, 300 / GameObject.SCALE);
//		waterR.translate(0, 0 / GameObject.SCALE);
//		water.setMass(MassType.INFINITE);
//		this.world.addBody(water);
//		this.water = water;
//		// create a triangle object
//		Triangle triShape = new Triangle(
//				new Vector2(0.0, 0.5), 
//				new Vector2(-0.5, -0.5), 
//				new Vector2(0.5, -0.5));
//		GameObject triangle = new GameObject();
//		triangle.addFixture(triShape);
//		triangle.setMass(MassType.NORMAL);
//		triangle.translate(-1.0, 2.0);
//		// test having a velocity
//		triangle.getLinearVelocity().set(5.0, 0.0);
//		this.world.addBody(triangle);
		
//		for(int i = 0; i < 0; i++){
//			GameObject r1 = new GameObject();
//			BodyFixture fixture = new BodyFixture(new Circle(2 / GameObject.SCALE));
//			fixture.setDensity(0.1f + (0.01f * i));
//			r1.addFixture(fixture);
//			fixture.setRestitution(0);
//			
//			r1.setMass(MassType.NORMAL);
//			r1.translate((100 + ((i%100) * 2)) / GameObject.SCALE, (300 - ((i/100)*10)) / GameObject.SCALE);
//			r1.color = Color.CYAN;
//			this.world.addBody(r1);
//		}
		
//		for(int i = 0; i < 0; i++){
//		//GameObject r1 = createRacecarFixture();
//			GameObject r1 = new GameObject();
//			r1.desiredRotation = 90f;
//			BodyFixture fixture = new BodyFixture(new Rectangle(Rand.range(10, 60) / GameObject.SCALE, Rand.range(10, 60) / GameObject.SCALE));
//			fixture.setDensity(0.5f);
//			r1.addFixture(fixture);
//			fixture.setRestitution(0.5d);
//			
//			r1.setMass(MassType.NORMAL);
//			r1.translate(400 / GameObject.SCALE, 300 / GameObject.SCALE);
//			//System.out.println(r1 + " " + r1.desiredRotation);
//			r1.desiredRotation = Rand.range(0f, 360f);
//			this.world.addBody(r1);
//		}
		
//		GameObject obj = createBoatObject();
//		obj.translate(300 / GameObject.SCALE, 100 / GameObject.SCALE);
//		world.addBody(obj);
		
		/*
		GameObject r2 = new GameObject();
		BodyFixture fixture2 = new BodyFixture(new Rectangle(200 / GameObject.SCALE, 50 / GameObject.SCALE));
		fixture2.setDensity(1f);
		r2.addFixture(fixture2);
		fixture2.setRestitution(0.5d);
		
		r2.setMass(MassType.NORMAL);
		r2.translate(200 / GameObject.SCALE, 300 / GameObject.SCALE);
		r2.desiredRotation = 10f;
		
		this.world.addBody(r2);*/
		
		//r1.applyForce(new Vector2(-100, 100));
		//r1.applyTorque(1d);
//		// create a circle
//		Circle cirShape = new Circle(0.5);
//		GameObject circle = new GameObject();
//		circle.addFixture(cirShape);
//		circle.setMass(MassType.NORMAL);
//		circle.translate(2.0, 2.0);
//		// test adding some force
//		circle.applyForce(new Vector2(-100.0, 0.0));
//		// set some linear damping to simulate rolling friction
//		circle.setLinearDamping(0.05);
//		this.world.addBody(circle);
//		
//		// try a rectangle
//		Rectangle rectShape = new Rectangle(1.0, 1.0);
//		GameObject rectangle = new GameObject();
//		rectangle.addFixture(rectShape);
//		rectangle.setMass(MassType.NORMAL);
//		rectangle.translate(0.0, 2.0);
//		rectangle.getLinearVelocity().set(-5.0, 0.0);
//		this.world.addBody(rectangle);
//		
//		// try a polygon with lots of vertices
//		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
//		GameObject polygon = new GameObject();
//		polygon.addFixture(polyShape);
//		polygon.setMass(MassType.NORMAL);
//		polygon.translate(-2.5, 2.0);
//		// set the angular velocity
//		polygon.setAngularVelocity(Math.toRadians(-20.0));
//		this.world.addBody(polygon);
//		
//		// try a compound object
//		Circle c1 = new Circle(0.5);
//		BodyFixture c1Fixture = new BodyFixture(c1);
//		c1Fixture.setDensity(0.5);
//		Circle c2 = new Circle(0.5);
//		BodyFixture c2Fixture = new BodyFixture(c2);
//		c2Fixture.setDensity(0.5);
//		Rectangle rm = new Rectangle(2.0, 1.0);
//		// translate the circles in local coordinates
//		c1.translate(-1.0, 0.0);
//		c2.translate(1.0, 0.0);
//		GameObject capsule = new GameObject();
//		capsule.addFixture(c1Fixture);
//		capsule.addFixture(c2Fixture);
//		capsule.addFixture(rm);
//		capsule.setMass(MassType.NORMAL);
//		capsule.translate(0.0, 4.0);
//		this.world.addBody(capsule);
//		
//		GameObject issTri = new GameObject();
//		issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
//		issTri.setMass(MassType.NORMAL);
//		issTri.translate(2.0, 3.0);
//		this.world.addBody(issTri);
//		
//		GameObject equTri = new GameObject();
//		equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
//		equTri.setMass(MassType.NORMAL);
//		equTri.translate(3.0, 3.0);
//		this.world.addBody(equTri);
//		
//		GameObject rightTri = new GameObject();
//		rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
//		rightTri.setMass(MassType.NORMAL);
//		rightTri.translate(4.0, 3.0);
//		this.world.addBody(rightTri);
//		
//		GameObject cap = new GameObject();
//		cap.addFixture(new Capsule(1.0, 0.5));
//		cap.setMass(MassType.NORMAL);
//		cap.translate(-3.0, 3.0);
//		this.world.addBody(cap);
//		
//		GameObject slice = new GameObject();
//		slice.addFixture(new Slice(0.5, Math.toRadians(120)));
//		slice.setMass(MassType.NORMAL);
//		slice.translate(-3.0, 3.0);
//		this.world.addBody(slice);
}
	
	private boolean[] getRandomSwitchOrientation() {
		
		List<boolean[]> poss = new ArrayList<boolean[]>();
		poss.add(new boolean[]{true, false, true});
		poss.add(new boolean[]{false, true, false});
		poss.add(new boolean[]{true, true, true});
		poss.add(new boolean[]{false, false, false});
		
		return poss.get(Rand.range(0, poss.size()-1));
	}

	public void addPlayer(Player pl) {
		players.add(pl);
		world.addBody(pl.base);
	}

	public void render(Graphics2D g){
		
		//System.out.println("r3");
		AffineTransform ot = g.getTransform();
		
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, 1);
		AffineTransform move = AffineTransform.getTranslateInstance(0, 0);
		g.transform(yFlip);
		g.transform(move);
		
		if(shipAligned) g.rotate(-getSelf().base.getTransform().getRotation(), Game.getWidth()/2, Game.getHeight()/2);
		AffineTransform ofs = AffineTransform.getTranslateInstance(xOffset, yOffset);
		g.transform(ofs);

		
		List<Body> bodies = new ArrayList<Body>();
		bodies.addAll(world.getBodies());
		
		for(Body b : bodies){
			GameObject o = (GameObject) b;
			
//			if(Game.game.input.leftMouse && o.pullable){
//				if(o.getMass().getType() == MassType.NORMAL){
//					Point frameloc = Game.game.frame.getLocation();
//					frameloc.x += Game.game.frame.getInsets().right;
//					frameloc.y += Game.game.frame.getInsets().top;
//					float width = 20 - ((float) new Point((int)(o.getWorldCenter().x * GameObject.SCALE), (int)(o.getWorldCenter().y * GameObject.SCALE)).distance(new Point2D.Float(MouseInfo.getPointerInfo().getLocation().x - frameloc.x, MouseInfo.getPointerInfo().getLocation().y - frameloc.y)) / 10f);
//					
//					if(width < 1) width = 1;
//					
//					g.setColor(o.color.darker());
//					g.setStroke(new BasicStroke(width));
//					g.drawLine((int)(o.getWorldCenter().x * GameObject.SCALE), (int)(o.getWorldCenter().y * GameObject.SCALE), (int)(MouseInfo.getPointerInfo().getLocation().x - frameloc.x - xOffset), (int) (MouseInfo.getPointerInfo().getLocation().y - frameloc.y - yOffset));
//					g.setStroke(new BasicStroke(1f));
//				}
//			}
//			o.render(g);
			
		}
		
		g.drawImage(field.getImage(), 0, 0, (int)(field.getWidth() * GameObject.SCALE * 0.05 * FIELD_SCALE), (int)(field.getHeight() * GameObject.SCALE * 0.05 * FIELD_SCALE), null);
		
		List<GameObject> wal = new ArrayList<GameObject>();
		wal.addAll(walls);
		for(GameObject o : wal){
			o.render(g);
		}
		
		List<GameObject> par = new ArrayList<GameObject>();
		par.addAll(particles);
		for(GameObject o : par){
			o.render(g);
		}
		
		List<Switch> sca = new ArrayList<Switch>();
		sca.addAll(scales);
//		System.out.println(scales + " " + sca);
		for(Switch o : sca){
			if(o != null) o.render(g);
		}
		
		List<PowerCube> cub = new ArrayList<PowerCube>();
		cub.addAll(cubes);
		for(PowerCube o : cub){
			if(o != null) o.render(g);
		}
		
		List<Player> pl = new ArrayList<Player>();
		pl.addAll(players);
		for(Player p : pl){
			if(p != null) p.render(g);
		}
		
//		if(Game.game.input.rightMouse){
//			Point frameloc = Game.game.frame.getLocation();
//			frameloc.x += Game.game.frame.getInsets().right;
//			frameloc.y += Game.game.frame.getInsets().top;
//			g.drawLine(Game.game.input.rStart.x - frameloc.x, Game.game.input.rStart.y - frameloc.y, MouseInfo.getPointerInfo().getLocation().x - frameloc.x, MouseInfo.getPointerInfo().getLocation().y - frameloc.y);
//		}
		
		g.setColor(Color.GREEN);
		g.drawRect(0, 0, 10, 10);
		
		g.setTransform(ot);
		
		renderHUD(g);
	}
	
	public void render(Graphics2D g, double xOffset2, double yOffset2, double scale){
		
		double realScale = GameObject.SCALE;
		GameObject.SCALE = scale;
		
		//System.out.println("r3");
		AffineTransform ot = g.getTransform();
		
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, 1);
		AffineTransform move = AffineTransform.getTranslateInstance(0, 0);
		g.transform(yFlip);
		g.transform(move);
		
		AffineTransform ofs = AffineTransform.getTranslateInstance(xOffset2, yOffset2);
		g.transform(ofs);
		
		List<Body> bodies = new ArrayList<Body>();
		bodies.addAll(world.getBodies());
		
		floor.render(g);
		
		List<GameObject> par = new ArrayList<GameObject>();
		par.addAll(particles);
		for(GameObject o : par){
			o.render(g);
		}
		
		List<PowerCube> cub = new ArrayList<PowerCube>();
		cub.addAll(cubes);
		for(PowerCube o : cub){
			if(o != null) o.base.render(g);
		}
		
		List<Switch> sca = new ArrayList<Switch>();
		sca.addAll(scales);
//		System.out.println(scales + " " + sca);
		for(Switch o : sca){
			if(o != null) o.render(g);
		}
		
		List<Player> pl = new ArrayList<Player>();
		pl.addAll(players);
		for(Player p : pl){
			if(p != null) p.ship.render(g);
		}
		
//		if(Game.game.input.rightMouse){
//			Point frameloc = Game.game.frame.getLocation();
//			frameloc.x += Game.game.frame.getInsets().right;
//			frameloc.y += Game.game.frame.getInsets().top;
//			g.drawLine(Game.game.input.rStart.x - frameloc.x, Game.game.input.rStart.y - frameloc.y, MouseInfo.getPointerInfo().getLocation().x - frameloc.x, MouseInfo.getPointerInfo().getLocation().y - frameloc.y);
//		}
		
//		g.setColor(Color.GREEN);
//		g.drawRect(0, 0, 10, 10);
		
		g.setTransform(ot);
		
		renderHUD(g);
		
		GameObject.SCALE = realScale;
	}
	
	public AffineTransform getTransform(){
		
		AffineTransform trans = new AffineTransform();
		
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, 1);
		AffineTransform move = AffineTransform.getTranslateInstance(0, 0);
		trans.concatenate(yFlip);
		trans.concatenate(move);
		
		AffineTransform ofs = AffineTransform.getTranslateInstance(xOffset, yOffset);
		trans.concatenate(ofs);
		
		return trans;
	}
	
	private void renderHUD(Graphics2D g) {
		
		if(Ship.buildMode){
			g.setFont(Fonts.gamer.deriveFont(40f));
			g.setColor(Color.WHITE);
			g.drawString("BUILD MODE", 10, 44);
			
			
			g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
			g.fillRect(Game.getWidth() - 100, Game.getHeight() - 100, 100, 100);
			
			Player p = Game.getWorld().getSelf();
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
		
//		System.out.println(power_boost + " " + power_boost_queued);
		
		g.setFont(Fonts.pixelmix.deriveFont(20f));
		
		if(power_boost != Team.NONE){
			g.setColor(power_boost.color);
			String s = "boost (" + (power_boost_timer / 60) + ")";
			if(power_boost_queued != Team.NONE) s += " (" + power_boost_queued + " queued)";
			g.drawString(s, 10, Game.getHeight() - 20);
		}
		
		if(power_force != Team.NONE){
			g.setColor(power_force.color);
			String s = "force (" + (power_force_timer / 60) + ")";
			if(power_force_queued != Team.NONE) s += " (" + power_force_queued + " queued)";
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
		
		double time = gameTime / 60;
		
		int seconds = (int) (time % 60);
		String secondsStr = (seconds < 10 ? "0" : "") + seconds; 
		int minutes = (int) (time / 60);
		String minutesStr = "" + minutes; 
		
		String timeS = minutesStr + ":" + secondsStr;
		g.setColor(Color.DARK_GRAY);
		g.drawString(timeS, Game.getWidth()/2 - g.getFontMetrics().stringWidth(timeS)/2 - 3, scoreBoardY - 34 - 3);
		g.setColor(getSelf().team.color);
		g.drawString(timeS, Game.getWidth()/2 - g.getFontMetrics().stringWidth(timeS)/2, scoreBoardY - 34);
		
		g.setFont(Fonts.pixelLCD.deriveFont(28f));
		
		int colonWidth = g.getFontMetrics().stringWidth(":")/2;
		
		g.setColor(Color.DARK_GRAY);
		int redScore = getScoreWithPenalites(Team.RED);
		int blueScore = getScoreWithPenalites(Team.BLUE);
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
		
		g.drawImage(PowerCube.spr.getImage(), Game.getWidth() - 80, Game.getHeight() - 80, 60, 60, null);
		g.setFont(Fonts.pixeled.deriveFont(14f));
		g.setColor(Color.DARK_GRAY);
		g.drawString("" + cubeStorage.get(getSelf().team), Game.getWidth() - 80 + 53, Game.getHeight() - 80 + 65);
		g.setColor(Color.WHITE);
		g.drawString("" + cubeStorage.get(getSelf().team), Game.getWidth() - 80 + 55, Game.getHeight() - 80 + 67);
		
	}

	public void tick(){
		
		//System.out.println(GameObject.SCALE);
		//System.out.println(Math.round((float)(GameObject.SCALE*100f))/100f);
		
		//System.out.println("aa");
		
		//System.out.println(GameObject.SCALE + " " + GameObject.DESIRED_SCALE);
		
		GameObject.DESIRED_SCALE = (Math.round((float)(GameObject.DESIRED_SCALE*100f))/100f);
		GameObject.SCALE += (GameObject.DESIRED_SCALE - GameObject.SCALE + GameObject.ZOOM_SCALE)/4f;
		//GameObject.SCALE = GameObject.DESIRED_SCALE;
		GameObject.SCALE = (Math.round((float)(GameObject.SCALE*10f))/10f);
		//System.out.println(GameObject.SCALE);
		if(GameObject.SCALE < 0.1) {
			//GameObject.SCALE = 1;
			//GameObject.DESIRED_SCALE = 1;
			GameObject.ZOOM_SCALE *= 0.95;
		}
			
		//System.out.println(GameObject.SCALE);
		
		if(Game.isServer()){
			xOffset = -5 * GameObject.SCALE + Game.getDisp().realWidth/2;
    		yOffset = -5 * GameObject.SCALE + Game.getDisp().realHeight/2;
		}else{
    		xOffset = -selfPlayer.base.getWorldCenter().x * GameObject.SCALE + Game.getDisp().realWidth/2;
    		yOffset = -selfPlayer.base.getWorldCenter().y * GameObject.SCALE + Game.getDisp().realHeight/2;
		}
		
//		xOffset = 0;
//		yOffset = 0;
		
		xOffset = (Math.round((float)(xOffset*100f))/100f);
		yOffset = (Math.round((float)(yOffset*100f))/100f);
		
		
		AffineTransform trans = new AffineTransform();
		
		trans.concatenate(Game.getWorld().getTransform());
		trans.scale(GameObject.SCALE, GameObject.SCALE);
		trans.translate(fieldXofs, fieldYofs);
		
		Point mPos = Game.mouseLoc();
		
		Point pt = new Point();
		try {
			AffineTransform inv = trans.createInverse();
			Point2D.Float f = new Point2D.Float(0, 0);
			inv.transform(mPos, f);
//			System.out.println(f);
		}catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	
		
//		if(Game.getTime() % 30 == 0){
//			
//			double dx = selfPlayer.getLocation().x - 1;
//			double dy = selfPlayer.getLocation().y - 0;
//			
//			System.out.println(dx + " " + dy);
//			
//			double angle = Math.atan2(dy, dx) + Math.toRadians(90);
//			
//			System.out.println(Math.toDegrees(angle));
//			
//			Bullet b = new Bullet("null", 1, 0, angle);
//			addBullet(b);
//		}
		
		//System.out.println("bb");
		
		if(gameTime > 0) gameTime--;
		
		List<Body> bodies = new ArrayList<Body>();
		bodies.addAll(world.getBodies());
		for(Body b : bodies){
			GameObject o = (GameObject) b;
			//System.out.println(o.getWorldCenter());
//			if(water != null){
//				if(o != water && o.getMass().getType() == MassType.NORMAL){
//					
//					double contains = water.containsFixture(o);
//					System.out.println(contains + " " + o.getMass().getMass());
//					if(contains > o.getMass().getMass()){
//						//System.out.println(o.getMass().getCenter().copy().subtract(o.getWorldCenter()).x);
//						//System.out.println((o.getMass().getCenter().x-o.getWorldCenter().x/GameObject.SCALE) + " " + o.getWorldCenter().x);
//						//System.out.println(contains + " " + o.getMass().getMass());
//						o.applyForce(new Vector2(0, (-world.getGravity().y * 1.5f) * o.getMass().getMass()));
//						
//						//System.out.println(o + " " + o.desiredRotation());
//						//System.out.println(o + " " + o.desiredRotation);
//						float desiredRot = o.desiredRotation;
//						
//						float rot = (float) Math.toDegrees(o.getTransform().getRotation());
//						//rot += 90;
//						//rot %= 360;
//						//rot += 180;
//						//rot = 180;
//						rot += desiredRot;
//						//System.out.println(rot - desiredRot);
//						
//						float correction = (float) (-rot / GameObject.SCALE);
//						
//						//System.out.println(o.getMass().getMass());
//						float range = (float) 2.4f;
//						
//						//System.out.println(rot + " " + correction);
//							if(correction > o.getTorque() + range){
//								correction = 0;
//							}else if(correction < o.getTorque() - range){
//								correction = 0;
//							}
//						o.applyTorque(correction * o.getMass().getMass());
//					}
//				}
//			}
			
//			if(Game.game.input.leftMouse && o.pullable){
//				//System.out.println(o.getMass().getMass());
//				Point frameloc = Game.game.frame.getLocation();
//				frameloc.x += Game.game.frame.getInsets().right;
//				frameloc.y += Game.game.frame.getInsets().top;
//				//System.out.println();
//				Vector2 mouse = new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - frameloc.getX() - xOffset, MouseInfo.getPointerInfo().getLocation().getY() - frameloc.getY() - yOffset);
//				//System.out.println(mouse + " " + o.getWorldCenter());
//				o.applyForce(mouse.subtract(o.getWorldCenter().multiply(GameObject.SCALE)).multiply(0.1f).multiply(o.getMass().getMass()));
//				//o.applyForce(new Vector2(new Vector2(), new Vector2(o.getLocalCenter(), y)));
//			}
		}
		
//		if(Game.game.input.rightMouse && Game.game.time % 5 == 0){
//			Point frameloc = Game.game.frame.getLocation();
//			frameloc.x += Game.game.frame.getInsets().right;
//			frameloc.y += Game.game.frame.getInsets().top;
//			
//			GameObject go = new GameObject();
//			BodyFixture fix = new BodyFixture(new Circle(2 / GameObject.SCALE));
//			fix.setDensity(0.5f);
//			go.addFixture(fix);
//			go.translate((Game.game.input.rStart.x - frameloc.x) / GameObject.SCALE, (Game.game.input.rStart.y - frameloc.y) / GameObject.SCALE);
//			go.setMass(MassType.NORMAL);
//			
//			Vector2 mouse = new Vector2(MouseInfo.getPointerInfo().getLocation().getX() - frameloc.getX(), MouseInfo.getPointerInfo().getLocation().getY() - frameloc.getY());
//			go.applyForce(mouse.subtract(go.getWorldCenter().multiply(GameObject.SCALE)).multiply(4f).multiply(go.getMass().getMass()));
//			go.color = new Color(64, 64, (go.color.getBlue()));
//			world.addBody(go);
//			
//			Scheduler.delayedTask(new Runnable() {
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					toRemove.add(go);
//				}
//			}, 60 * 5);
//			//g.drawLine(Game.game.input.rStart.x - frameloc.x, Game.game.input.rStart.y - frameloc.y, MouseInfo.getPointerInfo().getLocation().x - frameloc.x, MouseInfo.getPointerInfo().getLocation().y - frameloc.y);
//		}
		
		if(power_boost_timer > 0){
			if(power_boost_level % 2 == 0){ // 0 or 2
    			if(power_boost == Team.RED) redScoreModSwitch = 2;
    			if(power_boost == Team.BLUE) blueScoreModSwitch = 2;
			}
			
			if(power_boost_level >= 1){ // 1 or 2
    			if(power_boost == Team.RED) redScoreModScale = 2;
    			if(power_boost == Team.BLUE) blueScoreModScale = 2;
			}
			
			power_boost_timer--;
			if(power_boost_timer == 0){
				redScoreModSwitch = 1;
				redScoreModScale = 1;
				blueScoreModSwitch = 1;
				blueScoreModScale = 1;
				
				power_boost = Team.NONE;
				if(power_boost_queued != Team.NONE){
					forceBoost(power_boost_queued);
					power_boost_queued = Team.NONE;
				}
			}
		}
		
		if(power_force_timer > 0){
			if(power_force_level % 2 == 0){ // 0 or 2
				if(power_force == Team.RED) redSwitch.setOwnerOverride(Team.RED);
				if(power_force == Team.BLUE) blueSwitch.setOwnerOverride(Team.BLUE);
			}
			
			if(power_force_level >= 1){ // 1 or 2
				scale.setOwnerOverride(power_force);
			}
			
			power_force_timer--;
			if(power_force_timer == 0){
				power_force = Team.NONE;
				
				redSwitch.setOwnerOverride(Team.NONE);
				blueSwitch.setOwnerOverride(Team.NONE);
				scale.setOwnerOverride(Team.NONE);
				
				if(power_force_queued != Team.NONE){
					forceForce(power_force_queued);
					power_force_queued = Team.NONE;
				}
			}
		}
		
		List<PowerCube> cub = new ArrayList<PowerCube>();
		cub.addAll(cubes);
		for(PowerCube c : cub){
			if(beingDeleted .contains(c)) continue;
			if(redExchangeSensor.contains(c.base.getWorldCenter())){
				cubeStorage.put(Team.RED, cubeStorage.get(Team.RED) + 1);
				beingDeleted.add(c);
				c.base.setLinearVelocity(-40, 0);
				Scheduler.delayedTask(() -> {
					removeCube(c);
				}, 30);
			}else if(blueExchangeSensor != null && blueExchangeSensor.contains(c.base.getWorldCenter())){
				cubeStorage.put(Team.BLUE, cubeStorage.get(Team.BLUE) + 1);
				beingDeleted.add(c);
				c.base.setLinearVelocity(40, 0);
				Scheduler.delayedTask(() -> {
					removeCube(c);
				}, 30);
			}
		}
		
		if(Game.getTime() % 60 == 0){
    		if(blueSwitch.getOwner() == Team.BLUE){
    			addScore(Team.BLUE, 1 * blueScoreModSwitch);
    		}
    		
    		if(redSwitch.getOwner() == Team.RED){
    			addScore(Team.RED, 1 * redScoreModSwitch);
    		}
    		
    		if(scale.getOwner() == Team.BLUE){
    			addScore(Team.BLUE, 1 * blueScoreModSwitch);
    		}else if(scale.getOwner() == Team.RED){
    			addScore(Team.RED, 1 * redScoreModSwitch);
    		}
		}
		
		for(Player p : players){
			if(p != null) p.tick();
		}
		
		List<GameObject> par = new ArrayList<GameObject>();
		par.addAll(particles);
		for(GameObject o : par){
			if(System.currentTimeMillis() >= o.destructionTime){
				if(world.removeBody(o)){
					particles.remove(o);
				}
			}
		}
		
		List<Switch> sca = new ArrayList<Switch>();
		sca.addAll(scales);
		for(Switch o : sca){
			if(o != null) o.tick();
		}
		
		List<GameObject> rem = new ArrayList<GameObject>();
		rem.addAll(toRemove);
		for(GameObject o : rem){
			world.removeBody(o);
			toRemove.remove(o);
		}
		
		try{
			world.update(1d/60d);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//System.out.println("ff");
	}
	
	private void addScore(Team blue, int i) {
		score.put(blue, score.get(blue) + i);
	}

	public GameObject createBoatObject(){
		GameObject r1 = new GameObject();
		BodyFixture bf1 = r1.addFixture(new Polygon(new Vector2(180 / GameObject.SCALE, 300 / GameObject.SCALE), new Vector2(100 / GameObject.SCALE, 200 / GameObject.SCALE), new Vector2(0 / GameObject.SCALE, 0 / GameObject.SCALE)));
		bf1.setDensity(0.5f);
		
//		Rectangle w1 = new Rectangle(44d / GameObject.SCALEd, 14d / GameObject.SCALEd);
//		w1.translate(0, -28 / GameObject.SCALEd);
//		BodyFixture bf2 = r1.addFixture(w1);
//		bf2.setDensity(0.1f);
		
//		Rectangle w2 = new Rectangle(44d / GameObject.SCALEd, 12d / GameObject.SCALEd);
//		w2.translate(0, 20 / GameObject.SCALEd);
//		BodyFixture bf3 = r1.addFixture(w2);
//		bf3.setDensity(0.1f);
		
		r1.setMass(MassType.NORMAL);
		r1.desiredRotation = (float) GameObject.SCALE;
		
		return r1;
	}
	
	public Player getSelf(){
		return selfPlayer;
	}

	public Player getPlayer(String name) {
		for(Player p : players){
//			System.out.println(p.name);
			if(p != null && p.name != null) if(p.name.equals(name)) return p;
		}
		return null;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void removePlayer(Player player) {
		players.remove(player);
		if(player.base != null) world.removeBody(player.base);
	}
	
	public void addParticle(GameObject obj){
		particles.add(obj);
		world.addBody(obj);
	}
	
	public void addPowerCube(PowerCube obj){
		cubes.add(obj);
		world.addBody(obj.base);
	}

	public Component getComponent(GameObject go) {
		for(Player p : players){
			if(p.ship == null) continue;
			Component c = p.ship.getComponent(go);
			if(c != null) return c;
		}
		return null;
	}

	public void removeComponent(Component comp) {
		for(Player p : players){
			Component c = p.ship.getComponent(comp.lastBody);
			if(c != null) {
				p.destroyComponent(c);
			}
		}
	}

	public Player getPlayer(Component comp) {
		for(Player p : players){
			Component c = p.ship.getComponent(comp.lastBody);
			if(c != null) return p;
		}
		return null;
	}

	public boolean isPowerCube(GameObject o) {
		for(PowerCube b : cubes){
			if(b.base == o) return true;
		}
		return false;
	}
	
	public List<PowerCube> getCubes(){
		List<PowerCube> pc = new ArrayList<PowerCube>();
		pc.addAll(cubes);
		return pc;
	}

	public void removeCube(PowerCube c) {
		cubes.remove(c);
		world.removeBody(c.base);
		beingDeleted.remove(c);
	}
	
	public void addScale(Switch scale){
		scales.add(scale);
		scalePlatforms.add(scale.getRedPlatform());
		scalePlatforms.add(scale.getBluePlatform());
		world.addBody(scale.getBluePlatform().base);
		world.addBody(scale.getRedPlatform().base);
		
//		if(!(scale instanceof Scale)){
			world.addBody(scale.walls);
			walls.add(scale.walls);
//		}
		
	}
	
	public boolean isCubeOnScale(PowerCube cube) {
		for(ScalePlatform sp : scalePlatforms) {
			if(sp.base.contains(cube.base.getWorldCenter())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean useBoost(Team team){
		if(power_force == team) return false;
		System.out.println("boost " + power_boost_used.contains(team));
		if(power_boost_used.contains(team)) return false;
		
		power_boost_used.add(team);
		
		if(power_boost != Team.NONE) {
			power_boost_queued = team;
			return true;
		}
		
		forceBoost(team);
		
		return true;
	}
	
	public void forceBoost(Team team){
		power_boost = team;
		power_boost_timer = 60 * BOOST_TIME;
		
		if(team == Team.RED) power_boost_level = power_boost_level_red;
		if(team == Team.BLUE) power_boost_level = power_boost_level_blue;
		
	}
	
	public boolean useForce(Team team){
		if(power_boost == team) return false;
		if(power_force_used.contains(team)) return false;
		
		power_force_used.add(team);
		
		if(power_force != Team.NONE) {
			power_force_queued = team;
			return true;
		}
		
		forceForce(team);
		
		return true;
	}
	
	public void forceForce(Team team){
		power_force = team;
		power_force_timer = 60 * BOOST_TIME;
		
		if(team == Team.RED) power_force_level = power_force_level_red;
		if(team == Team.BLUE) power_force_level = power_force_level_blue;
	}
	
	public boolean useLevitate(Team team){
		if(power_levitate_used.contains(team)) return false;
		power_levitate_used.add(team);
		return true;
	}
	
	public void resetPowerups(){
		power_boost = Team.NONE;
		power_boost_queued = Team.NONE;
		power_boost_timer = 0;
		power_boost_level_red = 0;
		power_boost_level_blue = 0;
		power_boost_used.clear();
		
		power_force = Team.NONE;
		power_force_queued = Team.NONE;
		power_force_timer = 0;
		power_force_level_red = 0;
		power_force_level_blue = 0;
		power_force_used.clear();
		
		power_levitate_used.clear();
	}
	
	public int getScoreWithPenalites(Team t){
		int base = score.get(t);
		Team other = t.getOpposite();
		
		for(Pentalty p : penalties.get(other).keySet()){ // we check the other team's penalties because penalties add to your rival's score
			int num = penalties.get(other).get(p);
			switch(p){
				case FOUL:
					base += 5 * num;
					break;
				case TECH_FOUL:
					base += 25 * num;
					break;
				default:
					break;
			}
		}
		
		return base;
	}
	
}
