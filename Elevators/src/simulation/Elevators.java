package simulation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import entities.*;
import methods.FirstComeFirstServe;
import methods.Method;
import states.PersonStates;
import states.ElevatorStates;
import states.NextEventStates;

public class Elevators {

	public static void main(String[] args) {

		new Elevators(args);
	}
	
	
	public static final int SEED_PERSON_ARRIVAL = 10009;
	public static final int SEED_PERSON_WORK = 20089;
	public static final int SEED_PERSON_FLOOR = 30071;
	public static final int SEED_INDECISIVE = 40093;
	public static final int SEED_ELEVATOR_DECISION = 50021;
	private Random personArrival;
	private Random personWork;
	private Random personFloor;
	private Random indecisive;
	private int floors = 1;
	private double lambdaArrival = 0.5;
	private double meanWorkRate = 60;
	private double floorChangeRate = 10;
	private double timeStamp;
	private int numElevators;
	private boolean debug = true;
	private int personNumber;
	private int elevatorNumber;
	private int next;
	private int eventAfterNext;
	private double minute = 60.0;
	private Random elevatorDecision;
	
	public Elevators(String [] args)
	{
		personArrival = new Random(SEED_PERSON_ARRIVAL);
		personWork = new Random(SEED_PERSON_WORK);
		personFloor = new Random(SEED_PERSON_FLOOR);
		indecisive = new Random(SEED_INDECISIVE);
		elevatorDecision = new Random(SEED_ELEVATOR_DECISION);
		timeStamp = 0;
		numElevators = 1;
		personNumber = 0;
		elevatorNumber = 0;
	}
	
	
	
	
	public void start()
	{
		LinkedList<Person> people = new LinkedList<Person>();
		LinkedList<Elevator> elevators = new LinkedList<Elevator>();
		int nextElevator;
		int nextPerson;
		Method moveMethod = new FirstComeFirstServe();
		double timeSkip;
		int closestElevator;
		
		for(int i = 0; i < numElevators; i++)
		{
			elevators.add(new Elevator(this));
		}
		
		people.add(new Person(this));
		
		for(int i = 0; i < 10; i++)
		{
			nextElevator = -1;
			
			//Check for idle elevators
			for(int j = 0; j < elevators.size(); j++)
			{
				if(elevators.get(j).getState().compareTo(ElevatorStates.IDLE) == 0)
				{
					nextElevator = j;
					break;
				}
			}
			
			
			//There is an idle elevator
			if(nextElevator != -1)
			{
				//find the next relevant person
				nextPerson = nextPersonEvent(people);

				//check to see if the elevator is on that floor
				if(!(elevators.get(nextElevator).getCurrentFloorPosition() == people.get(nextPerson).getCurrentFloor()))
				{
					elevators.get(nextElevator).addFloorRequest(people.get(nextPerson).getCurrentFloor());
					
					//see what floor to move
					if(elevators.get(nextElevator).getCurrentFloorPosition() > people.get(nextPerson).getCurrentFloor())
					{
						elevators.get(nextElevator).setState(ElevatorStates.MOVING_DOWN);

					}
					else
					{
						elevators.get(nextElevator).setState(ElevatorStates.MOVING_UP);
					}
					people.get(nextPerson).setState(PersonStates.WAITING);
				}
				else
				{
					for(Person person : people)
					{
						if(person.getCurrentFloor() == elevators.get(nextElevator).getCurrentFloorPosition()
								&&
								person.getState().compareTo(PersonStates.WAITING) == 0)
						{
							elevators.get(nextElevator).addFloorRequest(person.getFloor());
							person.setState(PersonStates.IN_ELEVATOR);
						}
						else if(person.getFloor() == elevators.get(nextElevator).getCurrentFloorPosition()
								&&
								person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
						{
							//Need to do waiting in elevator to leave
							//and waiting in elevator to work
						}
					}
				}
			}
			else
			{
				//Still get next person, and add request
				nextPerson = nextPersonEvent(people);
				nextElevator = elevatorDecision.nextInt(elevators.size());
				elevators.get(nextElevator).addFloorRequest(people.get(nextPerson).getCurrentFloor());
			}
			
			closestElevator = shortestElevatorToFloor(elevators);
			
			if(timeStamp + elevators.get(closestElevator).distanceFromFloor() < people.get(nextPerson).getNextRelevantTime())
			{
				timeStamp += elevators.get(closestElevator).distanceFromFloor();
				for(int j = 0; j < elevators.size(); j++)
				{
					if(elevators.get(j).getState().compareTo(ElevatorStates.MOVING_UP) == 0)
					{
						elevators.get(j).setCurrentFloorPosition(elevators.get(j).getCurrentFloorPosition()+elevators.get(closestElevator).distanceFromFloor());
					}
					else if(elevators.get(j).getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
					{
						elevators.get(j).setCurrentFloorPosition(elevators.get(j).getCurrentFloorPosition()-elevators.get(closestElevator).distanceFromFloor());						
					}
				}
			}
			else
			{
				timeStamp += timeSkip = people.get(nextPerson).getNextRelevantTime()-elevators.get(closestElevator).distanceFromFloor();
				for(int j = 0; j < elevators.size(); j++)
				{
					if(elevators.get(j).getState().compareTo(ElevatorStates.MOVING_UP) == 0)
					{
						elevators.get(j).setCurrentFloorPosition(elevators.get(j).getCurrentFloorPosition()+timeSkip);
					}
					else if(elevators.get(j).getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
					{
						elevators.get(j).setCurrentFloorPosition(elevators.get(j).getCurrentFloorPosition()-timeSkip);						
					}
				}
			}
			
		}
		
		
		//Person arrives
		
		//Person wants floor
		
			//Case 1 - Elevator is busy
				
				//put request in
		
					//while not in elevator
		
						//add delay
		
			//Case 2 - Elevator is idle
				
				//If elevator not on floor
		
					//put request in
		
						//while not in elevator
						
							//add delay
		
		//Person is in elevator
		
		//Person works
	
		//Person finishes working
	
		//person wants floor
	
			//Case 1 - Elevator is busy
		
				//put request in
			
					//while not in elevator
	
						//add delay
		
			//Case 2 - Elevator is idle
		
				//if elevator not on floor
		
					//put request in
		
						//while not in elevator
		
							//add delay
		
		
		
		
		
	}
	
	
	public int shortestElevatorToFloor(LinkedList<Elevator> elevators)
	{
		int smallest = elevators.size()-1;
		
		for(int i = 0; i < elevators.size()-1; i++)
		{
			if(elevators.get(smallest).distanceFromFloor() > elevators.get(i).distanceFromFloor())
			{
				smallest = i;
			}
		}
		return smallest;
	}
	
	
	public int nextPersonEvent(LinkedList<Person> people)
	{
		int smallest = people.size()-1;
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(i).getNextRelevantTime() < people.get(smallest).getNextRelevantTime())
			{
				smallest = i;
			}
		}
		
		return smallest;
	}
	
	
	/*
	public void start()
	{
		LinkedList<Person> people = new LinkedList<Person>();
		LinkedList<Elevator> elevators = new LinkedList<Elevator>();
		Method nextFloor = new FirstComeFirstServe();
		int availableElevator;
		int floorCrossingTime = 10; //in seconds
		
		//Check status of all people
		//Check status of all elevators
		
		for(int i = 0; i < numElevators; i++)
		{
			elevators.add(new Elevator(this));
		}
		
		people.add(new Person(this));
		
		for(int i = 0; i < 10; i++)
		{
			availableElevator = allBusyElevators(elevators);
			if(availableElevator != -1)
			{
				for(Person person : people)
				{
					if(!((person.getState().compareTo(PersonStates.WAITING_TO_WORK) == 0
							||	
							person.getState().compareTo(PersonStates.WAITING_TO_LEAVE) == 0)
							&&
							person.getCurrentFloor() == elevators.get(availableElevator).getFloorOn()))
					{
						nextFloor.add(person.getFloorRequested());
						
						
					}
						
				}
			}
			
			
			people.add(new Person(this));
			
			
		}
		
		
		
	}
	
	
	private double moveForwardTime(Person person, Elevator elevator)
	{
		if(person.getState().compareTo(PersonStates.WAITING_TO_WORK) == 0)
		{
			if((elevator.getFloorProgress() + floorChangeRate)/minute > person.getArrivalTime())
			{
				elevator.setFloorProgress(elevator.getFloorProgress()+((((elevator.getFloorProgress()+floorChangeRate)/minute)-person.getArrivalTime())*minute));
			}
		}
	}
	
	private double abs(int val1, int val2)
	{
		if(val1 > val2)
		{
			return val1-val2;
		}
		else
		{
			return val2-val1;
		}
	}
	
	private int allBusyElevators(LinkedList<Elevator> elevators)
	{
//		int elevatorsBusy = 0;
		int specificElevator = -1;
		for(int i = 0; i < elevators.size(); i++)
		{
			if(elevators.get(i).getState().compareTo(ElevatorStates.IDLE) == 0)
			{
//				elevatorsBusy++;
				specificElevator = i;
				break;
			}
		}
		
		return specificElevator;
		
	}
	
	*/
/*	
	public void start()
	{
		LinkedList<Person> people = new LinkedList<Person>();
		LinkedList<Integer> floorRequests = new LinkedList<Integer>();
		LinkedList<Person> tempPeopleList = new LinkedList<Person>();
		LinkedList<Elevator> tempElevators = new LinkedList<Elevator>();
		Elevator [] elevator = new Elevator[numElevators];
//		Integer next = null;
//		Integer afterNext = null;
		NextEventStates nextEventState;
		NextEventStates afterNextEventState;
		Scanner scanner = new Scanner(System.in);
		double timeJump = 0;
		
		for(int i = 0; i < elevator.length; i++)
		{
			elevator[i] = new Elevator(this);
		}
		
		for(int i = 0; i < 10; i++)
		{
			people.add(new Person(this));
			if(debug)
			{
				for(int j = 0; j < people.size(); j++)
				{
					System.out.println("Person " + people.get(j) + " is currently" + people.get(j).getState() + " and wants floor " + people.get(j).getFloorRequested());
				}
				for(int j = 0; j < elevator.length; j++)
				{
					System.out.println("Elevator " + elevator[j] + " is currently" + elevator[j].getState());
				}
				scanner.nextLine();
			}
			
			nextEventState = nextEvent(people,elevator,timeStamp);
			
			if(debug)
			{
				System.out.println("The next thing that will happen is " + nextEventState);
				scanner = new Scanner(System.in);
				scanner.nextLine();
			}
			
			
			if(nextEventState == NextEventStates.PERSON_ARRIVAL)
			{
				tempPeopleList = new LinkedList<Person>();
				for(int j = 0; j < people.size(); j++)
				{
					if(j != next)
					{
						tempPeopleList.add(people.get(j));
					}
				}
				afterNextEventState = eventAfterNext(tempPeopleList,elevator);
				if(afterNextEventState == NextEventStates.PERSON_ARRIVAL)
				{
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.PERSON_DEPARTURE)
				{
					
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getWorkTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.ELEVATOR_DEPARTURE)
				{
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					for(int j = 0; j < elevator.length; j++)
					{
						if(j != next)
						{
							tempElevators.add(elevator[j]);
						}
					}
					
					timeJump = tempElevators.get(eventAfterNext).getFloorProgress() - tempPeopleList.get(next).getWorkTime();
					
					timeStamp += timeJump;
					
					/*					for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
			}
			else if(nextEventState == NextEventStates.PERSON_DEPARTURE)
			{
				tempPeopleList = new LinkedList<Person>();
				for(int j = 0; j < people.size(); j++)
				{
					if(j != next)
					{
						tempPeopleList.add(people.get(j));
					}
				}
				afterNextEventState = eventAfterNext(tempPeopleList,elevator);
				if(afterNextEventState == NextEventStates.PERSON_ARRIVAL)
				{
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.PERSON_DEPARTURE)
				{
					
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getWorkTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.ELEVATOR_DEPARTURE)
				{
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					for(int j = 0; j < elevator.length; j++)
					{
						tempElevators.add(elevator[j]);
					}
					
					timeJump = tempElevators.get(eventAfterNext).getFloorProgress() - tempPeopleList.get(next).getWorkTime();
					
					timeStamp += timeJump;
					
					/*					for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
			}
			else if(nextEventState == NextEventStates.ELEVATOR_DEPARTURE)
			{
				tempPeopleList = new LinkedList<Person>();
				for(int j = 0; j < people.size(); j++)
				{
					if(j != next)
					{
						tempPeopleList.add(people.get(j));
					}
				}
				afterNextEventState = eventAfterNext(tempPeopleList,elevator);
				if(afterNextEventState == NextEventStates.PERSON_ARRIVAL)
				{
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.PERSON_DEPARTURE)
				{
					
					timeJump = timeJumpPerson(afterNextEventState,eventAfterNext,tempPeopleList,next);
					
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getWorkTime();
					timeStamp += timeJump;
	/*				for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
				else if(afterNextEventState == NextEventStates.ELEVATOR_DEPARTURE)
				{
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					for(int j = 0; j < elevator.length; j++)
					{
						tempElevators.add(elevator[j]);
					}
					
					timeJump = tempElevators.get(eventAfterNext).getFloorProgress() - tempPeopleList.get(next).getWorkTime();
					
					timeStamp += timeJump;
					
					/*					for(Person person : people)
					{
						person.setArrivalTime(person.getArrivalTime()-timeJump);
					}
					for(int j = 0; j < elevator.length; j++)
					{
						elevator[j].setFloorProgress(elevator[j].getFloorProgress()-timeJump);
					}
					tempPeopleList = null;
					tempElevatorList = null;*/
/*				}
			}
		}
		
		
		//Generate person
		
		//check all people and elevators to see which event goes first
		
		//move time until the start of next event
		
		
		
		
		
		
		
	}*/
/*	
	public double timeJumpPerson(NextEventStates eventAfterNext, int entityAfterNext, LinkedList<Person> tempPeopleList, int next)
	{
		
		if(eventAfterNext == NextEventStates.PERSON_ARRIVAL)
		{
			return tempPeopleList.get(entityAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
		}
		else if(eventAfterNext == NextEventStates.PERSON_DEPARTURE)
		{
			return tempPeopleList.get(entityAfterNext).getArrivalTime() - tempPeopleList.get(next).getWorkTime();
		}

		return 0;
	}
	*/
	
	
/*	
	public NextEventStates nextEvent(LinkedList<Person> people, Elevator[] elevator, double base)
	{
		NextEventStates lowest = null;
		int nextArrivalEvent = people.size()-1;
		int nextDepartureEvent = people.size()-1;
		int nextElevatorEvent = elevator.length-1;
		
		
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(nextArrivalEvent).getArrivalTime() > base && people.get(nextArrivalEvent).getArrivalTime() > people.get(i).getArrivalTime())
			{
				nextArrivalEvent = i;
			}
		}
		
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(nextDepartureEvent).getWorkTime() > base && people.get(nextDepartureEvent).getWorkTime() > people.get(i).getWorkTime())
			{
				nextDepartureEvent = i;
			}
		}
		
		for(int i = 0; i < elevator.length-1; i++)
		{
			if(elevator[nextElevatorEvent].getFloorProgress() < elevator[i].getFloorProgress())
			{
				nextElevatorEvent = i;
			}
		}
	//You left here to update the base	
		if(people.get(nextArrivalEvent).getArrivalTime() < people.get(nextDepartureEvent).getWorkTime() && people.get(nextArrivalEvent).getArrivalTime() < elevator[nextElevatorEvent].getFloorProgress())
		{
			lowest = NextEventStates.PERSON_ARRIVAL;
			next = nextArrivalEvent;
		}
		else if(people.get(nextDepartureEvent).getWorkTime() < people.get(nextArrivalEvent).getArrivalTime() && people.get(nextDepartureEvent).getWorkTime() < elevator[nextElevatorEvent].getFloorProgress())
		{
			lowest = NextEventStates.PERSON_DEPARTURE;
			next = nextDepartureEvent;
		}
		else if(elevator[nextElevatorEvent].getFloorProgress() < people.get(nextArrivalEvent).getArrivalTime() && elevator[nextElevatorEvent].getFloorProgress() < people.get(nextDepartureEvent).getWorkTime())
		{
			lowest = NextEventStates.ELEVATOR_DEPARTURE;
			next = nextElevatorEvent;
		}
		else
		{
			try {
				throw new Exception("Everything is identical??");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return lowest;
	}
	
	public NextEventStates eventAfterNext(LinkedList<Person> people, Elevator[] elevator)
	{
		NextEventStates lowest = null;
		int nextArrivalEvent = people.size()-1;
		int nextDepartureEvent = people.size()-1;
		int nextElevatorEvent = elevator.length-1;
		
		
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(nextArrivalEvent).getArrivalTime() > people.get(i).getArrivalTime())
			{
				nextArrivalEvent = i;
			}
		}
		
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(nextDepartureEvent).getWorkTime() > people.get(i).getWorkTime())
			{
				nextDepartureEvent = i;
			}
		}
		
		for(int i = 0; i < elevator.length-1; i++)
		{
			if(elevator[nextElevatorEvent].getFloorProgress() < elevator[i].getFloorProgress())
			{
				nextElevatorEvent = i;
			}
		}
		
		if(people.get(nextArrivalEvent).getArrivalTime() < people.get(nextDepartureEvent).getWorkTime() && people.get(nextArrivalEvent).getArrivalTime() < elevator[nextElevatorEvent].getFloorProgress())
		{
			lowest = NextEventStates.PERSON_ARRIVAL;
			eventAfterNext = nextArrivalEvent;
		}
		else if(people.get(nextDepartureEvent).getWorkTime() < people.get(nextArrivalEvent).getArrivalTime() && people.get(nextDepartureEvent).getWorkTime() < elevator[nextElevatorEvent].getFloorProgress())
		{
			lowest = NextEventStates.PERSON_DEPARTURE;
			eventAfterNext = nextDepartureEvent;
		}
		else if(elevator[nextElevatorEvent].getFloorProgress() < people.get(nextArrivalEvent).getArrivalTime() && elevator[nextElevatorEvent].getFloorProgress() < people.get(nextDepartureEvent).getWorkTime())
		{
			lowest = NextEventStates.ELEVATOR_DEPARTURE;
			eventAfterNext = nextElevatorEvent;
		}
		else
		{
			try {
				throw new Exception("Everything is identical??");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return lowest;
	}
*/	
	public int getElevatorName()
	{
		return this.elevatorNumber++;
	}
	
	public int getPersonName()
	{
		return this.personNumber++;
	}

	public double getArrival()
	{
		return distribution(lambdaArrival,personArrival.nextDouble());
	}
	
	public double getWork()
	{
		return distribution(1/meanWorkRate,personWork.nextDouble());
	}
	
	public int getFloor()
	{
		return this.personFloor.nextInt(floors) + 1;
	}
	
	public boolean indecisive()
	{
		return indecisive.nextBoolean();
	}
	
	public double getTimeStamp()
	{
		return this.timeStamp;
	}

	private double distribution(double lambda, double randomNumber)
	{
		return ((1.0/lambda) * Math.log(1.0-randomNumber))*-1;
	}
}
