import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class RouteSearch {

//	private static int[][] graph;
//	private char[] route;
//	private static int A = 0;
//	private static int B = 1;
//	private static int C = 2;
//	private static int D = 3;
	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;
	private ArrayList<Integer> bestRouteLocalSearch;
	private ArrayList<Integer> bestRouteRandomSearch;

	public RouteSearch(int setTime) {
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Calculating the best route using Local Search. This will take " + setTime/1000 + " seconds...");
		double localSearchCSVCost = localSearchCSV(setTime);		
		System.out.println("Calculating the best route using Random Search. This will take " + setTime/1000 + " seconds...");
		double randomSearchCSVCost = randomSearchCSV(setTime);
		
		System.out.println("---------------------------------------------");
		
		System.out.println("Local Search: The best route is " + bestRouteLocalSearch + " and it costs " + localSearchCSVCost);
		System.out.println("Random Search: The best route is " + bestRouteRandomSearch + " and it costs " + randomSearchCSVCost);
		//System.out.println(timedRouteCSV(2000));
	}

	public static void main(String[] args) {	
		new RouteSearch(10000);
	}
	
	static int factorial(int n){
		int res = 1, i;
		for(i=2; i<=n; i++){
			res *= i;
		}
		return res;
	}


	
	
	/*CSV FILE CODE*/
	
	
	// Takes in a tour as a parameter and then swaps two cities around.
	// Eg A neighbour can be [1,3,2,4].
	// One 2-opt swapped version would be [3,1,2,4].
	private ArrayList<Integer> generateTwoOptTourCSV(ArrayList<Integer> route){
		Random generator = new Random(); 
		int i = generator.nextInt(route.size());
		int u = generator.nextInt(route.size());
		
		while(i == u){
			u = generator.nextInt(route.size());
		}
		
		Collections.swap(route, i, u);
		return route;
	}
	
	// Stores all the possibilities of unique tours into a set to form a neighbourhood.
	private Set<ArrayList<Integer>> generateNeighbourhoodCSV(ArrayList<Integer> route){	
		Set<ArrayList<Integer>> neighbourhood = new HashSet<ArrayList<Integer>>();
		neighbourhood.add(route);
		while(neighbourhood.size() < (((route.size()*(route.size() - 1))/2) + 1)){
			neighbourhood.add(generateTwoOptTourCSV(route));
		}
		return neighbourhood;
	}
	
	// Finds the shortest tour within a randomly generated neighbourhood.
	private ArrayList<Integer> bestNeighbourStepCSV(Set<ArrayList<Integer>> neighbourhood){
		double currentCost=0.0;
		double leastCost=100000.0;
		ArrayList<Integer> bestRoute = null;
		for(ArrayList<Integer> route : neighbourhood){
			ArrayList<Integer> storeRoute = route;
			currentCost = getCostOfRouteCSV(route);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRoute = storeRoute;
			}
		}
		return bestRoute;
	}

	//Performs a local search
	private double localSearchCSV(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		double currentCost=0.0;
		double leastCost=100000.0;
		bestRouteLocalSearch = null;
		ArrayList<Integer> bestNeighbourStepRoute = bestNeighbourStepCSV(generateNeighbourhoodCSV(generateRandomRouteCSV()));
		while(System.currentTimeMillis() < timer){
			currentCost = getCostOfRouteCSV(bestNeighbourStepRoute);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRouteLocalSearch = bestNeighbourStepRoute;
			}
			bestNeighbourStepRoute = bestNeighbourStepCSV(generateNeighbourhoodCSV(bestNeighbourStepRoute));
		}
		return leastCost;
	}

	
	private ArrayList<Integer> generateRandomRouteCSV() {
		ArrayList<Integer> cities = new ArrayList<Integer>();
		for(int i=0; i<cityLocations.length; i++){
			cities.add(i);
		}
		
		Collections.shuffle(cities);
		return cities;
	}
	
	private Double getCostOfRouteCSV(ArrayList<Integer> route) {
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
	
	private double randomSearchCSV(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		double currentCost=0.0;
		double leastCost=100000.0;
		bestRouteRandomSearch = null;
		while(System.currentTimeMillis() < timer){
			ArrayList<Integer> randomRoute = generateRandomRouteCSV();
			currentCost = getCostOfRouteCSV(randomRoute);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRouteRandomSearch = randomRoute;
			}
		}
		
		return leastCost;
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
