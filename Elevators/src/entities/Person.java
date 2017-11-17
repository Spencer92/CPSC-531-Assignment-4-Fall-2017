package entities;

import simulation.Elevators;
import states.PersonStates;

public class Person 
{
	private double arrivalTime;
	private int floorRequested;
	private double workTime;
	private PersonStates state = PersonStates.WAITING_TO_WORK;
	private Elevators elevator;
	
	public Person(Elevators elevator)
	{
		this.elevator = elevator;
		
		
	}
	
	
}
