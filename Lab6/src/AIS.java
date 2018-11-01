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

	public AIS(int populationNum) {
		
		try {
			loadFile(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		initialisePopulation(populationNum);
		System.out.println(normalisedFitness);
		
	}

	public static void main(String[] args) {	
		new AIS(100);
	}
	
	private ArrayList<ArrayList<Integer>> initialisePopulation(int populationSize){
		population = new ArrayList<ArrayList<Integer>>();
		populationCost = new ArrayList<Double>();
		normalisedFitness = new ArrayList<Double>();
		double bestCost = 10000;
		for(int i=0; i<populationSize; i++){
			ArrayList<Integer> newRoute = generateRandomRouteCSV();
			double thisCost = getCostOfRouteCSV(newRoute);
			populationCost.add(thisCost);
			population.add(newRoute);
			if(thisCost<bestCost){
				bestCost = thisCost;
			}
		}
		for(ArrayList<Integer> singlePopulation : population){
			normalisedFitness.add(populationCost.get(singlePopulation)/bestCost);
		}
		return population;
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
