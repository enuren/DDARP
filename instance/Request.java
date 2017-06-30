package instance;

public class Request {
	private Visit pickup;
	private Visit delivery;
	private int demand;
	
	private int id;
	private static int maxId = 0;
	
	public Request(Visit pu, Visit dl, int demand){
		pickup = pu;
		delivery = dl;
	
		id = maxId;
		maxId++;
		
		pickup.setVisitType(VisitType.Pickup);
		delivery.setVisitType(VisitType.Delivery);
		pickup.setRequestId(id);
		delivery.setRequestId(id);
		pickup.setRequest(this);
		delivery.setRequest(this);
		
		this.demand=demand;
	}
	
	public Visit getPickup() {
		return pickup;
	}



	public Visit getDelivery() {
		return delivery;
	}



	public int getDemand() {
		return demand;
	}



	@Override
	public String toString(){
		return "<Request: "+pickup +" -> "+delivery+" demand "+demand+">";
	}
}
