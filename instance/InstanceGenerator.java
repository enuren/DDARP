package instance;

import distributions.Distribution;
import distributions.Uniform;

public class InstanceGenerator {
	private Distribution xCoordDistPickup;
	private Distribution yCoordDistPickup;
	private Distribution xCoordDistDelivery;
	private Distribution yCoordDistDelivery;
	private Distribution reactTimeP;
	private Distribution reactTimeD;
	private Distribution twLengthP;
	private Distribution twLengthD;
	private Distribution demandDist;
	private double fractionPickUpTW;
	
	public InstanceGenerator(Distribution xCoordP, Distribution yCoordP,
			Distribution xCoordD, Distribution yCoordD,
			Distribution twLengthP,
			Distribution twLengthD,
			Distribution demandDist,
			double fractionPickUpTW){
		xCoordDistPickup = xCoordP;
		yCoordDistPickup = yCoordP;
		xCoordDistDelivery = xCoordD;
		yCoordDistDelivery = yCoordD;
		this.twLengthP = twLengthP;
		this.twLengthD = twLengthD;
		this.demandDist = demandDist;
		this.fractionPickUpTW = fractionPickUpTW;
	}
	
	/**
	 * Return an instance With the given characteristics
	 * @param nPreknown The number of visits known before execution starts
	 * @param nRevealed The number of visits revealed during execution
	 * @param timeDist The distribution of the time visits becomes known in the instance
	 * @return The generated instance
	 */
	public Instance generate(int n, int nRev, Distribution awareDist,
			Distribution reactTimeP, Distribution reactTimeD, Distribution preReactTimeP, Distribution preReactTimeD){
		Instance inst = new Instance();
		
		Uniform isPickup = new Uniform(0, 1);
		
		Distribution twP;
		Distribution twD;
		Distribution reactTime;
		
		for(int i=0; i<n; i++){
			if(isPickup.sample()<fractionPickUpTW){
				twP = twLengthP;
				twD=null;
				reactTime = preReactTimeP;
			}else{
				twP=null;
				twD = twLengthD;
				reactTime = preReactTimeD;
			}
			
			inst.generateVisit(xCoordDistPickup, yCoordDistPickup, xCoordDistDelivery, yCoordDistDelivery, null,
					reactTime, twP, twD, demandDist);
		}
		
		for(int i=0; i<nRev; i++){
			if(isPickup.sample()<fractionPickUpTW){
				twP = twLengthP;
				twD=null;
				reactTime = reactTimeP;
			}else{
				twP=null;
				twD = twLengthD;
				reactTime = reactTimeD;
			}
			
			inst.generateVisit(xCoordDistPickup, yCoordDistPickup, xCoordDistDelivery, yCoordDistDelivery, awareDist,
					reactTime, twP, twD, demandDist);
		}
		
		return inst; 
	}
}
