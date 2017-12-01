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
	private double currentFloor;
	private double elevatorWait;
	
	public Person(Elevators elevator, double prevArrivalTime)
	{
		this.elevator = elevator;
		this.floor = elevator.getFloor()*10;
		this.workTime = elevator.getWork();
		this.arrival = round(elevator.getArrival() + prevArrivalTime);
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
		this.elevatorWait = round(elevatorWait);
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
	
	public double getCurrentFloor()
	{
		return this.currentFloor;
	}
	
	public void setCurrentFloor(double currentFloor)
	{
		this.currentFloor = round(currentFloor);
	}
	
	public double getAddedWaitTime()
	{
		return this.addedWaitTime;
	}
	
	public void setAddedWaitTime(double addedWaitTime)
	{
		this.addedWaitTime = round(addedWaitTime);
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
	
	public double getNextRelevantTime()
	{
		return nextRelevantTime;
	}
	
	public void setNextRelevantTime(double nextRelevantTime)
	{
		this.nextRelevantTime = round(nextRelevantTime);
	}
	
	public double getLeaveDelay()
	{
		return this.leaveDelay;
	}
	
	public void setLeaveDelay(double delay)
	{
		this.leaveDelay = round(delay);
	}
	
	public double getWorkDelay()
	{
		return this.workDelay;
	}
	
	public void setWorkDelay(double workDelay)
	{
		this.workDelay = round(workDelay);
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
