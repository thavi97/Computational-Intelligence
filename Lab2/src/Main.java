import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {

	private static int[][] graph;
	private char[] route;
	private static int A = 0;
	private static int B = 1;
	private static int C = 2;
	private static int D = 3;
	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;

	public Main() {
		
		createGraph();
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//int getCostOfRandomRoute = getCostOfRoute((createRoute(5,generateRandomRoute())));
	
		// System.out.println(Arrays.deepToString(createGraph()));
	
//		 System.out.println(getCostOfRoute(createRoute(4, "ADCB")));
		//System.out.println(generateRandomRoute());
		// System.out.println(getCostOfRandomRoute);
		//System.out.println(timedRoute(1000));
		//System.out.println(System.currentTimeMillis());
		//System.out.println(System.currentTimeMillis());
		//System.out.println(generateTwoOptNeighbour(generateRandomRoute()));
		//System.out.println(generateNeighbourhood(generateRandomRoute()));
		//System.out.println(neighbourhoodStep(generateNeighbourhood(generateRandomRoute())));
		//System.out.println(Arrays.deepToString(cityLocations));
		//System.out.println(generateRandomRouteCSV());
		//System.out.println(getCostOfRouteCSV(generateRandomRouteCSV()));
		//System.out.println(timedRouteCSV(20000));
		//System.out.println(generateTwoOptNeighbourCSV(generateRandomRouteCSV()));
		//System.out.println(generateNeighbourhoodCSV(generateRandomRouteCSV()));
		System.out.println(randomLocalSearchCSV(30000));
	}

	public static void main(String[] args) {
		
		new Main();
		

	}
	
	static int factorial(int n){
		int res = 1, i;
		for(i=2; i<=n; i++){
			res *= i;
		}
		return res;
	}

	private int[][] createGraph() {
		graph = new int[4][4];
		// A = 0
		graph[A][A] = 0; // AA
		graph[A][B] = 20; // AB
		graph[A][C] = 41; // AC
		graph[A][D] = 35; // AD

		// B = 1
		graph[B][A] = 20; // BA
		graph[B][B] = 0; // BB
		graph[B][C] = 30; // BC
		graph[B][D] = 34; // BD

		// C = 2
		graph[C][A] = 42; // CA
		graph[C][B] = 30; // CB
		graph[C][C] = 0; // CC
		graph[C][D] = 12; // CD

		// D = 3
		graph[D][A] = 35; // DA
		graph[D][B] = 34; // DB
		graph[D][C] = 12; // DC
		graph[D][D] = 0; // DD

		return graph;
	}

	/**
	 * Create a route with x number of cities
	 * 
	 * @param numCities
	 * @return array with length x
	 */
	private char[] createRoute(int numCities, String cities) {
		route = new char[numCities];
		for (int i = 0; i < cities.length(); i++) {
			route[i] = cities.charAt(i);
		}		
		return route;
	}

	/**
	 * Calculate cost of route
	 * 
	 * @param route
	 * @return
	 */
	private int getCostOfRoute(char[] route) {
		int totalCost = 0;
		for (int i = 0; i < route.length - 1; i++) {
			char start = route[i];
			char next = route[i + 1];
			int startIndex = findGraphValue(start);
			int nextIndex = findGraphValue(next);
			int val = graph[startIndex][nextIndex];
			
			totalCost = totalCost + val;
		}
		
		int firstCity = findGraphValue(route[0]);
		int finalCity = findGraphValue(route[route.length - 1]);
		totalCost = totalCost + graph[finalCity][firstCity];
		return totalCost;
	}

	/**
	 * Translate char into numerical value to find on graph
	 * 
	 * @param letter
	 *            - A/B/C/D
	 * @return int - 0/1/2/3, returns 9 if letter not found
	 */
	private int findGraphValue(char letter) {
		switch (letter) {
		case 'A':
			return A;
		case 'B':
			return B;
		case 'C':
			return C;
		case 'D':
			return D;
		default:
			return 9;
		}
	}

	private String generateRandomRoute() {
		String route = "A";
		ArrayList<Character> cities = new ArrayList<Character>();
		cities.add('B');
		cities.add('C');
		cities.add('D');

		int length = cities.size();
		for (int i = 0; i < length; i++) {
			int randomCity = new Random().nextInt(cities.size());
			route = route + cities.get(randomCity);
			cities.remove(randomCity);
		}
		return route;
	}
	
	private String generateTwoOptNeighbour(String route){
		Random generator = new Random(); 
		char[] charRoute = route.toCharArray();
		int i = generator.nextInt(route.length());
		int u = generator.nextInt(route.length());
		
		while(i == u){
			u = generator.nextInt(route.length());
		}
		
		char temp = charRoute[i];
		charRoute[i] = charRoute[u];
		charRoute[u] = temp;
		return new String(charRoute);
	}
	
	private ArrayList<Integer> generateTwoOptNeighbourCSV(ArrayList<Integer> route){
		Random generator = new Random(); 
		int i = generator.nextInt(route.size());
		int u = generator.nextInt(route.size());
		
		while(i == u){
			u = generator.nextInt(route.size());
		}
		
		Collections.swap(route, i, u);
		return route;
	}
	
	private Set<String> generateNeighbourhood(String route){	
		Set<String> neighbourhood = new HashSet<String>();
		neighbourhood.add(route);
		while(neighbourhood.size() < (((route.length()*(route.length() - 1))/2) + 1)){
			neighbourhood.add(generateTwoOptNeighbour(route));
		}
		return neighbourhood;
	}
	
	private Set<ArrayList> generateNeighbourhoodCSV(ArrayList<Integer> route){	
		Set<ArrayList> neighbourhood = new HashSet<ArrayList>();
		neighbourhood.add(route);
		while(neighbourhood.size() < (((route.size()*(route.size() - 1))/2) + 1)){
			neighbourhood.add(generateTwoOptNeighbourCSV(generateRandomRouteCSV()));
		}
		return neighbourhood;
	}
	
	//Step 4
	private String neighbourhoodStep(Set<String> neighbourhood){
		int x=0;
		int leastCost=100000;
		String bestRoute = "";
		for(String route : neighbourhood){
			String randomRoute = route;
			x = getCostOfRoute((createRoute(4,randomRoute)));
			if(x<leastCost){
				leastCost = x;
				bestRoute = randomRoute;
			}
		}
		return bestRoute;
	}
	
	private ArrayList<Integer> neighbourhoodStepCSV(Set<ArrayList> neighbourhood){
		double x=0.0;
		double leastCost=100000.0;
		ArrayList<Integer> bestRoute = null;
		for(ArrayList<Integer> route : neighbourhood){
			ArrayList<Integer> randomRoute = route;
			x = getCostOfRouteCSV(generateRandomRouteCSV());
			if(x<leastCost){
				leastCost = x;
				bestRoute = randomRoute;
			}
		}
		return bestRoute;
	}
	
	//Step 5
	private String randomLocalSearch(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		int currentCost=0;
		int leastCost=100000;
		String bestRoute = "";
		while(System.currentTimeMillis() < timer){
			String randomRoute = neighbourhoodStep(generateNeighbourhood(generateRandomRoute()));
			currentCost = getCostOfRoute((createRoute(4,randomRoute)));
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRoute = randomRoute;
			}
		}
		return "The best route is " + bestRoute + " and it costs " + leastCost;
	}
	
	private String randomLocalSearchCSV(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		double currentCost=0.0;
		double leastCost=100000.0;
		ArrayList<Integer> bestRoute = null;
		while(System.currentTimeMillis() < timer){
			ArrayList<Integer> randomRoute = neighbourhoodStepCSV(generateNeighbourhoodCSV(generateRandomRouteCSV()));
			currentCost = getCostOfRouteCSV(randomRoute);
			if(currentCost<leastCost){
				leastCost = currentCost;
				bestRoute = randomRoute;
			}
		}
		return "The best route is " + bestRoute + " and it costs " + leastCost;
	}
	
	private String timedRoute(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		int x=0;
		int leastCost=100000;
		String bestRoute = "";
		while(System.currentTimeMillis() < timer){
			String randomRoute = generateRandomRoute();
			x = getCostOfRoute((createRoute(4,randomRoute)));
			if(x<leastCost){
				leastCost = x;
				bestRoute = randomRoute;
			}
		}
		
		return "Cheapest route to take is " + bestRoute + " and it costs " + leastCost;
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
				//System.out.print(xValue[i] + " " + yValue[i] + "| ");
				num++;
			}
			
		}
		
	
		//TO DO
		
		for(int city1 = 0; city1<16; city1++) {
			for(int city2=0; city2<16; city2++) {
				double minusX = Math.pow((xValue[city2]-xValue[city1]), 2);
				double minusY = Math.pow((yValue[city2]-yValue[city1]), 2);
				//System.out.print(Math.sqrt(minusX + minusY) + "|");
				cityLocations[city1][city2] = Math.sqrt(minusX + minusY);
			}
		}
		System.out.println(Arrays.deepToString(cityLocations));
		ulysses.close();
	}
	
//	private void randomTour(int tourLength){
//		int[] tour = new int[tourLength];
//		while
//	}
	
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
	
	private String timedRouteCSV(int setTime){
		long timer = System.currentTimeMillis() + setTime;
		double x=0.0;
		double leastCost=100000.0;
		ArrayList<Integer> bestRoute = null;
		while(System.currentTimeMillis() < timer){
			ArrayList<Integer> randomRoute = generateRandomRouteCSV();
			x = getCostOfRouteCSV(randomRoute);
			if(x<leastCost){
				leastCost = x;
				bestRoute = randomRoute;
			}
		}
		
		return "Cheapest route to take is " + bestRoute + " and it costs " + leastCost;
	}
	
}