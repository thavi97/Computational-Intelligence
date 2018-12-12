import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Thavi Tennakoon
 */
public class AISPricingProblem {

	private double[] priceList;
	private ArrayList<double[]> population;
	private ArrayList<Double> populationRevenue;
	private ArrayList<Double> normalisedFitness;
	private ArrayList<double[]> clonePool;
	private double bestRevenue;
	PricingProblem f = PricingProblem.courseworkInstance();

	public AISPricingProblem(int populationNum, long setTime, double cloneSizeFactor, int d) {
		
		initialisePopulation(populationNum);
		normaliseFitness();
		
//		try{
//		    /**
//		     * This block will record all the results and save them into a csv file
//		     * to be used for graphing.
//		     */
//		    FileWriter writer = new FileWriter("Graphs\\"+System.currentTimeMillis() + "AISPricingProblem Results (" + setTime/1000 + "s).csv");
//		    
//		    writer.append("Population Number: " + populationNum);
//		    writer.append(',');
//		    writer.append("Clone Size Factor: " + cloneSizeFactor);
//		    writer.append(',');
//		    writer.append("Metadynamics Removal: " + d);
//		    writer.append(',');
//		    writer.append('\n');
//		    
//		    writer.append("Time (ms)");
//		    writer.append(',');
//		    writer.append("Best Revenue");
//		    writer.append(',');
//		    writer.append('\n');
		    
		    /**
			 * Perform an AIS algorithm for a specific number of evaluations.
			 */
		    long timer = System.currentTimeMillis() + setTime;
		    while(System.currentTimeMillis() < timer){
				selection(clone(populationNum, cloneSizeFactor), populationNum);
				metadynamics(d, populationNum);
//	    		writer.append(Double.toString(((timer - System.currentTimeMillis() - setTime) * -1)));
//			    writer.append(',');
//			    writer.append(Double.toString(bestRevenue));
//			    writer.append(',');
//			    writer.append('\n');
				System.out.println("Best Revenue so far " + bestRevenue);
			}

//		    writer.flush();
//		    writer.close();
//		    
//		    }catch (Exception e){//Catch exception if any
//		      System.err.println("Error: " + e.getMessage());
//		    }
		
		
		double[] bestRoute = bestRoute();
		System.out.println("The best list of goods after " + setTime/1000 + " seconds is " + Arrays.toString(bestRoute));
		System.out.println("Its revenue is " + f.evaluate(bestRoute));
	}

	public static void main(String[] args) {	
		new AISPricingProblem(5, 20000, 3, 2);
	}
	
	/**
	 * Fills the population ArrayList with a bunch of random shopping lists and also
	 * stores the lists' costs in a separate ArrayList.
	 * 
	 * We also fill in the bestCost variable so it can be used for normalisation.
	 * 
	 * @param populationSize
	 * @return A population ArrayList
	 */
	private double[] initialisePopulation(int populationSize){
		population = new ArrayList<double[]>();
		populationRevenue = new ArrayList<Double>();
		normalisedFitness = new ArrayList<Double>();
		bestRevenue = 0;
		Random rng = new Random(0);
		
		for(int i=0; i<populationSize; i++){
			priceList = new double[20];

			for(int u=0; u<20; u++) {
				priceList[u] = rng.nextDouble() * 10;
			}
			
			double thisCost = f.evaluate(priceList);
		
				populationRevenue.add(thisCost);
				population.add(priceList);
			
				if(thisCost>bestRevenue){
					bestRevenue = thisCost;
				}
			
		}
		return priceList;
	}

	/**
	 * Gets all the costs of each shopping list and normalises them.
	 * 
	 * You normalise the cost be dividing the cost of the shopping list by the cheapest cost.
	 * 
	 * @return An ArrayList of normalised fitnesses.
	 */
	private ArrayList<Double> normaliseFitness(){
		for(double cost : populationRevenue){
			normalisedFitness.add(cost/bestRevenue);
		}	
		return normalisedFitness;
	}
	
	/**
	 * Create a new ArrayList that stores all the mutated clones in it.
	 * 
	 * @param populationNum
	 * @param cloneSizeFactor
	 * @return The clonePool ArrayList.
	 */
	private ArrayList<double[]> clone(int populationNum, double cloneSizeFactor){
		clonePool = new ArrayList<double[]>();
		for(int i=0; i<populationNum; i++){
			for(int u=0; u<populationNum*cloneSizeFactor; u++){
				double[] hyperMutation = hyperMutation(1, population.get(i), i);
				clonePool.add(hyperMutation);
			}	
		}

		return clonePool;
	}
	
	/**
	 * Performs the Hyper mutation.
	 * 
	 * First it calculates the mutation rate using the given formula.
	 * 
	 * Then it calculates the block length of the selected values in the shopping list.
	 * 
	 * For every shopping list, the block starts at a random index.
	 * 
	 * Finally, select all values in the selected block and reverse the order of them.
	 * 
	 * Now you have a mutated shopping list with reversed block values.
	 * 
	 * @param rho
	 * @param priceList
	 * @param index
	 * @return The new mutated route.
	 */
	private double[] hyperMutation(int rho, double[] priceList, int index){
		double[] mutatedPriceList = priceList.clone();
		double mutationRate = Math.exp((-1*rho)*normalisedFitness.get(index));
		int blockLength = (int)(mutatedPriceList.length* mutationRate);
		Random random = new Random();
		int startIndex = random.nextInt(mutatedPriceList.length);
		int u1 = startIndex;
		for(int i=0; i<blockLength; i++){
			if(u1 == mutatedPriceList.length) {
				u1 = 0;
			}
			mutatedPriceList[u1] = random.nextDouble() * 10;
			u1++;
		}
		return mutatedPriceList;
	}
	
	/**
	 * First, add the clonePool into the current population.
	 * 
	 * Then keep the best mu routes in the population.
	 * 
	 * @param clonePool
	 * @param populationSize
	 * @return The new population ArrayList.
	 */
	private ArrayList<double[]> selection(ArrayList<double[]> clonePool, int populationSize){
		population.addAll(clonePool);
		while(population.size() > populationSize){
			double[] thisPriceList = null;
			double[] worstPriceList = population.get(0);
			double thisRevenue = 0;
			double worstRevenue = f.evaluate(worstPriceList);
			for(int i=0; i<population.size(); i++){
				thisPriceList = population.get(i);
				thisRevenue = f.evaluate(thisPriceList);
				if(thisRevenue<worstRevenue){
					worstRevenue = thisRevenue;
					worstPriceList = thisPriceList;
				}
			}
			population.remove(population.indexOf(worstPriceList));
		}	
		return population;
	}
	
	
	/**
	 * Removes the d worst shopping lists and adds in 2 random ones.
	 * 
	 * Afterwards we refill the populationCost ArrayList and normalisedFitness ArrayList so it can be used for the next iteration.
	 * 
	 * @param d
	 * @param populationSize
	 * @return The population ArrayList.
	 */
	private ArrayList<double[]> metadynamics(int d, int populationSize){
		Random rng = new Random();
		populationRevenue.removeAll(populationRevenue);
		for(int u=0; u<d; u++){
			double[] thisPriceList = null;
			double[] worstPriceList = population.get(0);
			double thisRevenue = 0;
			double worstRevenue = f.evaluate(worstPriceList);
			for(int i=0; i<population.size(); i++){
				thisPriceList = population.get(i);
				thisRevenue = f.evaluate(thisPriceList);
				if(thisRevenue<worstRevenue){
					worstRevenue = thisRevenue;
					worstPriceList = thisPriceList;
				}
			}
			population.remove(population.indexOf(worstPriceList));
		}
		for(int i=0; i<d; i++){
			for (int u=0; u<20; u++) {
				priceList[u] = rng.nextDouble() * 10;
			}
			population.add(priceList);
		}
		
		for(int i=0; i<populationSize; i++){
			populationRevenue.add(f.evaluate(population.get(i)));
		}
		
		for(double revenue : populationRevenue) {
			if(revenue>bestRevenue){
				bestRevenue = revenue;
			}
		}
		
		normaliseFitness();
		
		return population;
	}
	
	/**
	 * We will then calculate the best route after all the iterations.
	 * 
	 * @return The best route (Cheapest route).
	 */
	private double[] bestRoute(){
		double[] thisPriceList = null;
		double thisRevenue = 0;
		double[] bestPriceList = population.get(0);
		double bestRevenue1 = f.evaluate(bestPriceList);	
		for(int i=0; i<population.size(); i++){
			thisPriceList = population.get(i);
			thisRevenue = f.evaluate(thisPriceList);
			if(thisRevenue>bestRevenue1){
				bestRevenue1 = thisRevenue;
				bestPriceList = thisPriceList;
			}
		}
		return bestPriceList;
	}
	
}
