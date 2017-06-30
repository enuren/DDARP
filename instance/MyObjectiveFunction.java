package instance;

import java.util.ArrayList;

import solution.Route;

public class MyObjectiveFunction implements ObjectiveFunction {

	public MyObjectiveFunction(){
		
	}

	@Override
	public double getCost(ArrayList<Route> solution) {
		// TODO return a better cost estimate
		return 0;
	}
}
