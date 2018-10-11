import java.util.*;

public class ParticleSwarmOptimisation {
	
	private AntennaArray antennaArray;
	private int antennaeNum = 3;
	private double steeringAngle = 90;
	private double population = 1 + Math.sqrt(antennaeNum);
	private double aperture = antennaeNum/2;
	private double minSpacing = antennaArray.MIN_SPACING;
	
	public ParticleSwarmOptimisation() {
		this.antennaArray = new AntennaArray(antennaeNum, steeringAngle);
		initialise();
	}

	public static void main(String[] args) {	
		new ParticleSwarmOptimisation();
		
	}
	
	private double[] initialise(){
		double[] design = new double[antennaeNum];
		design[0] = 0.22;
		design[1] = 1.02;
		design[2] = aperture;
		System.out.println(antennaArray.evaluate(design));
		return design;
	}
	
	private void generateAntennaePositions(){
		double[] design = new double[antennaeNum];
		double randomNumber = Math.random();
		for(int i=0; i<antennaeNum; i++){
			design[i] = randomNumber;
		}
	}

}
