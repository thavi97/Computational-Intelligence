import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class AIS {

	private Double[][] cityLocations;
	private Double[] xValue;
	private Double[] yValue;
	private ArrayList<ArrayList<Integer>> population;
	private ArrayList<Double> populationCost;
	private ArrayList<Double> normalisedFitness;
	private ArrayList<ArrayList<Integer>> clonePool;
	private double bestCost;

	public AIS(int populationNum, int evaluations) {
		
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		initialisePopulation(populationNum);

		normalisedFitness();
		for(int i=0; i<evaluations; i++){
			selection(clone(populationNum, 3), populationNum);
			metadynamics(2, populationNum);
		}
		
		ArrayList<Integer> bestRoute = bestRoute();
		System.out.println("The best route is " + bestRoute + " and it costs " + getCostOfRouteCSV(bestRoute));
	}

	public static void main(String[] args) {	
		new AIS(5, 5000);
	}
	
	private ArrayList<ArrayList<Integer>> initialisePopulation(int populationSize){
		population = new ArrayList<ArrayList<Integer>>();
		populationCost = new ArrayList<Double>();
		normalisedFitness = new ArrayList<Double>();
		bestCost = 10000;
		for(int i=0; i<populationSize; i++){
			ArrayList<Integer> newRoute = generateRandomRouteCSV();
			double thisCost = getCostOfRouteCSV(newRoute);
			populationCost.add(thisCost);
			population.add(newRoute);
			if(thisCost<bestCost){
				bestCost = thisCost;
			}
		}

		return population;
	}
	
	private ArrayList<Double> normalisedFitness(){
		for(double cost : populationCost){
			normalisedFitness.add(cost/bestCost);
		}
		
		return normalisedFitness;
	}
	
	private ArrayList<ArrayList<Integer>> clone(int populationNum, int cloneSizeFactor){
		clonePool = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<populationNum; i++){
			for(int u=0; u<populationNum*cloneSizeFactor; u++){
				ArrayList<Integer> hyperMutation = hyperMutation(1, population.get(i), i);
				clonePool.add(hyperMutation);
			}	
		}		
		return clonePool;
	}
	
	private ArrayList<Integer> hyperMutation(int rho, ArrayList<Integer> clonedRoute, int index){
		ArrayList<Integer> newClone = new ArrayList<Integer>(clonedRoute);
		double mutationRate = Math.exp((-1*rho)*normalisedFitness.get(index));
		int blockLength = (int)(newClone.size()* mutationRate);
		ArrayList<Integer> blockClone = new ArrayList<Integer>();
		
		Random random = new Random();
		int startIndex = random.nextInt(newClone.size());

		int u1 = startIndex;
		for(int i=startIndex; i<startIndex+blockLength; i++){
			if(u1 == newClone.size()) {
				u1 = 0;
			}
			blockClone.add(newClone.get(u1));
			newClone.set(u1, -1);
			u1++;
		}
		
		Collections.reverse(blockClone);
		
		int u2 = startIndex;
		int u3 = 0;
		for(int i=0; i<blockLength; i++){
			if(u2==newClone.size()){
				u2=0;
			}
			newClone.set(u2, blockClone.get(u3));
			u2++;
			u3++;
		}
		
//		System.out.println("start index = " + startIndex);
//		System.out.println("block length = " + blockLength);
//		System.out.println("cloned route = " + clonedRoute);
//		System.out.println("block clone = " + blockClone);
//		System.out.println("new clone = " + newClone);
//		System.out.println("--------------------------------------");
		return newClone;
	}
	
	
	private ArrayList<ArrayList<Integer>> selection(ArrayList<ArrayList<Integer>> clonePool, int populationSize){
		for(int i=0; i<clonePool.size(); i++){
			population.add(clonePool.get(i));
		}
		
		while(population.size() > populationSize){
			ArrayList<Integer> thisRoute = null;
			ArrayList<Integer> worstRoute = null;
			double thisCost = 0;
			double worstCost = 0;
			for(int i=0; i<population.size(); i++){
				thisRoute = population.get(i);
				thisCost = getCostOfRouteCSV(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			population.remove(population.indexOf(worstRoute));
		}
		
		normalisedFitness();
		
		return population;
	}
	
	private ArrayList<ArrayList<Integer>> metadynamics(int d, int populationSize){
		for(int u=0; u<d; u++){
			ArrayList<Integer> thisRoute = null;
			ArrayList<Integer> worstRoute = null;
			double thisCost = 0;
			double worstCost = 0;
			for(int i=0; i<population.size(); i++){
				thisRoute = population.get(i);
				thisCost = getCostOfRouteCSV(thisRoute);
				if(thisCost>worstCost){
					worstCost = thisCost;
					worstRoute = thisRoute;
				}
			}
			population.remove(population.indexOf(worstRoute));
		}
		for(int i=0; i<d; i++){
			population.add(generateRandomRouteCSV());
		}
		normalisedFitness();
		
		return population;
	}
	
	private ArrayList<Integer> bestRoute(){
		ArrayList<Integer> bestRoute = null;
		ArrayList<Integer> thisRoute = null;
		double thisCost = 0;
		double bestCost1 = 10000;
		for(int i=0; i<population.size(); i++){
			thisRoute = population.get(i);
			thisCost = getCostOfRouteCSV(thisRoute);
			if(thisCost<bestCost1){
				bestCost1 = thisCost;
				bestRoute = thisRoute;

			}
		}
		return bestRoute;
	}
	
	/*-------------------------------------------------------------------*/

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
