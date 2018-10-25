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
	private ArrayList<ArrayList<Integer>> population;

	public TSPEvolutionaryAlgorithm() {
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initialisePopulation(20);

		System.out.println(order1(parentSelection(), parentSelection()).toString());

	}

	public static void main(String[] args) {	
		new TSPEvolutionaryAlgorithm();
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
	
	private ArrayList<ArrayList<Integer>> initialisePopulation(int populationSize){
		population = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<populationSize; i++){
			population.add(generateRandomRouteCSV());
		}
		return population;
	}
	
	private ArrayList<Integer> parentSelection(){
		ArrayList<Integer> parent = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>();
		Random random = new Random();
		int sampleSize = (int)(population.size() * 0.2);
		int randomIndex = random.nextInt(population.size());
		for(int i=0; i<sampleSize; i++){
			candidates.add(population.get(randomIndex));
		}
		double bestCost = 1000;
		for(ArrayList<Integer> candidate: candidates){
			double thisCost = getCostOfRouteCSV(candidate);
			if(thisCost < bestCost){
				bestCost = thisCost;
				parent = candidate;
			}
		}
		
		return parent;
	}
	
	private ArrayList<Integer> order1(ArrayList<Integer> parent1, ArrayList<Integer> parent2){
		ArrayList<Integer> offspring = new ArrayList<Integer>();
		for(int i=0; i<parent1.size(); i++){
			offspring.add(-1);
		}
		Random random = new Random();
		int u = 0;
		while(u<offspring.size()/2){
			int randomIndex = random.nextInt(parent1.size());
			if(offspring.get(u) == -1){
				offspring.set(randomIndex, parent1.get(randomIndex));
				parent2.set(randomIndex, -1);
			}
			u++;
		}
		return offspring;
	}
	
	/*-------------------------------------------------------------------*/
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
