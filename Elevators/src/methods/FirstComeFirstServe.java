package methods;

import java.util.LinkedList;

import entities.Elevator;
import simulation.Elevators;

public class FirstComeFirstServe extends Method
{

	/**
	 * Just get the movement of the next floor requested
	 */
	@Override
	public double nextFloor(Elevator elevator)
	{
		if(Elevators.debug)
		{
			System.out.println("Entered nextFloor");
		}
		
		return Math.abs(elevator.getCurrentFloorPosition()-elevator.getFloorRequest(0));
	}


}
