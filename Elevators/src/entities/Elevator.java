package entities;

import java.util.LinkedList;

import simulation.Elevators;
import states.ElevatorStates;

public class Elevator 
{
	private int floorOn;
	private ElevatorStates state = ElevatorStates.IDLE;
	private Elevators elevator;
	private int name;
	private double floorProgress;
	private LinkedList<Integer> ElevatorRequests = new LinkedList<Integer>();
	


	public Elevator(Elevators elevator)
	{
		this.elevator = elevator;
		this.floorOn = elevator.getFloor()/2;
		this.name = elevator.getElevatorName();
	}

	public int getFloorOn() {
		return floorOn;
	}

	public void setFloorOn(int floorOn) {
		this.floorOn = floorOn;
	}

	public ElevatorStates getState() {
		return state;
	}

	public void setState(ElevatorStates state) {
		this.state = state;
	}
	
	public double getFloorProgress()
	{
		return floorProgress;
	}
	
	public void setFloorProgress(double floorProgress)
	{
		this.floorProgress = floorProgress;
	}
	
	public void addElevatorRequest(int request)
	{
		ElevatorRequests.add(request);
	}
	
}
