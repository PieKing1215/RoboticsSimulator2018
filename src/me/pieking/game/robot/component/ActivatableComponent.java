package me.pieking.game.robot.component;

import java.util.ArrayList;
import java.util.List;

public class ActivatableComponent extends Component {

	public boolean activated = false;
	public List<Integer> actKeys = new ArrayList<Integer>();
	public List<Integer> deactKeys = new ArrayList<Integer>();
	
	public boolean toggleMode = false;
	
	public ActivatableComponent(int x, int y, int width, int height, int rot, int maxHealth) {
		super(x, y, width, height, rot, maxHealth);
	}

	public void activate(){
		activated = true;
	}
	
	public void deactivate(){
		activated = false;
	}
	
	public void toggle(){
		if(activated){
			deactivate();
		}else{
			activate();
		}
	}
	
	@Override
	public String getDisplayName() {
		return "Activatable";
	}
	
}
