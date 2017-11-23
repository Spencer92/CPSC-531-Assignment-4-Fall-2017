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
	private int name;
	private int currentFloor = 0;
	
	public Person(Elevators elevator)
	{
		this.elevator = elevator;
		arrivalTime = elevator.getTimeStamp() + elevator.getArrival();
		this.arrivalTime = elevator.getFloor();
		this.workTime = elevator.getWork();
		this.name = elevator.getPersonName();
	}
	
	public int getTime()
	{
		return this.name;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getFloorRequested() {
		return floorRequested;
	}

	public void setFloorRequested(int floorRequested) {
		this.floorRequested = floorRequested;
	}

	public double getWorkTime() {
		return workTime;
	}

	public void setWorkTime(double workTime) {
		this.workTime = workTime;
	}

	public PersonStates getState() {
		return state;
	}

	public void setState(PersonStates state) {
		this.state = state;
	}
	
	
}
