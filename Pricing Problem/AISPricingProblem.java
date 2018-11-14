import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Thavi Tennakoon
 */
public class AISPricingProblem {

	private double[] prices;
	private ArrayList<double[]> population;
	private ArrayList<Double> populationCost;
	private ArrayList<Double> normalisedFitness;
	private ArrayList<double[]> clonePool;
	private double bestCost;

	public AISPricingProblem(int populationNum, int evaluations, double cloneSizeFactor, int d, int routeSize) {
		
		initialisePopulation(populationNum);
		normaliseFitness();
		
		/**
		 * Perform an AIS algorithm for a specific number of evaluations.
		 */
		for(int i=0; i<evaluations; i++){
			selection(clone(populationNum, cloneSizeFactor), populationNum);
			metadynamics(d, populationNum);
		}
		
		ArrayList<Integer> bestRoute = bestRoute();
		System.out.println("The best route is " + bestRoute + " and it costs " + getCostOfRoute(bestRoute));
	}

	public static void main(String[] args) {	
		new AISPricingProblem(5, 5000, 3, 2, 16);
	}
	
	/**
	 * Fills the population ArrayList with a bunch of random routes and also
	 * stores the routes' costs in a separate ArrayList.
	 * 
	 * We also fill in the bestCost variable so it can be used for normalisation.
	 * 
	 * @param populationSize
	 * @return A population ArrayList
	 */
	private double[] initialisePopulation(int populationSize){
		PricingProblem f = PricingProblem.courseworkInstance();
		prices = new double[populationSize];	
		population = new ArrayList<double[]>();
		populationCost = new ArrayList<Double>();
		normalisedFitness = new ArrayList<Double>();
		bestCost = 0;
		Random rng = new Random(0);
		
		for(int i=0; i<populationSize; i++){
			
			for (int u=0; u<populationSize; u++) {
	            prices[u] = rng.nextDouble() * 10;
	        }
			
			double thisCost = f.evaluate(prices);
			populationCost.add(thisCost);
			population.add(prices);
			
			if(thisCost>bestCost){
				bestCost = thisCost;
			}
			
		}

		return prices;
	}

	/**
	 * Gets all the costs of each route and normalises them.
	 * 
	 * You normalise the cost be dividing the cost of the route by the cheapest cost.
	 * 
	 * @return An ArrayList of normalised fitnesses.
	 */
	private ArrayList<Double> normaliseFitness(){
		for(double cost : populationCost){
			normalisedFitness.add(cost/bestCost);
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
	 * Then it calculates the block length of the selected values in the route.
	 * 
	 * For every route, the block starts at a random index.
	 * 
	 * Finally, select all values in the selected block and reverse the order of them.
	 * 
	 * Now you have a mutated route with reversed block values.
	 * 
	 * @param rho
	 * @param price
	 * @param index
	 * @return The new mutated route.
	 */
	private double[] hyperMutation(int rho, double[] price, int index){
		double[] mutatedPrice = price;
		double mutationRate = Math.exp((-1*rho)*normalisedFitness.get(index));
		int blockLength = (int)(mutatedPrice.length* mutationRate);
		
		Random random = new Random();
		Random rng = new Random(0);
		int startIndex = random.nextInt(mutatedPrice.length);

		int u1 = startIndex;
		for(int i=0; i<blockLength; i++){
			if(u1 == mutatedPrice.length) {
				u1 = 0;
			}
			mutatedPrice[u1] = rng.nextDouble();
			u1++;
		}
		
		return mutatedPrice;
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
	private ArrayList<ArrayList<Integer>> selection(ArrayList<ArrayList<Integer>> clonePool, int populationSize){
		prices.addAll(clonePool);
		while(prices.size() > populationSize){
			ArrayList<Integer> thisRoute = null;
			ArrayList<Integer> worstRoute = null;
			double thisCost = 0;
			double worstCost = 0;
			for(int i=0; i<prices.size(); i++){
				thisRoute = prices.get(i);
				thisCost = getCostOfRoute(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			prices.remove(prices.indexOf(worstRoute));
		}	
		return prices;
	}
	
	
	/**
	 * Removes the d worst routes and adds in 2 random ones.
	 * 
	 * Afterwards we refill the populationCost ArrayList and normalisedFitness ArrayList so it can be used for the next iteration.
	 * 
	 * @param d
	 * @param populationSize
	 * @return The population ArrayList.
	 */
	private ArrayList<ArrayList<Integer>> metadynamics(int d, int populationSize){	
		populationCost.removeAll(populationCost);
		for(int u=0; u<d; u++){
			ArrayList<Integer> thisRoute = null;
			ArrayList<Integer> worstRoute = null;
			double thisCost = 0;
			double worstCost = 0;
			for(int i=0; i<prices.size(); i++){
				thisRoute = prices.get(i);
				thisCost = getCostOfRoute(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			prices.remove(prices.indexOf(worstRoute));
		}
		for(int i=0; i<d; i++){
			prices.add(generateRandomRoute());
		}
		
		for(int i=0; i<populationSize; i++){
			populationCost.add(getCostOfRoute(prices.get(i)));
		}
		
		normaliseFitness();
		
		return prices;
	}
	
	/**
	 * We will then calculate the best route after all the iterations.
	 * 
	 * @return The best route (Cheapest route).
	 */
	private ArrayList<Integer> bestRoute(){
		ArrayList<Integer> thisRoute = null;
		double thisCost = 0;
		ArrayList<Integer> bestRoute = prices.get(0);
		double bestCost1 = getCostOfRoute(bestRoute);	
		for(int i=0; i<prices.size(); i++){
			thisRoute = prices.get(i);
			thisCost = getCostOfRoute(thisRoute);
			if(thisCost<bestCost1){
				bestCost1 = thisCost;
				bestRoute = thisRoute;
			}
		}
		return bestRoute;
	}
	
}
