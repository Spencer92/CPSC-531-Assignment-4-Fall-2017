package entities;

import simulation.Elevators;
import states.PersonStates;

public class Person 
{
	
	private Elevators elevator;
	private int floor;
	private int absFloor;
	private double arrival;
	private double absArrival;
	private double leaveDelay;
	private double workDelay;
	private double workTime;
	private double nextRelevantTime;
	private PersonStates state = PersonStates.ARRIVING;
	private int name;
	private double addedWaitTime;
	private int currentFloor;
	private double elevatorWait;
	
	public Person(Elevators elevator, double prevArrivalTime)
	{
		this.elevator = elevator;
		this.floor = elevator.getFloor();
		this.workTime = elevator.getWork();
		this.arrival = elevator.getArrival() + prevArrivalTime;
		this.nextRelevantTime = arrival;
		this.name = elevator.getPersonName();
		this.currentFloor = 0;
		this.absArrival = arrival;
		this.workDelay = 0.0;
		this.leaveDelay = 0.0;
		this.elevatorWait = 0.0;
		this.name = elevator.getPersonName();
	}
	
	public int getName()
	{
		return name;
	}
	
	
	public double getElevatorWait()
	{
		return elevatorWait;
	}
	
	public void setElevatorWait(double elevatorWait)
	{
		this.elevatorWait = elevatorWait;
	}
	
	public PersonStates getState()
	{
		return this.state;
	}
	
	public void setState(PersonStates state)
	{
		this.state = state;
	}
	
	public double getAbsArrival()
	{
		return this.absArrival;
	}
	
	public int getCurrentFloor()
	{
		return this.currentFloor;
	}
	
	public void setCurrentFloor(int currentFloor)
	{
		this.currentFloor = currentFloor;
	}
	
	public double getAddedWaitTime()
	{
		return this.addedWaitTime;
	}
	
	public void setAddedWaitTime(double addedWaitTime)
	{
		this.addedWaitTime = addedWaitTime;
	}
	
	public double getArrival()
	{
		return this.arrival;
	}
	
	public double getWorkTime()
	{
		return this.workTime;
	}
	
	public int getFloor()
	{
		return this.floor;
	}
	
	public void setFloor(int floor)
	{
		this.floor = floor;
	}
	
	public int getAbsFloor()
	{
		return this.absFloor;
	}
	
	public double getNextRelevantTime()
	{
		return nextRelevantTime;
	}
	
	public void setNextRelevantTime(double nextRelevantTime)
	{
		this.nextRelevantTime = nextRelevantTime;
	}
	
	public double getLeaveDelay()
	{
		return this.leaveDelay;
	}
	
	public void setLeaveDelay(double delay)
	{
		this.leaveDelay = delay;
	}
	
	public double getWorkDelay()
	{
		return this.workDelay;
	}
	
	public void setWorkDelay(double workDelay)
	{
		this.workDelay = workDelay;
	}
	
	public void setArrival(double arrival)
	{
		this.arrival = arrival;
	}
	
	/*
	private double arrivalTime;
	private int floorRequested;
	private double workTime;
	private PersonStates state = PersonStates.WAITING_TO_WORK;
	private Elevators elevator;
	private int name;
	private int currentFloor = 0;
	private double delay = 0;
	
	public Person(Elevators elevator)
	{
		this.elevator = elevator;
		arrivalTime = elevator.getTimeStamp() + elevator.getArrival();
		this.arrivalTime = elevator.getFloor();
		this.workTime = elevator.getWork();
		this.name = elevator.getPersonName();
	}
	
	public int getCurrentFloor()
	{
		return currentFloor;
	}
	
	public void setCurrentFloor(int currentFloor)
	{
		this.currentFloor = currentFloor;
	}
	
	public double getDelay()
	{
		return delay;
	}
	
	public void setDelay(double delay)
	{
		this.delay = delay;
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
	*/
	
}
