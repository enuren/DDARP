package eventSimulator;

import instance.Request;
import instance.Visit;

public class Event implements Comparable<Event>{
	private double awareTime;
	private Request request;
	
	//TODO propagate update
	private boolean isCancellation = false;
	private boolean isNoShow = false;
	
	public Event(double awareTime, Request r){
		this.awareTime = awareTime;
		this.request = r;
	}

	/**
	 * Mark this event as a cancellation
	 */
	public void setIsCancellation(){
		isCancellation = true;
	}
	
	/**
	 * 
	 * @return true is the event is a cancellation
	 */
	public boolean isCancellation(){
		return isCancellation;
	}
	
//	/**
//	 * Mark this event as a cancellation
//	 */
//	public void setIsNoShow(){
//		isNoShow = true;
//	}
//	
//	/**
//	 * 
//	 * @return true is the event is a cancellation
//	 */
//	public boolean isNoShow(){
//		return isNoShow;
//	}
	
	/**
	 * 
	 * @return The time the simulator becomes aware of the request
	 */
	public double getAwareTime(){
		return awareTime;
	}
	
	/**
	 * 
	 * @return The request associated with the event-
	 */
	public Request getRequest(){
		return request;
	}
	
	/**
	 * Makes events sortable by their awareness time. The first to be revealed will be sorted last
	 */
	@Override
	public int compareTo(Event e) {
		return -Double.compare(awareTime,e.awareTime);
	}
	
	@Override
	public String toString(){
		if(isCancellation())
			return "<Cancelled Event: "+request+" @ "+awareTime+">";
		return "<Event: "+request+" @ "+awareTime+">";
	}
	
}
