package solver;

import instance.ObjectiveFunction;
import instance.Request;
import instance.Visit;

import java.util.ArrayList;

import solution.Route;

public interface Solver {
	
	/**
	 * 
	 * @param solution The solution as it has been constructed so far
	 * @param unplannedVisits A list of all revealed but not yet planned visits. Any element left in the list will be sent during next solving
	 * @param time The current time.
	 * @return The time the solver should be called again if no events occur before that.
	 */
	public double solve(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time, ObjectiveFunction cf);
	
	/**
	 * 
	 * @param solution The solution as it has been constructed so far
	 * @param unplannedVisits A list of all revealed but not yet planned visits. Any element left in the list will be sent during next solving
	 * @param time The current time.
	 * @param cf
	 * @param cancelled The cancelled request
	 */
	public void handleCancellation(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time, ObjectiveFunction cf, Request cancelled);
//	
//	/**
//	 * 
//	 * @param solution The solution as it has been constructed so far
//	 * @param unplannedVisits A list of all revealed but not yet planned visits. Any element left in the list will be sent during next solving
//	 * @param time The current time.
//	 * @param cf
//	 * @param noShow The no show request
//	 */
//	public void handleNoShow(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time, ObjectiveFunction cf, Request noShow);
}
