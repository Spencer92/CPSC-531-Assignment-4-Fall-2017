package methods;

import java.util.LinkedList;

import entities.Elevator;
import simulation.Elevators;
import states.ElevatorStates;

public class LinearScan extends Method
{

	/**
	 * 
	 * Find how much the elevator needs to move based on linear scan
	 * 
	 */
	@Override
	public double nextFloor(Elevator elevator) 
	{
		double movement;
		int next = 0;
		boolean moreAbove = false;
		boolean moreBelow = false;
		
		
		//If it was moving up, or it still can move up
		if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0 || elevator.getPrevElevatorState().compareTo(ElevatorStates.MOVING_UP) == 0)
		{
			for(int i = 0; i < elevator.floorRequestSize(); i++)
			{
				//See if there are any other floors
				if(elevator.getFloorRequest(i) > elevator.getCurrentFloorPosition())
				{
					moreAbove = true;
					break;
				}
			}
		}
		//If it was moving down, or still can
		else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0 || elevator.getPrevElevatorState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
		{
			for(int i = 0; i < elevator.floorRequestSize(); i++)
			{
				//See if there are any other floors
				if(elevator.getFloorRequest(i) < elevator.getCurrentFloorPosition())
				{
					moreBelow = true;
					break;
				}
			}
		}
		
		//Find the next floor above that can be serviced
		if(moreAbove || elevator.getCurrentFloorPosition() == 0.0)
		{
			while(next < elevator.floorRequestSize() && elevator.getFloorRequest(next) < elevator.getCurrentFloorPosition())
			{
				if(Elevators.debug)
				{
					System.out.println("Elevator floor request " + elevator.getFloorRequest(next) + " < " + " elevator position " + elevator.getCurrentFloorPosition());
				}
				next++;
			}
			
			if(next >= elevator.floorRequestSize())
			{
				next = 0;
			}
			else
			{
				for(int i = next; i < elevator.floorRequestSize(); i++)
				{
					if(elevator.getFloorRequest(next) > elevator.getFloorRequest(i) && elevator.getFloorRequest(i) > elevator.getCurrentFloorPosition())
					{
						if(Elevators.debug)
						{
							System.out.println("Elevator floor request " + elevator.getFloorRequest(next) + " > " + " elevator request " + elevator.getFloorRequest(i));
						}
						next = i;
					}
				}
			}
			elevator.setNextFloor(next);
			if(Elevators.debug)
			{
				System.out.println("Moving Up - Current Elevators in queue: ");
				for(int j = 0; j < elevator.floorRequestSize(); j++)
				{
					System.out.print(elevator.getFloorRequest(j) + ", ");
				}
				System.out.println("\n and next is " + next);
				System.out.println("Elevator current position: " + elevator.getCurrentFloorPosition());
				System.out.println("Current floor request: " + ((double)elevator.getFloorRequest(next)));
			}
			
			movement = ((double)elevator.getFloorRequest(next)) - elevator.getCurrentFloorPosition();
			return movement;
		}
		//Find the next floor below that can be serviced
		else if(moreBelow || elevator.getCurrentFloorPosition() != 0.0)
		{
			while(next < elevator.floorRequestSize() && elevator.getFloorRequest(next) > elevator.getCurrentFloorPosition())
			{
				if(Elevators.debug)
				{
					System.out.println("Elevator floor request " + elevator.getFloorRequest(next) + " > " + " elevator position " + elevator.getCurrentFloorPosition());
				}				
				next++;
			}
			
			if(next >= elevator.floorRequestSize())
			{
				next = 0;
			}
			else
			{
				for(int i = next; i < elevator.floorRequestSize(); i++)
				{
					if(elevator.getFloorRequest(next) < elevator.getFloorRequest(i) && elevator.getFloorRequest(i) < elevator.getCurrentFloorPosition())
					{
						if(Elevators.debug)
						{
							System.out.println("Prev floor request: " + elevator.getFloorRequest(next));
							System.out.println("Elevator floor request " + elevator.getFloorRequest(next) + " < " + " elevator request " + elevator.getFloorRequest(i));
						}
						next = i;
						if(Elevators.debug)
						{
							System.out.println("New floor request: " + elevator.getFloorRequest(next));
						}
					}
				}
			}
			elevator.setNextFloor(next);
			
			if(Elevators.debug)
			{
				System.out.println("Moving Down - Current Elevators in queue: ");
				for(int j = 0; j < elevator.floorRequestSize(); j++)
				{
					System.out.print(elevator.getFloorRequest(j) + ", ");
				}
				System.out.println("\n and next is " + next);
				System.out.println("Elevator current position: " + elevator.getCurrentFloorPosition());
				System.out.println("Current floor request: " + ((double)elevator.getFloorRequest(next)));
			}
			
			movement = elevator.getCurrentFloorPosition() - ((double)elevator.getFloorRequest(next));
			return movement;
		}
		return 0.0;
	}


}
