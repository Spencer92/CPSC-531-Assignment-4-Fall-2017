package methods;

import java.util.LinkedList;

import entities.Elevator;

public abstract class Method 
{
	protected LinkedList<Integer> floorRequests = new LinkedList<Integer>();
	
	public abstract double nextFloor(Elevator elevator);
		
}
