import java.util.Arrays;
import java.util.Random;

public class Particle {
	private AntennaArray antennaArray;
	private double[] position;
	private double[] velocity;
	public double[] pBest;
	public double pBestValue;
	private int antennaeNum;
	private static double theta = 1/(2*Math.log(2));
	private static double phi1 = 1/2 + Math.log(2);
	private static double phi2 = 1/2 + Math.log(2);
	
	public Particle(int antennaeNum, double steeringAngle, AntennaArray antennaArray){
		this.antennaArray = antennaArray;
		this.antennaeNum = antennaeNum;
		position = generateRandomPosition(antennaeNum);
		pBest = position;
		pBestValue = antennaArray.evaluate(pBest);
		velocity = halfVector(getVectorDifference(generateRandomPosition(antennaeNum), position));
	}
	
	public static void main(String[] args){
		
	}
	
	
	public static double[] generateRandomPosition(int antennaeNum){
		AntennaArray antennaArray = new AntennaArray(antennaeNum, 90);
		double[] newPosition = new double[antennaeNum];
		double aperture = (double)antennaeNum/2;
		while(!antennaArray.is_valid(newPosition)){
			for(int i = 0; i < antennaeNum; i++){
				double randomPosition = Math.random();
				if(randomPosition<aperture && randomPosition>0) {
					newPosition[i] = randomPosition*aperture;
				}	
			newPosition[antennaeNum -1] = aperture;
			}
		}
		Arrays.sort(newPosition);
		return newPosition;
	}
	
	//Move the particle to the next position.
	//Current position + next velocity
	public double[] moveNext(double[] gBestPos){
		double[] newPosition = new double[position.length];
		for(int i = 0; i<antennaeNum; i++){
			newPosition[i] = position[i] + velocity[i];
		}
		Arrays.sort(newPosition);
		if(antennaArray.is_valid(newPosition)){			
			position = newPosition;
			double positionValue = antennaArray.evaluate(position);
			if(Math.abs(positionValue) < Math.abs(pBestValue)){
				pBest = position;
				pBestValue = positionValue;
			}
		}
		velocity = getNextVelocity(gBestPos);
		
		return position;

	}
	
	private double[] getNextVelocity(double[] gBestPos){
		double[] nextVelocity = new double[antennaeNum];
		double[] inertia = new double[antennaeNum];
		double[] cognitive = new double[antennaeNum];
		double[] social = new double[antennaeNum];
		double[] r1 = generateRandomVector(antennaeNum);
		double[] r2 = generateRandomVector(antennaeNum);
		double[] pBestDiff = getVectorDifference(pBest, position);
		double[] gBestDiff = getVectorDifference(gBestPos, position);
		for(int i=0; i<antennaeNum-1; i++){
			inertia[i] = theta*velocity[i]; 
			cognitive[i] = phi1*r1[i]*pBestDiff[i]; 
			social[i] = phi2*r2[i]*gBestDiff[i]; 
			nextVelocity[i] = inertia[i] + cognitive[i] + social[i];
		}	

		return nextVelocity;
		
	}
	
	//For r1 and r2
	private double[] generateRandomVector(int antennaeNum){
		double[] randomVector = new double[antennaeNum];
		for(int i=0; i<antennaeNum; i++){
			Random r = new Random();
			randomVector[i] = r.nextDouble();
		}

		return randomVector;
	}
	
	private double[] getVectorDifference(double[] vector1, double[] vector2){
		// checking for vectors of different sizes
		if(vector1.length != vector2.length){
			System.out.println("####ERROR##########");
			double[] invalidDifferenceVector = new double[vector1.length];
			for(int i = 0; i < vector1.length; i++){
				invalidDifferenceVector[i] = Double.MAX_VALUE;
			}
			return invalidDifferenceVector;
		}
		double[] differenceVector = new double[vector1.length];
		for(int i = 0; i < vector1.length; i++){
				differenceVector[i] = vector1[i] - vector2[i];
		}
		return differenceVector;
	}
	
	private double[] halfVector(double[] vector){
		double[] newVector = new double[vector.length];
		for(int i = 0;i< newVector.length;i++){
			newVector[i] = vector[i] / 2;
		}
		return newVector;
	}
	

}