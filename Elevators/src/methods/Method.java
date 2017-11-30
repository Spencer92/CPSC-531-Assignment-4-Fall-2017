package methods;

import java.util.LinkedList;

import entities.Elevator;

public abstract class Method 
{
	protected LinkedList<Integer> floorRequests = new LinkedList<Integer>();
	
	public abstract void add(int floorRequest);
	
	public abstract int nextFloor(Elevator elevator);
	
	public abstract double floorTimeJump(double elevatorFloor, double personFloor);
	
}
