import java.io.FileWriter;
import java.util.Arrays;

/**
 * @author Thavi Tennakoon
 */
public class PSOPricingProblem {
	
	static PricingProblem pricingProblem = PricingProblem.courseworkInstance();
	
	/**
	 * @param numberOfGoods Number of antennae in our array.
     * @param steeringAngle Desired direction of the main beam in degrees.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Calculate the peakSLL for the swarm and random search.
     * 
     */
	public PSOPricingProblem(int numberOfGoods, long setTime) {
		
		System.out.println("Getting the best Swarm vector... This will take " + setTime/1000 + " seconds.");
		double[] swarmBest = swarm(numberOfGoods, pricingProblem, setTime);
		System.out.println("--------------------");
		double swarmPeakSLL = pricingProblem.evaluate(swarmBest);
		System.out.println("Swarm Peak SLL: " + swarmPeakSLL + " with vector " + Arrays.toString(swarmBest));

	}

	public static void main(String[] args) {	
		new PSOPricingProblem(20, 5000);
	}
	
	/**
	 * @param numberOfGoods Number of antennae in our array.
     * @param antennaArray An antenna array object.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Create a swarm of particles and return the best position out of all
     * the particles' positions within a given time limit.
     * 
     * The number of particles is given by the equation:
     * 20 + sqrt(D) where D is problem dimension.
     * 
     * The particles will be moving to a new position and then will be updating their
     * personal best (pBest) costs. If the pBest cost is the lowest of all the particles, it then
     * becomes the global best position (gBestPos).
     * 
     * @return The global best position.
     * 
     */
	private static double[] swarm(int numberOfGoods, PricingProblem pricingProblem, long setTime) {
		Good[] goods = new Good[(int) (400 + Math.sqrt(numberOfGoods))];
		for(int i=0; i<goods.length; i++) {
			goods[i] = new Good(numberOfGoods, pricingProblem);
		}
		double[] gBestPos = goods[0].pBest;
		double gBestValue = pricingProblem.evaluate(gBestPos);
		
		long timer = System.currentTimeMillis() + setTime;
		try{
		    // Create file 
		    FileWriter writer = new FileWriter(System.currentTimeMillis() + "PSOPricingProblem Results ( " + setTime/1000 + " seconds).csv");

		    writer.append("Best Revenue");
		    writer.append(',');
		    writer.append("Seconds");
		    writer.append(',');
		    writer.append('\n');	    
		    
		    while(System.currentTimeMillis() < timer){
		    	for(Good good : goods) {
		    		double[] newPosition = good.moveNext(gBestPos);
		    		double newPosValue = pricingProblem.evaluate(newPosition);
		    		if(newPosValue > gBestValue){
		    			gBestPos = newPosition;
		    			gBestValue = newPosValue;
		    		}
		    		writer.append(Double.toString(gBestValue));
				    writer.append(',');
				    writer.append(Double.toString(((timer - System.currentTimeMillis() - setTime) * -1)/1000));
				    writer.append(',');
				    writer.append('\n');
		    		System.out.println("Best Revenue " + gBestValue);
		    	}
		    }
		    
		    writer.flush();
		    writer.close();
		    
		}catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		 }
		
		return gBestPos;
	}
	
}
