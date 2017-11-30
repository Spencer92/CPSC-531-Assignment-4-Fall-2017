package methods;

import java.util.LinkedList;

import entities.Elevator;
import states.ElevatorStates;

public class LinearScan extends Method
{

	public LinearScan() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void add(int floorRequest) 
	{
//		floorRequests.add(floorRequest);
	}

	@Override
	public int nextFloor(Elevator elevator) 
	{
		int next = 0;
		if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0 || elevator.getState().compareTo(ElevatorStates.STOP_UP) == 0)
		{
			next = elevator.getFloorRequest(elevator.floorRequestSize()-1);
			for(int i = 0; i < elevator.floorRequestSize()-1; i++)
			{
				if(elevator.getFloorRequest(next) > elevator.getFloorRequest(i))
				{
					next = i;
				}
			}
		}
		else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0 || elevator.getState().compareTo(ElevatorStates.STOP_DOWN) == 0)
		{
			next = elevator.getFloorRequest(elevator.floorRequestSize()-1);
			for(int i = 0; i < elevator.floorRequestSize()-1; i++)
			{
				if(elevator.getFloorRequest(next) < elevator.getFloorRequest(i))
				{
					next = i;
				}
			}
		}
		
		return next;
	}

	@Override
	public double floorTimeJump(double elevatorFloor, double personFloor) {
		// TODO Auto-generated method stub
		return 0;
	}

}
