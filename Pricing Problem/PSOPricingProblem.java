import java.io.FileWriter;
import java.util.Arrays;

/**
 * @author Thavi Tennakoon
 */
public class PSOPricingProblem {
	
	static PricingProblem pricingProblem = PricingProblem.courseworkInstance();
	
	/**
	 * @param numberOfGoods Number of antennae in our array.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Calculate the peakSLL for the swarm and random search.
     * 
     */
	public PSOPricingProblem(int numberOfGoods, long setTime) {
		
		System.out.println("Getting the best Swarm vector... This will take " + setTime/1000 + "s.");
		double[] swarmBest = swarm(numberOfGoods, pricingProblem, setTime);
		System.out.println("--------------------");
		double swarmPeakSLL = pricingProblem.evaluate(swarmBest);
		System.out.println("Best Revenue: " + swarmPeakSLL + " with vector " + Arrays.toString(swarmBest));

	}

	public static void main(String[] args) {	
		new PSOPricingProblem(20, 10000);
	}
	
	/**
	 * @param numberOfGoods Number of goods in our priceList.
     * @param pricingProblem A PricingProblem instance.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Create a swarm of priceLists and return the best position out of all
     * the priceLists' positions within a given time limit.
     * 
     * The number of priceLists is given by the equation:
     * 400 + sqrt(D) where D is problem dimension.
     * 
     * The priceLists will be moving to a new position and then will be updating their
     * personal best (pBest) costs. If the pBest cost is the lowest of all the priceLists, it then
     * becomes the global best position (gBestPos).
     * 
     * @return The global best position.
     * 
     */
	private static double[] swarm(int numberOfGoods, PricingProblem pricingProblem, long setTime) {
		PSOPriceList[] priceLists = new PSOPriceList[(int) (400 + Math.sqrt(numberOfGoods))];
		for(int i=0; i<priceLists.length; i++) {
			priceLists[i] = new PSOPriceList(numberOfGoods, pricingProblem);
		}
		double[] gBestPos = priceLists[0].pBest;
		double gBestValue = pricingProblem.evaluate(gBestPos);
		
		long timer = System.currentTimeMillis() + setTime;
//		try{
//			/**
//		     * This block will record all the results and save them into a csv file
//		     * to be used for graphing.
//		     */
//		    FileWriter writer = new FileWriter("Graphs\\"+System.currentTimeMillis() + "PSOPricingProblem Results ( " + setTime/1000 + " seconds).csv");
//
//		    writer.append("Time (ms)");
//		    writer.append(',');
//		    writer.append("Best Revenue");
//		    writer.append(',');
//		    writer.append('\n');	    
		    
		    while(System.currentTimeMillis() < timer){
		    	for(PSOPriceList priceList : priceLists) {
		    		double[] newPosition = priceList.moveNext(gBestPos);
		    		double newPosValue = pricingProblem.evaluate(newPosition);
		    		if(newPosValue > gBestValue){
		    			gBestPos = newPosition;
		    			gBestValue = newPosValue;
		    			System.out.println("Best Revenue " + gBestValue);
		    		}
		    		
//		    		writer.append(Double.toString(((timer - System.currentTimeMillis() - setTime) * -1)));
//				    writer.append(',');
//				    writer.append(Double.toString(gBestValue));			    
//				    writer.append(',');
//				    writer.append('\n');
		    	}
		    }
		    
//		    writer.flush();
//		    writer.close();
//		    
//		}catch (Exception e){//Catch exception if any
//		      System.err.println("Error: " + e.getMessage());
//		 }
		
		return gBestPos;
	}
	
}
