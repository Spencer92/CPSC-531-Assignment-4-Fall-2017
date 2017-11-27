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
	private int floors = 2;
	private double lambdaArrival = 1.0/30.0;//0.5 rate change from sec to minutes
	private double meanWorkRate = 3600;
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
		
		System.out.println(lambdaArrival);
		
		start();
	}
	
	
	public void start()
	{
		LinkedList<Elevator> elevators = new LinkedList<Elevator>();
		LinkedList<Person> people = new LinkedList<Person>();
		int nextElevator = -1;
		int nextPerson;
		Person aPerson = new Person(this,0);
		Scanner input = new Scanner(System.in);
		
		
		for(int i = 0; i < numElevators; i++)
		{
			elevators.add(new Elevator(this));
		}
		
		//Elevator: 
		//Elevator waits for request
		//Once request given, elevator moves to floor
		//If more requests given, elevator puts in queue
			//Elevator processes requests based on method
		
		//Person: 
		//Person arrives
		//Person presses button
		//Person waits
		//Once time hits person arrival, then new person generated
		
		while(timeStamp < 3600)
		{
			nextElevator = -1;
			for(int j = 0; j < elevators.size(); j++)
			{
				if(elevators.get(j).getState().compareTo(ElevatorStates.IDLE) == 0)
				{
					nextElevator = j;
					break;
				}
			}
			
			if(nextElevator == -1)
			{
				nextElevator = elevatorDecision.nextInt(elevators.size());
				if(debug)
				{
					System.out.println("All elevators busy");
				}
			}
			
			if(debug)
			{
				System.out.println("The next elevator will be: " + nextElevator);
				
				System.out.println("THe next person arrival will be " + aPerson.getName());
				System.out.println("and will arrive at: " + aPerson.getAbsArrival());
				input.nextLine();
				input = new Scanner(System.in);
			}
			
			//The next elevator needs to do something
			
			if(people.size() > 0)
			{
				nextPerson = nextPersonEvent(people);
				
				if(debug)
				{
					System.out.println("The next person parsed is " + people.get(nextPerson).getName());
					System.out.println("The state of all people are: ");
					
					for(Person person : people)
					{
						System.out.println("Person " + person.getName() + " is " + person.getState());
					}
					
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				//Elevator is on same floor
				if(elevators.get(nextElevator).getCurrentFloorPosition() == people.get(nextPerson).getCurrentFloor())
				{
					if(debug)
					{
						System.out.println("And elevator " + elevators.get(nextElevator).getName() + " is on the same floor");
						input.nextLine();
						input = new Scanner(System.in);
					}
					
					elevators.get(nextElevator).addFloorRequest(people.get(nextPerson).getFloor());
//					people.get(nextPerson).setFloor(0);
					people.get(nextPerson).setState(PersonStates.IN_ELEVATOR);
					
					for(Person person : people)
					{
						if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getFloor() == 0 && elevators.get(nextElevator).getCurrentFloorPosition() == 0.0)
						{
							person.setState(PersonStates.LEFT);
						}
						else if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getFloor() != 0 && elevators.get(nextElevator).getCurrentFloorPosition() == person.getFloor())
						{
							person.setState(PersonStates.WORKING);
							person.setFloor(0);
							person.setNextRelevantTime(person.getAbsArrival() + person.getWorkDelay() + person.getWorkTime() + person.getElevatorWait());
						}
						else if(person.getState().compareTo(PersonStates.WAITING) == 0)
						{
							person.setState(PersonStates.IN_ELEVATOR);
							elevators.get(nextElevator).addFloorRequest(person.getFloor());
						}
					}
					int k = 0;
					while(k < elevators.get(nextElevator).floorRequestSize())
					{
						
						if(elevators.get(nextElevator).getFloorRequest(k) == elevators.get(nextElevator).getCurrentFloorPosition())
						{
							elevators.get(nextElevator).removeFloorRequest(k);
						}
						else
						{
							k++;
						}
					}
					if(elevators.get(nextElevator).floorRequestSize() == 0)
					{
						elevators.get(nextElevator).setState(ElevatorStates.IDLE);
					}
					else
					{
						if(elevators.get(nextElevator).getFloorRequest(0) > elevators.get(nextElevator).getCurrentFloorPosition())
						{
							elevators.get(nextElevator).setState(ElevatorStates.MOVING_UP);
						}
						else
						{
							elevators.get(nextElevator).setState(ElevatorStates.MOVING_DOWN);
						}

					}
					if(debug)
					{
						System.out.println("Elevator " + elevators.get(nextElevator).getName() + " is " + elevators.get(nextElevator).getState());
						System.out.println("And currently in position " + elevators.get(nextElevator).getCurrentFloorPosition());
						input.nextLine();
						input = new Scanner(System.in);
					}
					
				}
				else
				{
					if(debug)
					{
						System.out.println("And elevator " + elevators.get(nextElevator).getName() + " is not on same floor");
						input.nextLine();
						input = new Scanner(System.in);
					}
					
					if(people.get(nextPerson).getState().compareTo(PersonStates.WAITING) != 0)
					{
						elevators.get(nextElevator).addFloorRequest(people.get(nextPerson).getFloor());
					}
					
				}
				int shortestElevatorRequestTime;
				
				shortestElevatorRequestTime = shortestElevatorToFloor(elevators);
				
				double aDouble = elevators.get(shortestElevatorRequestTime).getFloorRequest(0);
				aDouble += timeStamp;
				double moveTime = aDouble - elevators.get(shortestElevatorRequestTime).getCurrentFloorPosition();
				
				if(moveTime < 0.0)
				{
					moveTime *= -1;
				}
				
				if(debug)
				{
					System.out.println("The next moveTime will be " + moveTime + " for elevator " + elevators.get(nextElevator).getName());
					System.out.println("The next floor the elevator is going to is " + elevators.get(nextElevator).getFloorRequest(0));
					System.out.println("The next personTime will be " + people.get(nextPerson).getNextRelevantTime() + " for person " + 
					people.get(nextPerson).getName());
				}
				
				if(moveTime > people.get(nextPerson).getNextRelevantTime())
				{
					if(debug)
					{
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}
					moveTime -= people.get(nextPerson).getNextRelevantTime();
					timeStamp += moveTime;
					if(people.get(nextPerson).getCurrentFloor() == 0)
					{
						if(debug)
						{
							System.out.println("And they are coming to work");
						}
						aPerson = new Person(this,people.get(nextPerson).getAbsArrival());
					}
					
					if(debug)
					{
						input.nextLine();
						input = new Scanner(System.in);
					}
				}
				else
				{
					if(debug)
					{
						System.out.println("Next event will be elevator " + elevators.get(shortestElevatorRequestTime) + " doing something");
					}
					timeStamp += moveTime;
					
					if(debug)
					{
						input.nextLine();
						input = new Scanner(System.in);
					}
				}
				
				if(debug)
				{
					System.out.println("Current time stamp is " + timeStamp);
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				for(Person person : people)
				{
					if(person.getState().compareTo(PersonStates.WAITING) == 0)
					{
						if(person.getCurrentFloor() == 0)
						{
							person.setWorkDelay(person.getWorkDelay() + moveTime);
						}
						else
						{
							person.setLeaveDelay(person.getLeaveDelay() + moveTime);
						}
					}
					else if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
					{
						person.setElevatorWait(person.getElevatorWait() + moveTime);
					}
				}
				
				for(Elevator elevator : elevators)
				{
					if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
					{
						elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
					}
				}
				
			}
			else
			{
				timeStamp = aPerson.getAbsArrival();
				aPerson.setState(PersonStates.WAITING);
				people.add(aPerson);
				aPerson = new Person(this,aPerson.getAbsArrival());
			}
			
		}
	}
	
	
	
	public int shortestElevatorToFloor(LinkedList<Elevator> elevators)
	{
		int shortest = 0;
		LinkedList<Elevator> activeElevators = new LinkedList<Elevator>();
		
		for(Elevator elevator : elevators)
		{
			if(elevator.getState().compareTo(ElevatorStates.IDLE) != 0)
			{
				activeElevators.add(elevator);
			}
		}
		
		if(activeElevators.size() > 0)
		{
			for(int i = 0; i < activeElevators.size(); i++)
			{
				if(Math.abs(activeElevators.get(shortest).getCurrentFloorPosition()-activeElevators.get(shortest).getFloorRequest(0)) > 
				Math.abs(activeElevators.get(i).getCurrentFloorPosition()-activeElevators.get(i).getFloorRequest(0)))
				{
					shortest = i;
				}
			}
			return shortest;
		}
		else
		{
			return -1;
		}
		
	}
	
	
	public int nextPersonEvent(LinkedList<Person> people)
	{
		int smallest = people.size()-1;
		for(int i = 0; i < people.size()-1; i++)
		{
			if(people.get(i).getState().compareTo(PersonStates.LEFT) != 0 && people.get(i).getNextRelevantTime() < people.get(smallest).getNextRelevantTime())
			{
				smallest = i;
			}
		}
		
		return smallest;
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
		return distribution(lambdaArrival,personArrival.nextDouble());//*60;//Change from minutes to seconds
	}
	
	public double getWork()
	{
		return distribution(1/meanWorkRate,personWork.nextDouble());//*60;//Change from minutes to seconds
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
//		double newLambda = lambda;
//		double value = 1.0/lambda;
//		double logging = Math.log(1.0-randomNumber);
//		System.out.println("New lambda is " + newLambda + "Value is " + value + " and logging is " + logging);
		
//		System.out.println("Random number for " + lambda + " is " + randomNumber);
//		System.out.println("Final result is " + (((1.0/lambda) * Math.log(1.0-randomNumber))*-1));
		return ((1.0/lambda) * Math.log(1.0-randomNumber))*-1;
	}
}
