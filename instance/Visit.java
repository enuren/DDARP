package instance;

import java.text.DecimalFormat;

public class Visit {
	private double x;
	private double y;
	private double twStart=0;
	private double twEnd=100000000;
	private static final DecimalFormat myFormatter = new DecimalFormat("###.000");
	private VisitType type;
	private int requestId = -1;
	private Request r;
	
	public Visit(double x, double y){
		this.x = x;
		this.y = y;
		type = VisitType.Depot; 
	}
	
	public Visit(double x, double y, double twStart, double twEnd){
		this.x = x;
		this.y = y;
		this.twStart = twStart;
		this.twEnd = twEnd;
		type = VisitType.Depot;
	}
	
	
	
	/**
	 * Set the id of the corresponding request
	 * @param id
	 */
	public void setRequestId(int id){
		requestId = id;
	}
	
	/**
	 * 
	 * @return The id of the associated request
	 */
	public int getRequestId(){
		return requestId;
	}
	
	/**
	 * Set the type of the visit
	 * @param t
	 */
	public void setVisitType(VisitType t){
		type = t;
	}
	
	/**
	 * 
	 * @return The type of the visit
	 */
	public VisitType getVisitType(){
		return type;
	}
	
	/**
	 * Calculates the Euclidian distance between two visits
	 * @param v
	 * @return
	 */
	public double getDistance(Visit v){
		return Math.sqrt( (v.x-x)*(v.x-x)+(v.y-y)*(v.y-y) );
	}
	
	/**
	 * 
	 * @return The x coordinate of the visit
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * 
	 * @return The y coordinate of the visit
	 */
	public double getY(){
		return y;
	}

	/**
	 * 
	 * @return The start of the time window
	 */
	public double getTwStart(){
		return twStart;
	}
	
	/**
	 * 
	 * @return The end of the time window
	 */
	public double getTwEnd(){
		return twEnd;
	}
	
	/**
	 * Returns a string representation of the visit
	 */
	public String toString(){
		if(twEnd>-1 && twEnd<Double.MAX_VALUE)
			return "<Visit ("+myFormatter.format(x)+","+myFormatter.format(y)+") tw:["+myFormatter.format(twStart)+","+myFormatter.format(twEnd)+"]>";
		return "<Visit ("+myFormatter.format(x)+","+myFormatter.format(y)+")>";
	}
	
	public String getTwLabel(){
		return "["+myFormatter.format(twStart)+", "+myFormatter.format(twEnd)+"]";
	}

	/**
	 * Get the request the visit is a part of
	 * @return
	 */
	public Request getRequest() {
		return r;
	}

	/**
	 * Set the request the visit is a part of
	 * @param r
	 */
	public void setRequest(Request r) {
		this.r = r;
	}
}
