import java.util.Arrays;
import java.util.Random;

public class Particle {
	private double[] position;
	private double[] velocity;
	private double[] pBest;
	private double[] gBest;
	private double[] nextVelocity;
	private AntennaArray antennaArray;
	private int antennaeNum;
	private double aperture;
	private double steeringAngle;
	private static double theta = 1/(2*Math.log(2));
	private static double phi1 = 1/2 + Math.log(2);
	private static double phi2 = 1/2 + Math.log(2);
	
	public Particle(int antennaeNum, double steeringAngle){
		this.antennaeNum = antennaeNum;
		this.steeringAngle = steeringAngle;
		aperture = (double)antennaeNum/2;
		antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		position = generateRandomPosition();
		System.out.println("Initial Position = " + Arrays.toString(position));
		velocity = generateInitialVelocity();
		System.out.println("Initial Velocity = " + Arrays.toString(velocity));
		pBest = position;
		gBest = generateRandomPosition();
		nextVelocity = getNextVelocity();
		System.out.println("Next Velocity = " + Arrays.toString(nextVelocity));
		System.out.println("Next Position = " + Arrays.toString(moveNext()));
		
	}
	
	public static void main(String[] args){
		new Particle(3, 90);
	}
	
	private double[] generateInitialVelocity(){
		double[] initialVelocity = new double[antennaeNum];
		double[] randomPosition2 = generateRandomPosition();
		for(int i=0; i<antennaeNum; i++){
			initialVelocity[i] = (position[i] - randomPosition2[i])/2;
		}
		return initialVelocity;
	}
	
	private double[] generateRandomPosition(){
		double[] newPosition = new double[antennaeNum];
		while(!antennaArray.is_valid(newPosition)){
			for(int i = 0; i < newPosition.length-1; i++){
				double randomPosition = Math.random();
				if(randomPosition<aperture && randomPosition>0) {
					newPosition[i] = Math.abs(randomPosition);
				}	
			}
			newPosition[newPosition.length -1] = aperture;
		}
		Arrays.sort(newPosition);
		return newPosition;
	}
	
	//Move the particle to the next position.
	//Current position + next velocity
	private double[] moveNext(){
		double[] nextPosition = new double[antennaeNum];
		for(int i = 0; i<antennaeNum-1; i++){
			nextPosition[i] = position[i] + nextVelocity[i];
		}
		return nextPosition;
	}
	
	private double[] getNextVelocity(){
		double[] nextVelocity = new double[antennaeNum];
		double[] inertia = new double[antennaeNum];
		double[] cognitive = new double[antennaeNum];
		double[] social = new double[antennaeNum];
		double[] r1 = generateRandomVector();
		double[] r2 = generateRandomVector();
		for(int i=0; i<antennaeNum-1; i++){
			inertia[i] = theta*velocity[i]; 
			cognitive[i] = phi1*r1[i]*(pBest[i]-position[i]); 
			social[i] = phi2*r2[i]*(gBest[i]-position[i]); 
			nextVelocity[i] = inertia[i] + cognitive[i] + social[i];
		}	

		return nextVelocity;
		
	}
	
	//For r1 and r2
	private double[] generateRandomVector(){
		double[] randomVector = new double[antennaeNum];
		for(int i=0; i<antennaeNum; i++){
			Random r = new Random();
			randomVector[i] = r.nextDouble();
		}

		return randomVector;
	}
	

}