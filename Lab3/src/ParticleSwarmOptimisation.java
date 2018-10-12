import java.util.Arrays;

public class ParticleSwarmOptimisation {
	
	private AntennaArray antennaArray;
	private int antennaeNum = 3;
	private double steeringAngle = 90;
	private double aperture = (double)antennaeNum/2;
	private double minSpacing = antennaArray.MIN_SPACING;
	
	public ParticleSwarmOptimisation() {
		this.antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		//System.out.println(generateAntennaePositions());
		System.out.println(randomSearchPeakSLL(10000));
		
	}

	public static void main(String[] args) {	
		new ParticleSwarmOptimisation();
	}
	
	private double[] generateAntennaePositions(){
		double[] design = new double[antennaeNum];
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
	
	private double randomSearchPeakSLL(long setTime) {
		long timer = System.currentTimeMillis() + setTime;
		double[] antenna = generateAntennaePositions();
		double[] newAntenna = null;
		double peakSLL = Math.abs(antennaArray.evaluate(antenna));
		while(System.currentTimeMillis() < timer){
			newAntenna = generateAntennaePositions();
			double newPeakSLL = Math.abs(antennaArray.evaluate(newAntenna));
			if(newPeakSLL < peakSLL) {
				peakSLL = newPeakSLL;
			}
		}
		System.out.println(Arrays.toString(newAntenna));
		return peakSLL;
	}

}
