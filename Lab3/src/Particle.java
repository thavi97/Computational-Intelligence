import java.util.Arrays;
import java.util.Random;
/**
 * @author Thavi Tennakoon
 */
public class Particle {
	private AntennaArray antennaArray;
	private double[] position;
	private double[] velocity;
	public double[] pBest;
	public double pBestValue;
	private int antennaeNum;
	private static double theta = 1/(2*Math.log(2)); //Inertia Constant
	private static double phi1 = 1/2 + Math.log(2); //Cognitive Constant
	private static double phi2 = 1/2 + Math.log(2); //Social Constant
	
	/**
     * Construct a single Particle to be used in the swarm.
     * @param antennaeNum Number of antennae in our array.
     * @param steeringAngle Desired direction of the main beam in degrees.
     * @param antennaArray An antenna array object.
     * double[] position is the initial position of the particle and is set to a randomly generated position vector.
     * double[] pBest is the personal best position and is initially set to to current position of the particle.
     * double pBestValue is set to the evaluated cost of the personal best position.
     * double[] velocity is initially set to ((position - random position vector)/2).
     */
	public Particle(int antennaeNum, double steeringAngle, AntennaArray antennaArray){
		this.antennaArray = antennaArray;
		this.antennaeNum = antennaeNum;
		position = generateRandomPosition(antennaeNum);
		pBest = position;
		pBestValue = antennaArray.evaluate(pBest);
		velocity = halfVector(getVectorDifference(generateRandomPosition(antennaeNum), position));
	}
	
	/**
     * Generates a random position vector for the particle.
     * @param antennaeNum Number of antennae in our array.
     * @return The newly generated position.
     */
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
	
	/**
     * Move the particle to the next position.
     * Current position + Next Velocity.
     * @param gBestPos The Global Best Position out of all the particles in the array.
     * @return The newly calculated position.
     */
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
	
	/**
     * Calculate the next velocity of a particle.
     * @param gBestPos The Global Best Position out of all the particles in the array.
     * @return The newly calculated velocity.
     */
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
	
	/**
     * Generate a random valid vector.
     * Used to give values to r1 and r2.
     * @param antennaeNum Number of antennae in our array.
     * @return The newly generated random vector.
     */
	private double[] generateRandomVector(int antennaeNum){
		double[] randomVector = new double[antennaeNum];
		for(int i=0; i<antennaeNum; i++){
			Random r = new Random();
			randomVector[i] = r.nextDouble();
		}

		return randomVector;
	}
	
	/**
     * Calculate the difference between two vectors.
     * @param vector1 One vector.
     * @param vector2 Another vector.
     * @return The difference between the two vectors.
     */
	private double[] getVectorDifference(double[] vector1, double[] vector2){
		double[] differenceVector = new double[vector1.length];
		for(int i = 0; i < vector1.length; i++){
				differenceVector[i] = vector1[i] - vector2[i];
		}
		return differenceVector;
	}
	
	/**
     * Divide a vector by 2.
     * @param vector A vector.
     * @return The vector divided by 2.
     */
	private double[] halfVector(double[] vector){
		double[] halfVector = new double[vector.length];
		for(int i = 0;i< halfVector.length;i++){
			halfVector[i] = vector[i] / 2;
		}
		return halfVector;
	}
	

}