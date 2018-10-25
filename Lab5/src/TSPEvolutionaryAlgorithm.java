import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class TSPEvolutionaryAlgorithm {

	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;

	public TSPEvolutionaryAlgorithm(int setTime) {
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static void main(String[] args) {	
		new TSPEvolutionaryAlgorithm(10000);
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
		
		for(int city1 = 0; city1<cityLocations.length; city1++) {
			for(int city2=0; city2<cityLocations.length; city2++) {
				double minusX = Math.pow((xValue[city2]-xValue[city1]), 2);
				double minusY = Math.pow((yValue[city2]-yValue[city1]), 2);
				//System.out.print(Math.sqrt(minusX + minusY) + "|");
				cityLocations[city1][city2] = Math.sqrt(minusX + minusY);
			}
		}
		ulysses.close();
	}
	
	static int factorial(int n){
		int res = 1, i;
		for(i=2; i<=n; i++){
			res *= i;
		}
		return res;
	}
	
}
