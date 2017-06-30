
package instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import solution.Route;
import distributions.Distribution;
import distributions.Uniform;
import eventSimulator.Event;

public class Instance {
	// The random generator used in all distributions
	public static Random rand = new Random();
	
	// The most extreme coordinates appearing in the solution
	private double minX,maxX,minY,maxY;
	// The events that are gradually revealed.
	private ArrayList<Event> events= new ArrayList<Event>();
	
//	private HashMap<Visit, Boolean> noShows = new HashMap<Visit, Boolean>();
	
	public Instance(){
		this.minX=Double.POSITIVE_INFINITY;
		this.maxX=Double.NEGATIVE_INFINITY;
		this.minY=Double.POSITIVE_INFINITY;
		this.maxY=Double.NEGATIVE_INFINITY;
	}
	
	/**
	 * Generate an event and its associated visit and add it to the simulation
	 * @param xDist A distribution describing the likelihood of the visit having that x coordinate
	 * @param yDist A distribution describing the likelihood of the visit having that y coordinate
	 * @param awareDist A distribution describing the likelihood of the visit being revealed to the simulator at that time
	 * @param reactionTime The time after the simulator becomes aware of a visit till its time window starts
	 * @param twLength The length of the time window
	 */
	/**
	 * 
	 * @param xDistPickup A distribution describing the likelihood of the pickup having that x coordinate
	 * @param yDistPickup A distribution describing the likelihood of the pickup having that y coordinate
	 * @param xDistDelivery A distribution describing the likelihood of the delivery having that x coordinate
	 * @param yDistDelivery A distribution describing the likelihood of the delivery having that y coordinate
	 * @param awareDist A distribution describing the likelihood of the visit being revealed to the simulator at that time
	 * @param reactionTime The time after the simulator becomes aware of a visit till its time window starts
	 * @param twLengthP The length of the pickups time window
	 * @param timeBetweenTimewindows The time between the start of the pickups and delivery's time window starts
	 * @param twLengthD The length of the delivery's time window
	 * @param demandDist
	 */
	public void generateVisit(Distribution xDistPickup, Distribution yDistPickup,
			Distribution xDistDelivery, Distribution yDistDelivery,
			Distribution awareDist, 
			Distribution reactionTime,
			Distribution twLengthP,
			Distribution twLengthD,
			Distribution demandDist){
		double xP=xDistPickup.sample();
		double yP=yDistPickup.sample();
		double xD=xDistDelivery.sample();
		double yD=yDistDelivery.sample();
		double aware = -1;
		if(awareDist!=null)
			aware = awareDist.sample();
		
		// Pickup time window
		double twStartP = 0;
		if(twLengthP!=null)
			twStartP = Math.max(aware,0)+reactionTime.sample();
		double twEndP = Double.MAX_VALUE;
		if(twLengthP!=null)
			twEndP = twStartP+twLengthP.sample();
		
		// Delivery time window
		double twStartD = 0;
		if(twLengthD!=null)
			twStartD = Math.max(aware,0)+reactionTime.sample();
		double twEndD = Double.MAX_VALUE;
		if(twLengthD!=null)
			twEndD = twStartD + twLengthD.sample();
		
		Visit p = new Visit(xP, yP, twStartP, twEndP);
		Visit d = new Visit(xD,yD, twStartD, twEndD);
		
		double x = Math.max(xP, xD);
		if(x>maxX){
			maxX = x;
		}
		x = Math.min(xP, xD);
		if(x<minX){
			minX = x;
		}
		double y = Math.max(yP, yD);
		if(y>maxY){
			maxY=y;
		}
		y = Math.min(yP, yD);
		if(y<minY){
			minY=y;
		}

		Visit depot = new Visit(0.0, 0.0);
		double ra = Math.max(0, aware);
		// If infeasible, retry (5 mins slack)
		if(p.getTwEnd()<Double.MAX_VALUE && 
				ra > p.getTwEnd()-depot.getDistance(p)/Route.getSpeed() - 5.0/60.0){
			System.err.println("Regenerating request due to infeasibility (pickup)");
			generateVisit(xDistPickup, yDistPickup, xDistDelivery, yDistDelivery, awareDist, reactionTime, twLengthP, twLengthD, demandDist);
			return;
		}
		// If infeasible, retry (5 mins slack)
		if(d.getTwEnd()<Double.MAX_VALUE && 
				ra > d.getTwEnd()-(depot.getDistance(p)+p.getDistance(d))/Route.getSpeed()-10.0/60.0 ){
			System.err.println("Regenerating request due to infeasibility (delivery)");
			generateVisit(xDistPickup, yDistPickup, xDistDelivery, yDistDelivery, awareDist, reactionTime, twLengthP, twLengthD, demandDist);
			return;
		}
		
		int demand = ((Double) demandDist.sample()).intValue();
		Request r = new Request(p, d, demand);
		Event e = new Event(aware,r);
		events.add(e);
		//System.out.println("Added "+e+" to the instance.");
	}
	
	/**
	 * Generate cancellation events. It should be called after noShow generation
	 * @param chance A number 0<chance<1 describing the chance that an request is cancelled
	 */
	public void generateCancellations(double chance){
		ArrayList<Event> cancellations = new ArrayList<Event>();
		Uniform ud = new Uniform(0, 1);
		for(Event e : events){
			if(ud.sample()<chance /*&& !noShows.containsKey(e.getRequest().getPickup())*/){
				Request r = e.getRequest();
				double lb = Math.max(0, e.getAwareTime());
				
				// Credit to Jesper, Alexander and Henrik
				double maxdrivetime = (r.getPickup().getDistance(r.getDelivery())/Route.getSpeed())*2+10.0/60.0;
				double ub = Math.max(Math.max(r.getPickup().getTwStart(),r.getDelivery().getTwStart()-maxdrivetime),lb);
				Uniform awareDist = new Uniform(lb, ub);
				
				Event cancel = new Event(awareDist.sample(), e.getRequest());
				cancel.setIsCancellation();
				cancellations.add(cancel);
			}
		}
		events.addAll(cancellations);
		
		System.out.println("Generated "+cancellations.size()+" cancellations.");
	}
	
//	/**
//	 * Generate a set of no shows
//	 * @param chance A number 0<chance<1 describing the chance that an request is a no show
//	 */
//	public void generateNoShows(double chance){
//		Uniform ud = new Uniform(0, 1);
//		for(Event e : events){
//			if(ud.sample()<chance){
//				noShows.put(e.getRequest().getPickup(), true);
//			}
//		}
//		System.out.println("Generated "+noShows.size()+" no shows.");
//	}
	
//	/**
//	 * 
//	 * @param v
//	 * @return true iff the visit is a now show
//	 */
//	public boolean isNoShow(Visit v){
//		if(!noShows.containsKey(v))
//			return false;
//		return noShows.get(v);
//	}
	
	/**
	 * Get the events to be revealed to the solver
	 * @return An ArrayList of events
	 */
	public ArrayList<Event> getEvents(){
		return new ArrayList<Event>(events);
	}
	
	/**
	 * Provides a textual description of errors if they can be detected in the given solution
	 * @param solution The solution to be checked.
	 * @return The errors found, if no errors are detected, the empty string will be returned.
	 */
	public String isSolutionValid(ArrayList<Route> solution){ 
		//TODO finish the method
		return "";
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	/**
	 * Returns a string representation of the instance
	 */
	@Override
	public String toString(){
		String str = "+============================================================\n"
				+ "| Instance x: ["+minX+","+maxX+"] y: ["+minY+","+maxY+"]:\n";
		Collections.sort(events);
		for(int e=events.size()-1;e>=0; e--){
			str += "|\t"+events.get(e)+"\n";
		}
		str += "+============================================================";
		return str;
	}
	
}
