import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class TSPEvolutionaryAlgorithm {

	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;
	private ArrayList<ArrayList<Integer>> population;

	public TSPEvolutionaryAlgorithm(int generations, double probability) {
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		initialisePopulation(100);
		ArrayList<Integer> bestOffspring = loopThroughGenerations(generations, probability);
		System.out.println("After " + generations + " generations, the Cheapest Route is " + bestOffspring + " and it's cost is " + getCostOfRouteCSV(bestOffspring));
		//newGeneration(order1(parentSelection(),parentSelection()), 0.7);
		
	}

	public static void main(String[] args) {	
		new TSPEvolutionaryAlgorithm(60000, 0.7);
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
		ArrayList<Integer> dummyParent2 = new ArrayList<Integer>(parent2);
		ArrayList<Integer> offspring = new ArrayList<Integer>();
		for(int i=0; i<parent1.size(); i++){
			offspring.add(-1);
		}
		Random random = new Random();
		
		ArrayList<Integer> parent1Indexes = new ArrayList<Integer>();
		ArrayList<Integer> parent2Indexes = new ArrayList<Integer>();
		
		int largestIndex = 0;
		
		while(parent1Indexes.size() < parent1.size()/2){
			int randomIndex = random.nextInt(parent1.size());
			if(randomIndex > largestIndex) {
				largestIndex = randomIndex;
			}
			if(!parent1Indexes.contains(randomIndex)){
				parent1Indexes.add(randomIndex);
			}
		}

		for(Integer index : parent1Indexes){
			offspring.set(index, parent1.get(index));
			int parent2Index = dummyParent2.indexOf(parent1.get(index));
			dummyParent2.set(parent2Index, -1);
		}
		
		int nextIndex = offspring.indexOf(-1);
		if(!(largestIndex+1 > offspring.size()-1)) {
			nextIndex = largestIndex+1;
		}
		int initialNextIndex = nextIndex;
		
		for(int i=0; i<dummyParent2.size(); i++) {
			if(dummyParent2.get(i) != -1) {
				parent2Indexes.add(i);
			}
		}
		
		for(Integer parent2Index : parent2Indexes) { 
			offspring.set(nextIndex, dummyParent2.get(parent2Index));
			while(offspring.get(nextIndex) != -1) {
				nextIndex++;
				if(nextIndex == 16) {
					nextIndex = 0;
				}
				if(nextIndex == initialNextIndex) {
					break;
				}		
			}
		}	
		return offspring;
	}
	
	private ArrayList<ArrayList<Integer>> newGeneration(ArrayList<Integer> offspring, double probability){
		ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<ArrayList<Integer>>(population);

		double random = Math.random();
		if(random < probability){
			generateTwoOptTourCSV(offspring);
		}
		newPopulation.add(offspring);
		
		ArrayList<Integer> mostExpensiveRoute = null;
		double mostExpensiveRouteValue = 0;
		for(ArrayList<Integer> route : newPopulation) {
			double routeValue = getCostOfRouteCSV(route);
			if(routeValue>mostExpensiveRouteValue){
				mostExpensiveRoute = route;
				mostExpensiveRouteValue = routeValue;
			}
		}
		int weak = newPopulation.indexOf(mostExpensiveRoute);
		newPopulation.remove(weak);
		
		population = newPopulation;
		return population;
	}
	
	private ArrayList<Integer> loopThroughGenerations(int numberOfGenerations, double probability){
		int i=0;
		ArrayList<ArrayList<Integer>> finalPopulation = new ArrayList<ArrayList<Integer>>();
		
		while(i < numberOfGenerations){
			finalPopulation = newGeneration(order1(parentSelection(),parentSelection()), probability);
			i++;
		}
		ArrayList<Integer> cheapestRoute = new ArrayList<Integer>();
		double cheapestRouteValue = 10000;
		for(ArrayList<Integer> route : finalPopulation) {
			double routeValue = getCostOfRouteCSV(route);
			if(routeValue<cheapestRouteValue){
				cheapestRoute = route;
			}
		}
		getCostOfRouteCSV(cheapestRoute);
		return cheapestRoute;
	}
	
	
	/*-------------------------------------------------------------------*/

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
	
}
