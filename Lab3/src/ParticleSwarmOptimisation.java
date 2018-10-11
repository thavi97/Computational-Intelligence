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
		System.out.println(randomSearchPeakSSL(10000));
		
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
		design[antennaeNum-1] = aperture;
		}
		double peakSLL = antennaArray.evaluate(design);
		return design;
	}
	
	private double randomSearchPeakSSL(long setTime) {
		long timer = System.currentTimeMillis() + setTime;
		double[] antenna = generateAntennaePositions();
		double[] newAntenna = null;
		double peakSLL = antennaArray.evaluate(antenna);
		while(System.currentTimeMillis() < timer){
			newAntenna = generateAntennaePositions();
			double newPeakSLL = antennaArray.evaluate(newAntenna);
			if(Math.abs(newPeakSLL) < Math.abs(peakSLL)) {
				peakSLL = newPeakSLL;
			}
		}
		System.out.println(Arrays.toString(newAntenna));
		return peakSLL;
	}

}
