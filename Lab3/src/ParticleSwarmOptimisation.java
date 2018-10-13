public class ParticleSwarmOptimisation {
	
	public ParticleSwarmOptimisation(int antennaeNum, double steeringAngle, long setTime) {

		AntennaArray antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		
		double[] psoBest = swarm(antennaeNum, steeringAngle, antennaArray, setTime);
		double[] randomSearchBest = randomSearchPeakSLL(antennaeNum, antennaArray, setTime);
		
		double psoBestCost = antennaArray.evaluate(psoBest);
		double randomSearchBestCost = antennaArray.evaluate(randomSearchBest);
		
		System.out.println();
		System.out.println("-----");
		System.out.println();
		
		System.out.println("The Particle Swarm Algorithm found an SLL value  " + Math.abs(psoBestCost));

		System.out.println("The Random Search found an SLL value  " + Math.abs(randomSearchBestCost));

	}

	public static void main(String[] args) {	
		new ParticleSwarmOptimisation(3, 90, 10000);
	}
	
	private static double[] swarm(int antennaeNum, double steeringAngle, AntennaArray antennaArray, long setTime) {
		Particle[] particles = new Particle[(int) (20 + Math.sqrt(antennaeNum))]; //Swarm size: 20 + sqrt(D) where D is problem dimension
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
				if(Math.abs(newPosValue) < Math.abs(gBestValue)){
					gBestPos = newPosition;
					gBestValue = newPosValue;
					System.out.println("Global Best Cost: " + gBestValue);
				}
			}
		}
		return gBestPos;
	}
	
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
	
	private static double[] randomSearchPeakSLL(int antennaeNum, AntennaArray antennaArray, long setTime) {
		long timer = System.currentTimeMillis() + setTime;
		double[] antenna = generateAntennaePositions(antennaeNum, antennaArray);
		double peakSLL = antennaArray.evaluate(antenna);
		while(System.currentTimeMillis() < timer){
			double[] newAntenna = generateAntennaePositions(antennaeNum, antennaArray);
			double newSLL = antennaArray.evaluate(newAntenna);
			if(Math.abs(newSLL) < Math.abs(peakSLL)) {
				antenna = newAntenna;
				peakSLL = newSLL;
			}
		}

		return antenna;
	}
	
	

}
