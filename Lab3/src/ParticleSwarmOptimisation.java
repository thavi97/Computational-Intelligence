import java.util.Arrays;

public class ParticleSwarmOptimisation {
	
	private AntennaArray antennaArray;
	private int antennaeNum = 3;
	private double steeringAngle = 90;
	private double aperture = (double)antennaeNum/2;
	private double minSpacing = antennaArray.MIN_SPACING;
	
	public ParticleSwarmOptimisation() {
		this.antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		generateAntennaePositions();
//		randomSearchPeakSSL(10000);
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
		System.out.println(Arrays.toString(design));
		System.out.println(peakSLL);
		return design;
	}
	
	private double[] randomSearchPeakSSL(long setTime) {
		long timer = System.currentTimeMillis() + setTime;
		double peakSLL = antennaArray.evaluate(generateAntennaePositions());
		while(System.currentTimeMillis() < timer){
			double newPeakSSL = antennaArray.evaluate(generateAntennaePositions());
			if(newPeakSSL < peakSSL) {
				
			}
			antennaArray.evaluate(generateAntennaePositions());
		}
		return null;
	}

}
