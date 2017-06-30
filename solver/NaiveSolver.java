package solver;

import instance.ObjectiveFunction;
import instance.Request;
import instance.Visit;

import java.util.ArrayList;

import solution.Route;

public class NaiveSolver implements Solver{

	@Override
	public double solve(ArrayList<Route> solution,
			ArrayList<Request> unplannedRequests, double time, ObjectiveFunction cf) {
		
		for(Route r : solution){
			assert(r.isConsistent(true));
		}
		
		// Insert each unplanned visit after turn starting with the last in the list
		for(int i=unplannedRequests.size()-1; i>=0; i--){
			Request r = unplannedRequests.remove(i);
			
			boolean inserted = insertBest(r, solution, time, cf);
			
			if(!inserted){
				Route ro = new Route(time);
				solution.add(ro);
				boolean inserted2 = insertBest(r, solution, time, cf);
				if(!inserted2){
					System.err.println("\nFailed to insert request at time "+time);
					System.err.println(solution.get(solution.size()-1));
					System.err.println(r);
					System.err.println(solution.get(solution.size()-1).getVisit(0).getDistance(r.getPickup())/Route.getSpeed());
					System.err.println(r.getPickup().getDistance(r.getDelivery())/Route.getSpeed());
					System.exit(1);
				}
					
			}
			
		}
		
		for(Route r : solution){
			assert(r.isConsistent(true));
		}
		
		// Request next solver to be in an infinite amount of time OR when next event occurs
		return Double.POSITIVE_INFINITY;
	}

	public boolean insertBest(Request r, ArrayList<Route> solution, double time, ObjectiveFunction cf){
		Route bestRoute=null; 
		double bestCost=Double.MAX_VALUE;
		int bp=0,bd=0;
		
		for(Route route : solution){
			assert(route.isConsistent(true));
			
			for(int p=1; p<=route.size(); p++){
				if(route.isLocked(p))
					continue;
				
				
				route.AddVisit(p, r.getPickup(), time);
				for(int d=p+1; d<=route.size(); d++){
					route.AddVisit(d, r.getDelivery(), time);
					
					if(route.isTimeFeasible() && cf.getCost(solution)<bestCost){
						bestCost = cf.getCost(solution);
						bestRoute = route;
						bp=p;
						bd=d;
					}
					route.removeVisit(d, time);
				}
				route.removeVisit(p, time);
				
				assert(route.isConsistent(true));
			}
			
		}
		if(bestRoute==null)
			return false;
		
		bestRoute.AddVisit(bp, r.getPickup(), time);
		bestRoute.AddVisit(bd, r.getDelivery(), time);
		
		assert(bestRoute.isConsistent(true));
		return true;
	}
	
	// If a name for the solver is ever required, return this:
	public String toString(){
		return "NaiveSolver";
	}

	@Override
	public void handleCancellation(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time,
			ObjectiveFunction cf, Request cancelled) {
		// TODO Auto-generated method stub
		
		for(Route r : solution){
			assert(r.isConsistent(true));
			//System.out.println("@ time "+time+"\n"+r);
		}
		
	}

//	@Override
//	public void handleNoShow(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time,
//			ObjectiveFunction cf, Request noShow) {
//		// TODO Auto-generated method stub
//		
//	}
}
