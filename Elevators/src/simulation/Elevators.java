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
	private double lambdaArrival = 2;// rate change from sec to minutes
	private double meanWorkRate = 300;//3600;
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
		LinkedList<Person> leftPeople = new LinkedList<Person>();
		int nextElevator = -1;
		int nextPerson;
		Person aPerson = new Person(this,0);
		Scanner input = new Scanner(System.in);
		
		System.out.println("Person " + aPerson.getName() + " created");
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
		
		while(timeStamp < 600)
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
				
				if(debug)
				{
					System.out.println("Current state of people: ");
					for(Person person : people)
					{
						System.out.println("Person " + person.getName() + " state: " + person.getState());
						System.out.println("Next relevant time: " + person.getNextRelevantTime());
						System.out.println("Next floor wanted: " + person.getFloor());
						System.out.println("Floor currently on: " + person.getCurrentFloor());
						System.out.println("Work time amount: " + person.getWorkTime());
					}
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				for(Person person : people)
				{
					
					if(person.getState().compareTo(PersonStates.ARRIVING) == 0 && person.getAbsArrival() == timeStamp)
					{
						elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
						person.setState(PersonStates.WAITING);
					}
					else if(person.getState().compareTo(PersonStates.WAITING) == 0 && elevators.get(nextElevator).getCurrentFloorPosition() == person.getCurrentFloor())
					{
						if(person.getCurrentFloor() != 0)
						{
							person.setFloor(0);
						}
						elevators.get(nextElevator).addFloorRequest((int)person.getFloor());
						person.setState(PersonStates.IN_ELEVATOR);
					}
					else if(person.getState().compareTo(PersonStates.WORKING) == 0 && person.getNextRelevantTime() == timeStamp)
					{
						person.setState(PersonStates.WAITING);
						person.setFloor(0);
						elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
					}
					else if(person.getState().compareTo(PersonStates.WAITING) == 0)
					{
						elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
					}
/*					else if(person.getCurrentFloor() == person.getAbsFloor() && timeStamp < person.getNextRelevantTime())
					{
						person.setState(PersonStates.WORKING);
						person.setNextRelevantTime(person.getAbsArrival()+person.getAddedWaitTime()+person.getElevatorWait()+person.getWorkTime());
						person.setFloor(0);
					}
					else if(person.getCurrentFloor() == 0 && person.getFloor() == 0)
					{
						person.setState(PersonStates.LEFT);
					}*/
				}
				
				for(Person person : people)
				{
					if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getFloor() == elevators.get(nextElevator).getCurrentFloorPosition())
					{
						if(elevators.get(nextElevator).getCurrentFloorPosition() == 0)
						{
							person.setState(PersonStates.LEFT);
						}
						else
						{
							person.setState(PersonStates.WORKING);
							person.setFloor(0);
							person.setNextRelevantTime(person.getAbsArrival()+person.getAddedWaitTime()+person.getElevatorWait()+person.getWorkTime());
						}
					}
				}
				
				int shortestElevatorRequestTime;
				
				
				shortestElevatorRequestTime = shortestElevatorToFloor(elevators);
				
				if(shortestElevatorRequestTime == -1)
				{
					for(int j = 0; j < elevators.size(); j++)
					{
						if(elevators.get(j).getState().compareTo(ElevatorStates.IDLE) == 0)
						{
							shortestElevatorRequestTime = j;
							break;
						}
					}
				}
				
				double moveTime = 0;
				
				if(debug)
				{
					System.out.println("Request Size for " + elevators.get(shortestElevatorRequestTime).getName() + ": " + elevators.get(shortestElevatorRequestTime).floorRequestSize());
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				if(elevators.get(shortestElevatorRequestTime).floorRequestSize() > 0)
				{
					double aDouble = Math.abs(elevators.get(shortestElevatorRequestTime).getFloorRequest(0)-elevators.get(shortestElevatorRequestTime).getCurrentFloorPosition());
					if(debug)
					{
						System.out.println("aDouble is " + aDouble);
						input.nextLine();
						input = new Scanner(System.in);
					}
					aDouble += timeStamp;
					moveTime = aDouble;// + elevators.get(shortestElevatorRequestTime).getCurrentFloorPosition();
				}
				else
				{
					moveTime = 0;
				}
				if(moveTime < 0.0)
				{
					moveTime *= -1;
				}
				
				
				if(isArrivalNextEvent(people,aPerson))
				{
					if(debug)
					{
						System.out.println("A new person will be arriving");
						System.out.println("TimeStamp is " + timeStamp);
						input.nextLine();
						input = new Scanner(System.in);
					}
//					aPerson.setState(PersonStates.WAITING);
					people.add(aPerson);
					nextPerson = people.size()-1;
					aPerson = new Person(this,aPerson.getAbsArrival());
					
				}
				else
				{
					if(debug)
					{
						System.out.println("No new person will be arriving yet");
						System.out.println("TimeStamp is " + timeStamp);
						input.nextLine();
						input = new Scanner(System.in);
					}
					
					for(Person person : people)
					{
						if(person.getNextRelevantTime() < timeStamp)
						{
							person.setNextRelevantTime(timeStamp);
						}
					}
					
					nextPerson = nextPersonEvent(people);
				}
				
				if(debug)
				{
					System.out.println("The next moveTime will be " + moveTime + " for elevator " + elevators.get(nextElevator).getName());
					System.out.print("The next floor the elevator is going to is ");
					try
					{
						System.out.println(elevators.get(nextElevator).getFloorRequest(0));						
					}
					catch (IndexOutOfBoundsException e)
					{
						System.out.println("Nothing yet");
					}
					System.out.println("The next personTime will be " + people.get(nextPerson).getNextRelevantTime() + " for person " + 
					people.get(nextPerson).getName());
				}


				
				if(moveTime > people.get(nextPerson).getNextRelevantTime() || moveTime > aPerson.getAbsArrival())
				{
					if(moveTime > aPerson.getAbsArrival())
					{
						aPerson.setState(PersonStates.WAITING);
						people.add(aPerson);
						aPerson = new Person(this,aPerson.getAbsArrival());
						if(debug)
						{
							System.out.println("Arriving person had to be added");
							input.nextLine();
							input = new Scanner(System.in);
						}
					}
					
					if(debug)
					{
						System.out.println("moveTime is going to be " + (moveTime - people.get(nextPerson).getNextRelevantTime()));
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}
					
					if(people.get(nextPerson).getState().compareTo(PersonStates.ARRIVING) == 0
							&&
							people.get(nextPerson).getAbsArrival() == timeStamp)
					{
						people.get(nextPerson).setState(PersonStates.WAITING);
					}
					
					moveTime -= people.get(nextPerson).getNextRelevantTime();
//					timeStamp += moveTime;
					
					double shortestNext = moveTime;
					if(debug)
					{
						if(people.size() > 1)
						{
							System.out.println("More than one person detected, finding if there is another person");
							for(Person person : people)
							{
								System.out.println("Person " + person.getName() + " nextRelevant is " + person.getNextRelevantTime());
							}							
							input.nextLine();
							input = new Scanner(System.in);
						}
					}
					
					if(people.size() > 1)
					{
						for(int j = 0; j < people.size(); j++)
						{
							if(timeStamp + shortestNext > people.get(j).getNextRelevantTime() && timeStamp < people.get(j).getNextRelevantTime())
							{
								if(debug)
								{
									System.out.println("ShortestNext is " + shortestNext);
									System.out.println("TimeStamp is " + timeStamp);
									System.out.println("Person " + people.get(j).getName() + " time is " + people.get(j).getNextRelevantTime());
									input.nextLine();
									input = new Scanner(System.in);
								}
								shortestNext = people.get(j).getNextRelevantTime() - timeStamp;
								nextPerson = j;
							}
						}
						if(people.get(nextPerson).getState().compareTo(PersonStates.ARRIVING) == 0 || people.get(nextPerson).getState().compareTo(PersonStates.WORKING) == 0)
						{
							people.get(nextPerson).setState(PersonStates.WAITING);
							elevators.get(nextElevator).addFloorRequest((int)people.get(nextPerson).getCurrentFloor());
						}
//						else if(people.get(nextPerson).getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
//						{
//							elevators.get(nextPerson).addFloorRequest((int)people.get(nextPerson).getFloor();
//						}
						moveTime = shortestNext;
					}
					timeStamp += moveTime;
					if(debug)
					{
						System.out.println("New moveTime is going to be " + moveTime);
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}
//					moveTime -= people.get(nextPerson).getNextRelevantTime();
//					timeStamp += moveTime;

					
					for(Elevator elevator : elevators)
					{
						if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
						{
/*							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() + moveTime);
								}
							}*/
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
						{
/*							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() - moveTime);
								}
							}*/
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.IDLE) == 0)
						{
							if(elevator.floorRequestSize() > 0)
							{
								if(elevator.getFloorRequest(0) > elevator.getCurrentFloorPosition())
								{
									elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
									elevator.setState(ElevatorStates.MOVING_UP);
								}
								else
								{
									elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
									elevator.setState(ElevatorStates.MOVING_DOWN);
								}
							}
						}
					}
					
					
					if(people.get(nextPerson).getCurrentFloor() == 0.0)
					{
						if(debug)
						{
							System.out.println("And they are coming to work");
						}
//						aPerson = new Person(this,aPerson.getAbsArrival());
					}
					
					if(debug)
					{
						input.nextLine();
						input = new Scanner(System.in);
					}
				}
/*				else if(moveTime > aPerson.getAbsArrival())
				{
					
					aPerson.setState(PersonStates.ARRIVING);
					elevators.get(nextElevator).addFloorRequest((int)aPerson.getCurrentFloor());
					people.add(aPerson);
					nextPerson = people.size()-1;
					aPerson = new Person(this,aPerson.getAbsArrival());
					
					if(debug)
					{
						System.out.println("New person arriving");
						input.nextLine();
						input = new Scanner(System.in);
					}
					
					if(debug)
					{
						System.out.println("moveTime is going to be " + (moveTime - people.get(nextPerson).getNextRelevantTime()));
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}
					
					if(people.get(nextPerson).getState().compareTo(PersonStates.ARRIVING) == 0
							&&
							people.get(nextPerson).getAbsArrival() == timeStamp)
					{
						people.get(nextPerson).setState(PersonStates.WAITING);
					}
					
					moveTime -= people.get(nextPerson).getNextRelevantTime();
//					timeStamp += moveTime;
					
					double shortestNext = moveTime;
					if(debug)
					{
						if(people.size() > 1)
						{
							System.out.println("More than one person detected, finding if there is another person");
							for(Person person : people)
							{
								System.out.println("Person " + person.getName() + " nextRelevant is " + person.getNextRelevantTime());
							}							
							input.nextLine();
							input = new Scanner(System.in);
						}
					}
					
					if(people.size() > 1)
					{
						for(int j = 0; j < people.size(); j++)
						{
							if(timeStamp + shortestNext > people.get(j).getNextRelevantTime() && timeStamp < people.get(j).getNextRelevantTime())
							{
								if(debug)
								{
									System.out.println("ShortestNext is " + shortestNext);
									System.out.println("TimeStamp is " + timeStamp);
									System.out.println("Person " + people.get(j).getName() + " time is " + people.get(j).getNextRelevantTime());
									input.nextLine();
									input = new Scanner(System.in);
								}
								shortestNext = people.get(j).getNextRelevantTime() - timeStamp;
								nextPerson = j;
							}
						}
						if(people.get(nextPerson).getState().compareTo(PersonStates.ARRIVING) == 0 || people.get(nextPerson).getState().compareTo(PersonStates.WORKING) == 0)
						{
							people.get(nextPerson).setState(PersonStates.WAITING);
							elevators.get(nextElevator).addFloorRequest((int)people.get(nextPerson).getCurrentFloor());
						}
//						else if(people.get(nextPerson).getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
//						{
//							elevators.get(nextPerson).addFloorRequest((int)people.get(nextPerson).getFloor();
//						}
						moveTime = shortestNext;
					}
					timeStamp += moveTime;
					if(debug)
					{
						System.out.println("New moveTime is going to be " + moveTime);
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}
//					moveTime -= people.get(nextPerson).getNextRelevantTime();
//					timeStamp += moveTime;

					
					for(Elevator elevator : elevators)
					{
						if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
						{
							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() + moveTime);
								}
							}
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
						{
							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() - moveTime);
								}
							}
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.IDLE) == 0)
						{
							if(elevator.floorRequestSize() > 0)
							{
								if(elevator.getFloorRequest(0) > elevator.getCurrentFloorPosition())
								{
									elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
									elevator.setState(ElevatorStates.MOVING_UP);
								}
								else
								{
									elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
									elevator.setState(ElevatorStates.MOVING_DOWN);
								}
							}
						}
					}
					
					if(people.get(nextPerson).getCurrentFloor() == 0.0)
					{
						if(debug)
						{
							System.out.println("And they are coming to work");
						}
//						aPerson = new Person(this,aPerson.getAbsArrival());
					}
					
					if(debug)
					{
						input.nextLine();
						input = new Scanner(System.in);
					}

				}*/
				else
				{
					if(debug)
					{
						System.out.println("moveTime is going to be " + moveTime);
						System.out.println("Next event will be elevator " + elevators.get(shortestElevatorRequestTime).getName() + " doing something");
					}
					if(debug)
					{
						System.out.println("Theoretical move time: " + (people.get(nextPerson).getNextRelevantTime()-timeStamp));
//						input.nextLine();
//						input = new Scanner(System.in);
					}
					
					moveTime = people.get(nextPerson).getNextRelevantTime() - timeStamp;
					timeStamp += moveTime;
					
					for(Elevator elevator : elevators)
					{
						if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
						{
/*							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() + moveTime);
								}
							}*/
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
						{
/*							for(Person person : people)
							{
								if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getCurrentFloor() == elevator.getCurrentFloorPosition())
								{
									person.setCurrentFloor(person.getCurrentFloor() - moveTime);
								}
							}*/
							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
						}
					}
					
//					if(debug)
//					{
//						input.nextLine();
//						input = new Scanner(System.in);
//					}
				}
				
				for(Person person : people)
				{
					if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
					{
						if(elevators.get(nextElevator).getState().compareTo(ElevatorStates.MOVING_UP) == 0)
						{
							person.setCurrentFloor(person.getCurrentFloor()+moveTime);
							if(debug)
							{
								System.out.println("Moved person " + person.getName() + " up " + moveTime);
							}
						}
						else if(elevators.get(nextElevator).getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
						{
							person.setCurrentFloor(person.getCurrentFloor()-moveTime);
							if(debug)
							{
								System.out.println("Moved person " + person.getName() + " down " + moveTime);
							}
						}
						if(debug)
						{
							input.nextLine();
							input = new Scanner(System.in);
						}
						
					}
				}
				
				if(debug)
				{
					System.out.println("Current time stamp is " + timeStamp);
//					input.nextLine();
//					input = new Scanner(System.in);
				}
				
				
				for(Person person : people)
				{
					if(person.getState().compareTo(PersonStates.WAITING) == 0)
					{
						if(person.getCurrentFloor() == 0)
						{
							person.setWorkDelay(person.getWorkDelay() + moveTime);
							person.setAddedWaitTime(person.getWorkDelay());
						}
						else
						{
							person.setLeaveDelay(person.getLeaveDelay() + moveTime);
							person.setAddedWaitTime(person.getLeaveDelay());
						}
					}
					else if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
					{
						person.setElevatorWait(person.getElevatorWait() + moveTime);
					}
				}
				
				if(people.get(nextPerson).getCurrentFloor() == 0.0)
				{
					if(debug)
					{
						System.out.println("And they are coming to work");
					}
//					aPerson = new Person(this,aPerson.getAbsArrival());
				}
				
				if(debug)
				{
					input.nextLine();
					input = new Scanner(System.in);
				}
				
/*				for(Elevator elevator : elevators)
				{
					if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
					{
						elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
					}
				}*/
				
/*				if(isArrivalNextEvent(people,aPerson))
				{
					if(debug)
					{
						System.out.println("A new person will be arriving");
						input.nextLine();
						input = new Scanner(System.in);
					}
					aPerson.setState(PersonStates.WAITING);
					people.add(aPerson);
					aPerson = new Person(this,aPerson.getAbsArrival());
				}
				else if(debug)
				{
					System.out.println("No new person will be arriving yet");
					input.nextLine();
					input = new Scanner(System.in);
				}*/
				
			}
			else
			{
				timeStamp = aPerson.getAbsArrival();
				aPerson.setState(PersonStates.WAITING);
				elevators.get(nextElevator).addFloorRequest((int)aPerson.getCurrentFloor());
				people.add(aPerson);
				aPerson = new Person(this,aPerson.getAbsArrival());
				
				if(debug)
				{
					System.out.println("Current time stamp is " + timeStamp);
					input.nextLine();
					input = new Scanner(System.in);
				}
			}
			
			for(Person person : people)
			{
				if(person.getNextRelevantTime() < timeStamp)
				{
					person.setNextRelevantTime(timeStamp);
				}
				if(person.getState().compareTo(PersonStates.WORKING) == 0)
				{
					person.setFloor(0);
				}
			}
			
			for(Elevator elevator : elevators)
			{
				int k = 0;
				boolean isOnFloor = false;
				while(k < elevator.floorRequestSize())
				{
					if(elevator.getCurrentFloorPosition() == elevator.getFloorRequest(k))
					{
						if(debug)
						{
							System.out.println("Removing floor request " + elevator.getFloorRequest(k));
							input.nextLine();
							input = new Scanner(System.in);
						}
						elevator.removeFloorRequest(k);
						isOnFloor = true;
					}
					else
					{
						k++;
					}
				}
				if(elevator.floorRequestSize() == 0 || isOnFloor)
				{
					elevator.setState(ElevatorStates.IDLE);
				}
			}
			
			int l = 0;
			while(l < people.size())
			{
				if(people.get(l).getState().compareTo(PersonStates.LEFT) == 0)
				{
					if(debug)
					{
						System.out.println(people.get(l).getName() + " is on floor " + people.get(l).getCurrentFloor() + " and has left ");
					}
					leftPeople.add(people.get(l));
					people.remove(l);
				}
				else
				{
					l++;
				}
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
	
	public boolean isArrivalNextEvent(LinkedList<Person> people, Person aPerson)
	{
		for(Person person : people)
		{
			if(person.getNextRelevantTime() < aPerson.getNextRelevantTime() || person.getState().compareTo(PersonStates.ARRIVING) == 0)
			{
				return false;
			}
		}
		return true;
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
		return distribution(lambdaArrival,personArrival.nextDouble())*60;//*60;//Change from minutes to seconds
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
