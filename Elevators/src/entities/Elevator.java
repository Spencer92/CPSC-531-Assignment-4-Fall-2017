package entities;

import java.util.LinkedList;

import simulation.Elevators;
import states.ElevatorStates;

public class Elevator 
{
	
	private int floorOn;
	private double currentFloorPosition;
	private ElevatorStates state;
	private int name;
	private Elevators elevator;
	LinkedList<Integer> floorRequests = new LinkedList<Integer>();
	
	
	public Elevator(Elevators elevator)
	{
		this.elevator = elevator;
		this.currentFloorPosition = 0;
		this.name = elevator.getElevatorName();
		this.state = ElevatorStates.IDLE;
	}

	public void addFloorRequest(int floorRequest)
	{
		floorRequests.add(floorRequest);
	}
	
	public void removeFloorRequest()
	{
		floorRequests.removeFirst();
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
	
	public int getFloorOn() {
		return floorOn;
	}


	public void setFloorOn(int floorOn) {
		this.floorOn = floorOn;
	}


	public double getCurrentFloorPosition() {
		return currentFloorPosition;
	}


	public void setCurrentFloorPosition(double currentFloorPosition) {
		this.currentFloorPosition = currentFloorPosition;
	}


	public ElevatorStates getState() {
		return state;
	}


	public void setState(ElevatorStates state) {
		this.state = state;
	}


	public Elevators getElevator() {
		return elevator;
	}


	public void setElevator(Elevators elevator) {
		this.elevator = elevator;
	}


	public int getName() {
		return name;
	}
	
	public double distanceFromFloor()
	{
		double distance = 0;
		if(this.state.compareTo(ElevatorStates.MOVING_UP) == 0)
		{
			distance = (double) ((int) this.currentFloorPosition+1);
			distance -= this.currentFloorPosition;
		}
		else if(this.state.compareTo(ElevatorStates.MOVING_DOWN) == 0)
		{
			distance = (double) ((int) this.currentFloorPosition-1);
			distance = this.currentFloorPosition - distance;
		}
		else if(this.state.compareTo(ElevatorStates.IDLE) == 0)
		{
			distance = 0;
		}
		else
		{
			try {
				throw new Exception("Should never come here");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return distance;
	}
	
	/*
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
		this.floorProgress = (double) this.floorOn;
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
	*/
}
