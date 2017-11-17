package entities;

import simulation.Elevators;
import states.ElevatorStates;

public class Elevator 
{
	private int floorOn;
	private ElevatorStates state = ElevatorStates.IDLE;
	private Elevators elevator;
	
	public Elevator(Elevators elevator)
	{
		this.elevator = elevator;
	}
	
}
