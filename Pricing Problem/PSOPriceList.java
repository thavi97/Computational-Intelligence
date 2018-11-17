import java.util.Arrays;
import java.util.Random;
/**
 * @author Thavi Tennakoon
 */
public class PSOPriceList {

	double[] position;
	private double[] velocity;
	public double[] pBest;
	public double pBestValue;
	private int numberOfGoods;
	private static double theta = 1/(2*Math.log(2)); //Inertia Constant
	private static double phi1 = 1/2 + Math.log(2); //Cognitive Constant
	private static double phi2 = 1/2 + Math.log(2); //Social Constant

	
	/**
     * Construct a single PriceList to be used in the swarm.
     * @param numberOfGoods Number of goods in our priceLists.
     * @param pricingProblem An PricingProblem instance.
     * double[] position contains the initial values of the priceList and is set to a randomly generated prices between 1 and 10.
     * double[] pBest is the personal best position and is initially set to to current position of the priceList.
     * double pBestValue is set to the evaluated cost of the personal best position.
     * double[] velocity is initially set to ((position - random position vector)/2).
     */
	public PSOPriceList(int numberOfGoods, PricingProblem pricingProblem){
		PSOPricingProblem.pricingProblem = pricingProblem;
		this.numberOfGoods = numberOfGoods;
		position = generateRandomPosition(numberOfGoods);
		pBest = position;
		pBestValue = pricingProblem.evaluate(pBest);
		velocity = halfVector(getVectorDifference(generateRandomPosition(numberOfGoods), position));
	}
	
	/**
     * Generates a random position vector for the priceLists.
     * @param numberOfGoods Number of antennae in our array.
     * @return The newly generated position.
     */
	public static double[] generateRandomPosition(int numberOfGoods){
		double[] newPosition = new double[numberOfGoods];
		double maxValue = 10.0;
		while(!PSOPricingProblem.pricingProblem.is_valid(newPosition)){
			for(int i = 0; i < numberOfGoods; i++){
				double randomPosition = Math.random();
				if(randomPosition<maxValue && randomPosition>0) {
					newPosition[i] = randomPosition*maxValue;
				}	
			}
			Arrays.sort(newPosition);
		}
		return newPosition;
	}
	
	/**
     * Move the priceLists to the next position.
     * Current position + Next Velocity.
     * @param gBestPos The Global Best Position out of all the priceLists in the swarm.
     * @return The newly calculated position.
     */
	public double[] moveNext(double[] gBestPos){
		double[] newPosition = new double[position.length];
		for(int i = 0; i<numberOfGoods; i++){
			newPosition[i] = position[i] + velocity[i];
		}
		if(PSOPricingProblem.pricingProblem.is_valid(newPosition)){			
			position = newPosition;
			double positionValue = PSOPricingProblem.pricingProblem.evaluate(position);
			if(positionValue > pBestValue){
				pBest = position;
				pBestValue = positionValue;
			}
		}
		velocity = getNextVelocity(gBestPos);
		return position;

	}
	
	/**
     * Calculate the next velocity of a priceLists.
     * @param gBestPos The Global Best Position out of all the priceLists in the swarm.
     * @return The newly calculated velocity.
     */
	private double[] getNextVelocity(double[] gBestPos){
		double[] nextVelocity = new double[numberOfGoods];
		double[] inertia = new double[numberOfGoods];
		double[] cognitive = new double[numberOfGoods];
		double[] social = new double[numberOfGoods];
		double[] r1 = generateRandomVector(numberOfGoods);
		double[] r2 = generateRandomVector(numberOfGoods);
		double[] pBestDiff = getVectorDifference(pBest, position);
		double[] gBestDiff = getVectorDifference(gBestPos, position);
		for(int i=0; i<numberOfGoods-1; i++){
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
     * @param numberOfGoods Number of goods in our priceList.
     * @return The newly generated random vector.
     */
	private double[] generateRandomVector(int numberOfGoods){
		double[] randomVector = new double[numberOfGoods];
		for(int i=0; i<numberOfGoods; i++){
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
		for(int i=0; i<vector1.length; i++){
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
		for(int i=0; i<halfVector.length; i++){
			halfVector[i] = vector[i] / 2;
		}
		return halfVector;
	}
	

}