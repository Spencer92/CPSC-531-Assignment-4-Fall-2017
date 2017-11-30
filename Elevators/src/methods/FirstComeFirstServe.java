package methods;

import java.util.LinkedList;

import entities.Elevator;

public class FirstComeFirstServe extends Method
{

	public FirstComeFirstServe() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void add(int floorRequest) 
	{
		floorRequests.add(floorRequest);
	
	}

	@Override
	public int nextFloor(Elevator elevator) 
	{
		return 0;
	}

	@Override
	public double floorTimeJump(double elevatorFloor, double personFloor) 
	{
		double floorChange = Math.abs(elevatorFloor-personFloor);
		
		floorChange *= 10;
		floorChange /= 60;
		
		return floorChange;
	}

}
