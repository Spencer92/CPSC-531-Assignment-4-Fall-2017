package simulation;

import java.util.Random;

public class Elevators {

	public static void main(String[] args) {

		new Elevators(args);
	}
	
	
	public static final int SEED_PERSON_ARRIVAL = 10009;
	public static final int SEED_PERSON_WORK = 20089;
	public static final int SEED_PERSON_FLOOR = 30071;
	public static final int SEED_INDECISIVE = 40093;
	private Random personArrival;
	private Random personWork;
	private Random personFloor;
	private Random indecisive;
	private int floors = 7;
	private double lambdaArrival = 0.5;
	private double meanWorkRate = 60;
	private double floorChangeRate = 1/6;
	
	
	public Elevators(String [] args)
	{
		personArrival = new Random(SEED_PERSON_ARRIVAL);
		personWork = new Random(SEED_PERSON_WORK);
		personFloor = new Random(SEED_PERSON_FLOOR);
		indecisive = new Random(SEED_INDECISIVE);
	}
	

	public double getArrival()
	{
		return distribution(lambdaArrival,personArrival.nextDouble());
	}
	
	public double getWork()
	{
		return distribution(1/meanWorkRate,personWork.nextDouble());
	}
	
	public int getFloor()
	{
		return this.personFloor.nextInt(floors);
	}
	
	public boolean indecisive()
	{
		return indecisive.nextBoolean();
	}

	private double distribution(double lambda, double randomNumber)
	{
		return ((1.0/lambda) * Math.log(1.0-randomNumber))*-1;
	}
}
