import java.util.Arrays;
/**
 * @author Thavi Tennakoon
 */
public class ParticleSwarmOptimisation {
	
	/**
	 * @param antennaeNum Number of antennae in our array.
     * @param steeringAngle Desired direction of the main beam in degrees.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Calculate the peakSLL for the swarm and random search.
     * 
     */
	public ParticleSwarmOptimisation(int antennaeNum, double steeringAngle, long setTime) {

		AntennaArray antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		
		System.out.println("Getting the best Swarm vector... This will take " + setTime/1000 + " seconds.");
		double[] swarmBest = swarm(antennaeNum, steeringAngle, antennaArray, setTime);
//		System.out.println("Getting the best Random Search vector... This will take " + setTime/1000 + " seconds.");
//		double[] randomSearchBest = randomSearchPeakSLL(antennaeNum, antennaArray, setTime);
		System.out.println("--------------------");
//		System.out.println("Evaluating both vectors' costs");
		double swarmPeakSLL = antennaArray.evaluate(swarmBest);
//		double randomSearchPeakSLL = antennaArray.evaluate(randomSearchBest);
		
		
		System.out.println("Swarm Peak SLL: " + swarmPeakSLL + " with vector " + Arrays.toString(swarmBest));
		
//		System.out.println("Random Search Peak SLL: " + randomSearchPeakSLL + " with vector " + Arrays.toString(randomSearchBest));

	}

	public static void main(String[] args) {	
		new ParticleSwarmOptimisation(3, 90, 10000);
	}
	
	/**
	 * @param antennaeNum Number of antennae in our array.
     * @param steeringAngle Desired direction of the main beam in degrees.
     * @param antennaArray An antenna array object.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Create a swarm of particles and return the best position out of all
     * the particles' positions within a given time limit.
     * 
     * The number of particles is given by the equation:
     * 20 + sqrt(D) where D is problem dimension.
     * 
     * The particles will be moving to a new position and then will be updating their
     * personal best (pBest) costs. If the pBest cost is the lowest of all the particles, it then
     * becomes the global best position (gBestPos).
     * 
     * @return The global best position.
     * 
     */
	private static double[] swarm(int antennaeNum, double steeringAngle, AntennaArray antennaArray, long setTime) {
		Particle[] particles = new Particle[(int) (20 + Math.sqrt(antennaeNum))];
		for(int i=0; i<particles.length; i++) {
			particles[i] = new Particle(antennaeNum, steeringAngle, antennaArray);
		}
		double[] gBestPos = Particle.generateRandomPosition(antennaeNum);
		double gBestValue = antennaArray.evaluate(gBestPos);
		
		long timer = System.currentTimeMillis() + setTime;
		while(System.currentTimeMillis() < timer){
			for(Particle particle : particles) {
				double[] newPosition = particle.moveNext(gBestPos);
				double newPosValue = antennaArray.evaluate(newPosition);
				if(newPosValue < gBestValue){
					gBestPos = newPosition;
					gBestValue = newPosValue;
				}
			}
		}
		return gBestPos;
	}
	
	/**
	 * @param antennaeNum Number of antennae in our array.
     * @param antennaArray An antenna array object.
     * 
     * Generate a valid antenna array to be used by the particle swarm.
     * 
     * @return A valid antenna array design.
     */
	private static double[] generateAntennaePositions(int antennaeNum, AntennaArray antennaArray){
		double[] design = new double[antennaeNum];
		double aperture = (double)antennaeNum/2;
		while(!antennaArray.is_valid(design)) {
			for(int i=0; i<antennaeNum-1; i++){
				double randomNumber = Math.random();
				if(randomNumber<aperture && randomNumber>0) {
					design[i] = randomNumber;
				}
			}
		design[design.length-1] = aperture;
		}
		return design;
	}
	
	/**
	 * @param antennaeNum Number of antennae in our array.
     * @param antennaArray An antenna array object.
     * @param setTime The time limit given for the method to run. (10000 = 10s)
     * 
     * Calculates the peak side lobe level using random search within a given time limit.
     * 
     * @return An antenna design with a peakSLL.
     * 
     */
	private static double[] randomSearchPeakSLL(int antennaeNum, AntennaArray antennaArray, long setTime) {
		long timer = System.currentTimeMillis() + setTime;
		double[] antenna = generateAntennaePositions(antennaeNum, antennaArray);
		double peakSLL = antennaArray.evaluate(antenna);
		while(System.currentTimeMillis() < timer){
			double[] newAntenna = generateAntennaePositions(antennaeNum, antennaArray);
			double newSLL = antennaArray.evaluate(newAntenna);
			if(newSLL < peakSLL) {
				antenna = newAntenna;
				peakSLL = newSLL;
			}
		}
		return antenna;
	}
}
