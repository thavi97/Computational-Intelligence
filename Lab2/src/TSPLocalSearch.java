import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class TSPLocalSearch {

	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;
	private ArrayList<Integer> bestRouteLocalSearch;
	private ArrayList<Integer> bestRouteRandomSearch;

	public TSPLocalSearch(int setTime, int routeSize) {
		try {
			loadFile(routeSize);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Integer> randomRoute = generateRandomRoute();
		System.out.println("A randomly generated route " + randomRoute + " costs " + getCostOfRoute(randomRoute));
		
		ArrayList<Integer> localRoute = bestNeighbour(generateNeighbourhood(generateRandomRoute()));
		System.out.println("A 2-opt swapped route " + localRoute + " costs " + getCostOfRoute(localRoute));
		
		System.out.println("---------------------------------------------");
		
		System.out.println("Calculating the best route using Local Search. This will take " + setTime/1000 + " seconds...");
		double localSearchCost = localSearch(setTime);		
		System.out.println("Calculating the best route using Random Search. This will take " + setTime/1000 + " seconds...");
		double randomSearchCost = randomSearch(setTime);
		
		System.out.println("---------------------------------------------");
		
		System.out.println("Local Search: The best route is " + bestRouteLocalSearch + " and it costs " + localSearchCost);
		System.out.println("Random Search: The best route is " + bestRouteRandomSearch + " and it costs " + randomSearchCost);

	}

	public static void main(String[] args) {	
		new TSPLocalSearch(10000, 16);
	}

	/**
	 * Takes in a tour as a parameter and then swaps two cities around.
	 * 
	 * Eg A neighbour can be [1,3,2,4].
	 * 
	 * One 2-opt swapped version would be [3,1,2,4].
	 * 
	 * @param route
	 * @return The 2-opt swapped route
	 */
	private ArrayList<Integer> generateTwoOptTour(ArrayList<Integer> route){
		Random generator = new Random(); 
		int i = generator.nextInt(route.size());
		int u = generator.nextInt(route.size());
		
		while(i == u){
			u = generator.nextInt(route.size());
		}
		
		Collections.swap(route, i, u);
		return route;
	}
	
	/**
	 * Stores all the possibilities of unique tours into a set to form a neighbourhood.
	 * 
	 * @param route
	 * @return The neighbourhood of tours.
	 */
	private Set<ArrayList<Integer>> generateNeighbourhood(ArrayList<Integer> route){	
		Set<ArrayList<Integer>> neighbourhood = new HashSet<ArrayList<Integer>>();
		neighbourhood.add(route);
		while(neighbourhood.size() < (((route.size()*(route.size() - 1))/2) + 1)){
			neighbourhood.add(generateTwoOptTour(route));
		}
		return neighbourhood;
	}
	
	/**
	 * Finds the cheapest tour within a randomly generated neighbourhood.
	 * 
	 * @param neighbourhood
	 * @return The cheapest tour within the neighbourhood.
	 */
	private ArrayList<Integer> bestNeighbour(Set<ArrayList<Integer>> neighbourhood){
		double currentCost=0.0;
		double leastCost=100000.0;
		ArrayList<Integer> bestRoute = null;
		for(ArrayList<Integer> route : neighbourhood){
			ArrayList<Integer> storeRoute = route;
			currentCost = getCostOfRoute(route);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRoute = storeRoute;
			}
		}
		return bestRoute;
	}

	
	/**
	 * Performs a local search.
	 * 
	 * Initially retrieves the best neighbour from a randomly generated neighbourhood and evaluates its cost.
	 * 
	 * This cost is set as the cheapest cost because there's nothing else to compare it with yet.
	 * 
	 * Now we will generate a new neighbourhood based on this best neighbour.
	 * 
	 * From this new neighbourhood we will retrieve the best neighbour.
	 * 
	 * Then we will compare this new neighbour with the old neighbour; the neighbour with the cheapest cost
	 * is now the best neighbour.
	 * 
	 * @param timeLimit
	 * @return The cheapest tour after the time limit.
	 */
	private double localSearch(int timeLimit){
		long timer = System.currentTimeMillis() + timeLimit;
		double currentCost = 0.0;
		bestRouteLocalSearch = null;
		ArrayList<Integer> bestNeighbour = bestNeighbour(generateNeighbourhood(generateRandomRoute()));
		double cheapestCost = getCostOfRoute(bestNeighbour);
		while(System.currentTimeMillis() < timer){
			currentCost = getCostOfRoute(bestNeighbour);
			if(currentCost<cheapestCost){
				cheapestCost = currentCost;
				bestRouteLocalSearch = bestNeighbour;
			}
			bestNeighbour = bestNeighbour(generateNeighbourhood(bestNeighbour));
		}
		return cheapestCost;
	}

	/**
	 * Generates a random tour.
	 * 
	 * @return A random route.
	 */
	private ArrayList<Integer> generateRandomRoute() {
		ArrayList<Integer> cities = new ArrayList<Integer>();
		for(int i=0; i<cityLocations.length; i++){
			cities.add(i);
		}
		
		Collections.shuffle(cities);
		return cities;
	}
	
	/**
	 * Calculates the overall cost of a given route.
	 * 
	 * @param route
	 * @return The final cost of the route.
	 */
	private double getCostOfRoute(ArrayList<Integer> route) {
		double totalCost = 0.0;
		int startCity = 0;
		int nextCity = 0;
		for(int i=0; i<route.size()-1; i++){
			startCity = route.get(i);
			nextCity = route.get(i+1);
			totalCost += cityLocations[startCity][nextCity];	
		}
		int firstCity = route.get(0);
		int endCity = route.get(route.size()-1);
		totalCost += cityLocations[endCity][firstCity];
		return totalCost;
	}
	
	/**
	 * Performs a random search to find the cheapest route.
	 * 
	 * Simply generate a random route and evaluate its cost.
	 * 
	 * Compare this route with another randomly generated route. The cheapest route is now the best route.
	 * 
	 * @param timeLimit
	 * @return The cheapest route after random search has been completed.
	 */
	private double randomSearch(int timeLimit){
		long timer = System.currentTimeMillis() + timeLimit;
		double currentCost=0.0;
		bestRouteRandomSearch = null;
		ArrayList<Integer> randomRoute = generateRandomRoute();
		double leastCost = getCostOfRoute(randomRoute);
		while(System.currentTimeMillis() < timer){
			currentCost = getCostOfRoute(randomRoute);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRouteRandomSearch = randomRoute;
			}
			randomRoute = generateRandomRoute();
		}	
		return leastCost;
	}
	
	/**
	 * Load in a CSV file in the style of "ulysses16.csv".
	 * 
	 * Delimits the file between every new line.
	 * 
	 * Once the character is a digit we will start extracting the data.
	 * 
	 * Store the new location data into an array.
	 * 
	 * @param arraySize
	 * @throws FileNotFoundException
	 */
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
		
		for(int city1=0; city1<cityLocations.length; city1++) {
			for(int city2=0; city2<cityLocations.length; city2++) {
				double minusX = Math.pow((xValue[city2]-xValue[city1]), 2);
				double minusY = Math.pow((yValue[city2]-yValue[city1]), 2);
				cityLocations[city1][city2] = Math.sqrt(minusX + minusY);
			}
		}
		ulysses.close();
	}
	
	/**
	 * Performs a factorial function.
	 * 
	 * @param n
	 * @return
	 */
	static int factorial(int n){
		int res = 1, i;
		for(i=2; i<=n; i++){
			res *= i;
		}
		return res;
	}
	
}
