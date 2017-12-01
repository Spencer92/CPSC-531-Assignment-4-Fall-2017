package methods;

import java.util.LinkedList;

import entities.Elevator;
import simulation.Elevators;

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
	public double nextFloor(Elevator elevator)
	{
		if(Elevators.debug)
		{
			System.out.println("Entered nextFloor");
		}
		
		return Math.abs(elevator.getCurrentFloorPosition()-elevator.getFloorRequest(0));
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
