import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Thavi Tennakoon
 */
public class TSPArtificialImmuneSystem {

	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;
	private ArrayList<ArrayList<Integer>> population;
	private ArrayList<Double> populationCost;
	private ArrayList<Double> normalisedFitness;
	private ArrayList<ArrayList<Integer>> clonePool;
	private double bestCost;

	public TSPArtificialImmuneSystem(int populationNum, int evaluations, double cloneSizeFactor, int d, int routeSize) {
		
		try {
			loadFile(routeSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
		new TSPArtificialImmuneSystem(100, 50, 0.1, 5, 16);
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
	private ArrayList<ArrayList<Integer>> initialisePopulation(int populationSize){
		population = new ArrayList<ArrayList<Integer>>();
		populationCost = new ArrayList<Double>();
		normalisedFitness = new ArrayList<Double>();
		bestCost = 10000;
		for(int i=0; i<populationSize; i++){
			ArrayList<Integer> newRoute = generateRandomRoute();
			double thisCost = getCostOfRoute(newRoute);
			populationCost.add(thisCost);
			population.add(newRoute);
			if(thisCost<bestCost){
				bestCost = thisCost;
			}
		}

		return population;
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
	private ArrayList<ArrayList<Integer>> clone(int populationNum, double cloneSizeFactor){
		clonePool = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<populationNum; i++){
			for(int u=0; u<populationNum*cloneSizeFactor; u++){
				ArrayList<Integer> hyperMutation = hyperMutation(1, population.get(i), i);
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
	 * @param route
	 * @param index
	 * @return The new mutated route.
	 */
	private ArrayList<Integer> hyperMutation(int rho, ArrayList<Integer> route, int index){
		ArrayList<Integer> mutatedRoute = new ArrayList<Integer>(route);
		double mutationRate = Math.exp((-1*rho)*normalisedFitness.get(index));
		int blockLength = (int)(mutatedRoute.size()* mutationRate);
		ArrayList<Integer> blockRoute = new ArrayList<Integer>(); //We will be storing the selected cities in the block in here
		
		Random random = new Random();
		int startIndex = random.nextInt(mutatedRoute.size());

		int u1 = startIndex;
		for(int i=0; i<blockLength; i++){
			if(u1 == mutatedRoute.size()) {
				u1 = 0;
			}
			blockRoute.add(mutatedRoute.get(u1));
			mutatedRoute.set(u1, -1);
			u1++;
		}
		
		Collections.reverse(blockRoute);
		
		int u2 = startIndex;
		int u3 = 0;
		for(int i=0; i<blockLength; i++){
			if(u2==mutatedRoute.size()){
				u2=0;
			}
			mutatedRoute.set(u2, blockRoute.get(u3));
			u2++;
			u3++;
		}
		
		return mutatedRoute;
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
		population.addAll(clonePool);
		while(population.size() > populationSize){
			ArrayList<Integer> thisRoute = null;
			ArrayList<Integer> worstRoute = null;
			double thisCost = 0;
			double worstCost = 0;
			for(int i=0; i<population.size(); i++){
				thisRoute = population.get(i);
				thisCost = getCostOfRoute(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			population.remove(population.indexOf(worstRoute));
		}	
		return population;
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
			for(int i=0; i<population.size(); i++){
				thisRoute = population.get(i);
				thisCost = getCostOfRoute(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			population.remove(population.indexOf(worstRoute));
		}
		for(int i=0; i<d; i++){
			population.add(generateRandomRoute());
		}
		
		for(int i=0; i<populationSize; i++){
			populationCost.add(getCostOfRoute(population.get(i)));
		}
		
		for(double cost : populationCost) {
			if(cost<bestCost){
				bestCost = cost;
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
	private ArrayList<Integer> bestRoute(){
		ArrayList<Integer> thisRoute = null;
		double thisCost = 0;
		ArrayList<Integer> bestRoute = population.get(0);
		double bestCost1 = getCostOfRoute(bestRoute);	
		for(int i=0; i<population.size(); i++){
			thisRoute = population.get(i);
			thisCost = getCostOfRoute(thisRoute);
			if(thisCost<bestCost1){
				bestCost1 = thisCost;
				bestRoute = thisRoute;
			}
		}
		return bestRoute;
	}
	
	/*-------------------------------------------------------------------*/

	private ArrayList<Integer> generateRandomRoute() {
		ArrayList<Integer> cities = new ArrayList<Integer>();
		for(int i=0; i<cityLocations.length; i++){
			cities.add(i);
		}

		Collections.shuffle(cities);
		return cities;
	}

	private Double getCostOfRoute(ArrayList<Integer> route) {
		double totalCost = 0.0;
		int startCity = 0;
		int endCity = 0;
		for(int i=0; i<route.size()-1; i++){
			startCity = route.get(i);
			endCity = route.get(i+1);
			totalCost += cityLocations[startCity][endCity];	
		}
		int firstCity = route.get(0);
		int nextCity = route.get(route.size()-1);
		totalCost += cityLocations[nextCity][firstCity];
		return totalCost;
	}

	private void loadFile(int arraySize) throws FileNotFoundException{
		Scanner ulysses = new Scanner(new File("src\\ulysses16.csv"));
		ulysses.useDelimiter("\n");
		
		cityLocations = new Double[arraySize][arraySize]; 
		xValue = new Double[arraySize];
		yValue = new Double[arraySize];

		int num = 0;
		while(ulysses.hasNext()){
			char[] newLine = ulysses.next().trim().toCharArray();
			if(Character.isDigit(newLine[0])){
				String[] newLine1 = new String(newLine).split(",");
				xValue[num] =  Double.parseDouble(newLine1[1]);
				yValue[num] =  Double.parseDouble(newLine1[2]);
				num++;
			}
			
		}
		
		for(int city1 = 0; city1<cityLocations.length; city1++) {
			for(int city2=0; city2<cityLocations.length; city2++) {
				double minusX = Math.pow((xValue[city2]-xValue[city1]), 2);
				double minusY = Math.pow((yValue[city2]-yValue[city1]), 2);
				cityLocations[city1][city2] = Math.sqrt(minusX + minusY);
			}
		}
		ulysses.close();
	}
	
}
