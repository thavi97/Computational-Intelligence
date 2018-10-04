import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Main {

	private static int[][] graph;
	private char[] route;
	private static int A = 0;
	private static int B = 1;
	private static int C = 2;
	private static int D = 3;

	public Main() {
		
		createGraph();
//		try {
//			loadFile();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//int getCostOfRandomRoute = getCostOfRoute((createRoute(5,generateRandomRoute())));
	
		// System.out.println(Arrays.deepToString(createGraph()));
	
//		 System.out.println(getCostOfRoute(createRoute(4, "ADCB")));
		//System.out.println(generateRandomRoute());
		// System.out.println(getCostOfRandomRoute);
		System.out.println(timedRoute(10));
		//System.out.println(System.currentTimeMillis());
	}

	public static void main(String[] args) {
		
		new Main();
		

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
		//route = route + 'A';
		//System.out.println(route);
		return route;
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
	
	private void loadFile() throws FileNotFoundException{
		Scanner ulysses = new Scanner(new File("src\\ulysses16.csv"));
		ulysses.useDelimiter("\n");
		
		Double[][] cityLocations = new Double[16][16]; 
		Double[] xValue = new Double[16];
		Double[] yValue = new Double[16];
		
		int i = 0;
		int num = 0;
		while(ulysses.hasNext()){
			char[] newLine = ulysses.next().trim().toCharArray();
			if(Character.isDigit(newLine[0])){
				String[] newline1 = new String(newLine).split(",");
				xValue[num] =  Double.parseDouble(newline1[1]);
				yValue[num] =  Double.parseDouble(newline1[2]);
				//System.out.print(xValue[i] + " " + yValue[i] + "| ");
				num++;
			}
			
			if(i < 18) {
				i++;	
			}
			
		}
		
	
		//TO DO
		
		for(int city1 = 0; city1<15; city1++) {
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
	
}