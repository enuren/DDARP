package instance;

import java.util.ArrayList;
import solution.Route;

public interface ObjectiveFunction {
	
	/**
	 * 
	 * @param solution to be evaluated
	 * @return The cost of the solution
	 */
	public double getCost(ArrayList<Route> solution);
}
