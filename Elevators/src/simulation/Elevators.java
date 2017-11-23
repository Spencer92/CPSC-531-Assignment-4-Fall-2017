package simulation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import entities.*;
import states.PersonStates;
import states.NextEventStates;

public class Elevators {

	public static void main(String[] args) {

		new Elevators(args);
	}
	
	
	public static final int SEED_PERSON_ARRIVAL = 10009;
	public static final int SEED_PERSON_WORK = 20089;
	public static final int SEED_PERSON_FLOOR = 30071;
	public static final int SEED_INDECISIVE = 40093;
	private Random personArrival;
	private Random personWork;
	private Random personFloor;
	private Random indecisive;
	private int floors = 1;
	private double lambdaArrival = 0.5;
	private double meanWorkRate = 60;
	private double floorChangeRate = 1/6;
	private double timeStamp;
	private int numElevators;
	private boolean debug = true;
	private int personNumber;
	private int elevatorNumber;
	private int next;
	private int eventAfterNext;
	
	public Elevators(String [] args)
	{
		personArrival = new Random(SEED_PERSON_ARRIVAL);
		personWork = new Random(SEED_PERSON_WORK);
		personFloor = new Random(SEED_PERSON_FLOOR);
		indecisive = new Random(SEED_INDECISIVE);
		timeStamp = 0;
		numElevators = 1;
		personNumber = 0;
		elevatorNumber = 0;
	}
	
	public void start()
	{
		LinkedList<Person> people = new LinkedList<Person>();
		LinkedList<Integer> floorRequests = new LinkedList<Integer>();
		LinkedList<Person> tempPeopleList = new LinkedList<Person>();
		Elevator [] tempElevatorList = new Elevator[numElevators];
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
				}
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
				}
				else if(afterNextEventState == NextEventStates.ELEVATOR_DEPARTURE)
				{
	//				timeJump = tempPeopleList.get(eventAfterNext).getArrivalTime() - tempPeopleList.get(next).getArrivalTime();
					for(int j = 0; j < elevator.length; j++)
					{
						tempElevatorList[j] = elevator[j];
					}
					
					timeJump = tempElevatorList[eventAfterNext].getFloorProgress() - tempPeopleList.get(next).getWorkTime();
					
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
				}
			}
			else if(nextEventState == NextEventStates.PERSON_DEPARTURE)
			{
				
			}
			else if(nextEventState == NextEventStates.ELEVATOR_DEPARTURE)
			{
				
			}
		}
		
		
		//Generate person
		
		//check all people and elevators to see which event goes first
		
		//move time until the start of next event
		
		
		
		
		
		
		
	}
	
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
