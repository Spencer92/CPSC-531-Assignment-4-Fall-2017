package entities;

import java.util.LinkedList;

import simulation.Elevators;
import states.ElevatorStates;

public class Elevator 
{
	
	private int floorOn;
	private double currentFloorPosition;
	private ElevatorStates state;
	private ElevatorStates prevElevatorState;
	private int name;
	private Elevators elevator;
	LinkedList<Integer> floorRequests = new LinkedList<Integer>();
	private int nextFloorRequest;
	private int nextFloor;
	
	
	public int getNextFloor() {
		return nextFloor;
	}

	public void setNextFloor(int nextFloor) {
		this.nextFloor = nextFloor;
	}

	public ElevatorStates getPrevElevatorState() {
		return prevElevatorState;
	}

	public int getNextFloorRequest() {
		return nextFloorRequest;
	}

	public Elevator(Elevators elevator)
	{
		this.elevator = elevator;
		this.currentFloorPosition = 0;
		this.name = elevator.getElevatorName();
		this.state = ElevatorStates.IDLE;
		this.prevElevatorState = ElevatorStates.IDLE;
	}

	public void addFloorRequest(int floorRequest)
	{
		floorRequests.add(floorRequest);
	}
	
	
	public void removeFloorRequest(int index)
	{
		floorRequests.remove(index);
	}
	
	public int getFloorRequest(int index)
	{
		return floorRequests.get(index);
	}
	
	public int floorRequestSize()
	{
		return floorRequests.size();
	}

	public double getCurrentFloorPosition() {
		return currentFloorPosition;
	}


	public void setCurrentFloorPosition(double currentFloorPosition) {
		this.currentFloorPosition = round(currentFloorPosition);
	}


	public ElevatorStates getState() {
		return state;
	}


	public void setState(ElevatorStates state) {
		if(this.state != state)
		{
			this.prevElevatorState = this.state;
			this.state = state;
		}
	}


	public int getName() {
		return name;
	}
	
	private double round(double number)
	{
		number *= 1000;
		number = Math.round(number);
		number = Math.floor(number);
		number /= 1000;
		return number;
	}
	
}
