package simulation;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import entities.*;
import methods.FirstComeFirstServe;
import methods.LinearScan;
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
	private int floors = 3;
	private double lambdaArrival = 2.0;// rate change from sec to minutes
	private double meanWorkRate = 300;//3600;//3600;
	private double floorChangeRate = 10;
	private double timeStamp;
	private int numElevators;
	public static boolean debug = false;
	private int personNumber;
	private int elevatorNumber;
	private int next;
	private int eventAfterNext;
	private double minute = 60.0;
	private Random elevatorDecision;
	private Method method;
	
	public Elevators(String [] args)
	{
		boolean oneTest;
		boolean idling = false;
		int policy;
		boolean policyTruth = false;
		double time = 0;
		double lambda = 0;
		
		if(args.length > 0)
		{
			oneTest = true;
			idling = Boolean.parseBoolean(args[0]);
			idling = !idling;
			if(args[1].equals("0"))
			{
				policyTruth = true;
			}
			else
			{
				policyTruth = false;
			}
			time = Double.parseDouble(args[2]);
			lambda = Double.parseDouble(args[3]);
		}
		else
		{
			oneTest = false;
		}
		oneTest = true;
		if(oneTest)
		{
			personArrival = new Random(SEED_PERSON_ARRIVAL);
			personWork = new Random(SEED_PERSON_WORK);
			personFloor = new Random(SEED_PERSON_FLOOR);
			indecisive = new Random(SEED_INDECISIVE);
			elevatorDecision = new Random(SEED_ELEVATOR_DECISION);
		}
		timeStamp = 0;
		numElevators = 2;
		personNumber = 0;
		elevatorNumber = 0;
//		Method method = new FirstComeFirstServe();
		boolean [] isFirstCome = {true,false};
		boolean [] idle = {true,false};
		double [] lambdas = {0.5,2};
		double [] times = {3600,7200,74000/*2.5 days @ 8 hour days*/,144000/*5 days @ 8 hour days*/};
		
		System.out.println(lambdaArrival);

		
		start(true,600,0.5,false);
		
/*		
		if(!oneTest)
		{
			for(int l = 0; l < idle.length; l++)
			{
				for(int i = 0; i < isFirstCome.length; i++)
				{
					for(int j = 0; j < times.length; j++)
					{
						for(int k = 0; k < lambdas.length; k++)
						{
							personArrival = new Random(SEED_PERSON_ARRIVAL);
							personWork = new Random(SEED_PERSON_WORK);
							personFloor = new Random(SEED_PERSON_FLOOR);
							indecisive = new Random(SEED_INDECISIVE);
							elevatorDecision = new Random(SEED_ELEVATOR_DECISION);
							lambdaArrival = lambdas[k];
							start(isFirstCome[i],times[j],lambdas[k], idle[l]);
							resetElevatorNames();
							resetPersonNames();
						}
					}
				}
			}
		}
		else
		{
			lambdaArrival = lambda;
			start(policyTruth,time,lambda,idling);
		}*/

	}
	
	
	public void start(boolean isFirstCome,double times,double lambdas,boolean idle)
	{
		LinkedList<Elevator> elevators = new LinkedList<Elevator>();
		LinkedList<Person> people = new LinkedList<Person>();
		LinkedList<Person> leftPeople = new LinkedList<Person>();
		int nextElevator = -1;
		int nextPerson;
		Person aPerson = new Person(this,0);
		Scanner input = new Scanner(System.in);
//		int shortestElevatorRequestTime = 0;
		double moveTime;
		
		if(isFirstCome)
		{
			method = new FirstComeFirstServe();
		}
		else
		{
			method = new LinearScan();
		}
		
		
		for(int i = 0; i < numElevators; i++)
		{
			elevators.add(new Elevator(this));
		}
		
		
		while(timeStamp < times)
		{
			if(timeStamp > 179)
			{
				debug = true;
			}
			
			nextElevator = nextElevator(elevators,people);

			
			if(debug)
			{
				System.out.println("The next elevator will be: " + nextElevator);
				
				System.out.println("THe next person arrival will be " + aPerson.getName());
				System.out.println("and will arrive at: " + aPerson.getAbsArrival());
				
				System.out.println("Elevator states:");
				
				for(Elevator elevator : elevators)
				{
					System.out.println("Elevator " + elevator.getName() + ": " + elevator.getState() + ", pos: " + elevator.getCurrentFloorPosition());
				}
				
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
						System.out.print("Person " + person.getName() + " state: " + person.getState());
						if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
						{
							System.out.println(" " + person.getElevatorIn());
						}
						else
						{
							System.out.println();
						}
						System.out.println("Next relevant time: " + person.getNextRelevantTime());
						System.out.println("Next floor wanted: " + person.getFloor());
						System.out.println("Floor currently on: " + person.getCurrentFloor());
						System.out.println("Work time amount: " + person.getWorkTime());
					}
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				
				setPeopleStates(people,elevators,nextElevator);
				
				
				//Find the next response time
//				shortestElevatorRequestTime = shortestElevatorToFloor(elevators);
				
/*				if(nextElevator == -1)//shortestElevatorRequestTime == -1)
				{
					for(int j = 0; j < elevators.size(); j++)
					{
						if(elevators.get(j).getState().compareTo(ElevatorStates.IDLE) == 0)
						{
							nextshortestElevatorRequestTime = j;
							break;
						}
					}
				}*/
				
				if(elevators.get(nextElevator).getCurrentFloorPosition() < 0)
				{
					try {
						throw new Exception("Out of bounds");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				moveTime = 0;
				
				if(debug)
				{
					System.out.println("Request Size for " + elevators.get(nextElevator).getName()
							+ ": " + elevators.get(nextElevator).floorRequestSize());
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				//Find how much movement is needed
				
				if(elevators.get(nextElevator).floorRequestSize() > 0)//(shortestElevatorRequestTime).floorRequestSize() > 0)
				{
					moveTime = findNextEvent(elevators,nextElevator);//shortestElevatorRequestTime);
				}
				else
				{
					moveTime = 0;
				}
				if(moveTime < 0.0)
				{
					moveTime *= -1;
				}
				
				//Check the next arrival to see if they are coming in
				
				if(isArrivalNextEvent(people,aPerson))
				{
					if(debug)
					{
						System.out.println("A new person will be arriving");
						System.out.println("TimeStamp is " + timeStamp);
						input.nextLine();
						input = new Scanner(System.in);
					}
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
						System.out.println(elevators.get(nextElevator).getFloorRequest(elevators.get(nextElevator).getNextFloor()));						
					}
					catch (IndexOutOfBoundsException e)
					{
						System.out.println("Nothing yet");
					}
					System.out.println("The next personTime will be " + people.get(nextPerson).getNextRelevantTime() + " for person " + 
					people.get(nextPerson).getName());
				}


				//If skipping to the next elevator request moves
				//Over a person doing something, then
				//that time has to be compensated for
				
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
						people.get(nextPerson).setElevatorIn(-1);
					}
					
					moveTime -= people.get(nextPerson).getNextRelevantTime();
					
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
						moveTime = newMoveTime(people,elevators,shortestNext,nextPerson,nextElevator);
					}
					timeStamp += moveTime;
					if(debug)
					{
						System.out.println("New moveTime is going to be " + moveTime);
						System.out.println("Next event will be person " + people.get(nextPerson).getName() + " doing something");
					}

					moveElevators(elevators, moveTime);
					
					if(debug)
					{
						input.nextLine();
						input = new Scanner(System.in);
					}
				}//(moveTime > people.get(nextPerson).getNextRelevantTime() || moveTime > aPerson.getAbsArrival())
				
				else
				{
					if(debug)
					{
						System.out.println("moveTime is going to be " + moveTime);
						System.out.println("Next event will be elevator " + 
								elevators.get(nextElevator).getName() + " doing something");
//						elevators.get(shortestElevatorRequestTime).getName() + " doing something");
					}
					if(debug)
					{
						System.out.println("Theoretical move time: " + (people.get(nextPerson).getNextRelevantTime()-timeStamp));

					}
					
					moveTime = people.get(nextPerson).getNextRelevantTime() - timeStamp;
					timeStamp += moveTime;
					
					for(Elevator elevator : elevators)
					{
						if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
						{

							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
						}
						else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
						{

							elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
						}
					}
					
				}//else (moveTime > people.get(nextPerson).getNextRelevantTime() || moveTime > aPerson.getAbsArrival())
				
				
				
				movePeopleInElevators(people,elevators,nextElevator,moveTime);
				
				
				if(debug)
				{
					System.out.println("Current time stamp is " + timeStamp);
				}
				
				
				//Get the delay for processing
				
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

				
				if(debug)
				{
					input.nextLine();
					input = new Scanner(System.in);
				}
				
				
			}
			else//	if(people.size() > 0)
			{
				
				//There isn't anybody inside of the building
				timeStamp = aPerson.getAbsArrival();
				aPerson.setState(PersonStates.WAITING);
				
				if(debug)
				{
					int i;
					i = 0;
				}
				
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
					person.setElevatorIn(-1);
				}
			}
			
			for(Elevator elevator : elevators)
			{
				int k = 0;
				boolean isOnFloor = false;
				int aFloorRequest = -1;
				while(k < elevator.floorRequestSize())
				{
					if(elevator.getCurrentFloorPosition() == elevator.getFloorRequest(k))
					{
						
						aFloorRequest = elevator.getFloorRequest(k);
						if(debug)
						{
							System.out.println("Removing floor request " + elevator.getFloorRequest(k) + " from elevator " + elevator.getName());
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
					if(aFloorRequest != -1)
					{
						k = 0;
						for(Elevator restOfElevators : elevators)
						{
							while(k < restOfElevators.floorRequestSize())
							{
								if(restOfElevators.getFloorRequest(k) == aFloorRequest)
								{
									restOfElevators.removeFloorRequest(k);
								}
								else
								{
									k++;
								}
							}
						}
						aFloorRequest = -1;
					}
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
			
			if(idle)
			{
				for(Elevator elevator : elevators)
				{
					if(elevator.getState().compareTo(ElevatorStates.IDLE) == 0 && elevator.getCurrentFloorPosition() != 0.0)
					{
						elevator.addFloorRequest(0);
					}
				}
			}
			
			
			if(debug)
			{
				System.out.println("At timestamp " + timeStamp);
			}
		}
		
		
		averages(leftPeople,isFirstCome,lambdas,times,idle);
		timeStamp = 0.0;
		
		
	}
	
	
	/**
	 * 
	 * @param people the people who left the building
	 * @param isFirstCome checks what policy
	 * @param lambdas how many people are moving
	 * @param times how long the experiment went
	 * @param idle different idling policies
	 */
	
	public void averages(LinkedList<Person> people, boolean isFirstCome, double lambdas, double times, boolean idle)
	{
		double totalWorkDelay = 0;
		double totalLeaveDelay = 0;
		double [] adjustedTimesWorkDelay = new double[people.size()];
		double [] adjustedTimesLeaveDelay = new double[people.size()];
		double standardDeviationWorkDelay;
		double standardDeviationLeaveDelay;
		double adjustedWorkDelayTotal = 0;
		double adjustedLeaveDelayTotal = 0;
		double meanWorkDelay;
		double meanLeaveDelay;
		double [] workDelayTimes = new double[people.size()];
		double [] leaveDelayTimes = new double[people.size()];
		
		System.out.print("Stats for lambda at " + lambdas + ", times at " + times + ", ");
		if(isFirstCome)
		{
			System.out.print(" using FCFS, ");
		}
		else
		{
			System.out.print(" using linear scan, ");
		}
		if(idle)
		{
			System.out.println("and first floor policy");
		}
		else
		{
			System.out.println("and idling policy");
		}
		
		
		for(int i = 0; i < people.size(); i++)
		{
			workDelayTimes[i] = people.get(i).getWorkDelay();
			leaveDelayTimes[i] = people.get(i).getLeaveDelay();
		}
		
		//Gets the mean
		for(int i = 0; i < workDelayTimes.length && i < leaveDelayTimes.length; i++)
		{
			totalWorkDelay += workDelayTimes[i];
			totalLeaveDelay += leaveDelayTimes[i];
		}
		
		meanWorkDelay = totalWorkDelay/workDelayTimes.length;
		meanLeaveDelay = totalLeaveDelay/leaveDelayTimes.length;
		
		
		//Gets the standard deviation
		for(int i = 0; i < adjustedTimesWorkDelay.length && i < leaveDelayTimes.length; i++)
		{
			adjustedTimesWorkDelay[i] = workDelayTimes[i]-meanWorkDelay;
			adjustedTimesWorkDelay[i] *= adjustedTimesWorkDelay[i];
			adjustedWorkDelayTotal += adjustedTimesWorkDelay[i];
			adjustedTimesLeaveDelay[i] = leaveDelayTimes[i]-meanLeaveDelay;
			adjustedTimesLeaveDelay[i] *= adjustedTimesLeaveDelay[i];
			adjustedLeaveDelayTotal += adjustedTimesLeaveDelay[i];
			
		}
		
		standardDeviationWorkDelay = adjustedWorkDelayTotal/adjustedTimesWorkDelay.length;
		standardDeviationWorkDelay = Math.sqrt(standardDeviationWorkDelay);
		
		standardDeviationLeaveDelay = adjustedLeaveDelayTotal/adjustedTimesLeaveDelay.length;
		standardDeviationLeaveDelay = Math.sqrt(standardDeviationLeaveDelay);
		
		standardDeviationLeaveDelay = round(standardDeviationLeaveDelay);
		standardDeviationWorkDelay = round(standardDeviationWorkDelay);
		
		meanWorkDelay = round(meanWorkDelay);
		meanLeaveDelay = round(meanLeaveDelay);
		
		System.out.println("mean delay for work: " + meanWorkDelay);
		System.out.println("Standard deviation for work: " + standardDeviationWorkDelay  + "\n");


		System.out.println("mean delay for leaving: " + meanLeaveDelay);
		System.out.println("Standard deviation for leaving: " + standardDeviationLeaveDelay  + "\n");
		
		
		
	}
	
	
	/**
	 * Used to deal with floating point errors
	 * @param number
	 * @return a rounded number to the nearest thousandth
	 */
	private double round(double number)
	{
		number *= 1000;
		number = Math.round(number);
		number = Math.floor(number);
		number /= 1000;
		return number;
	}
	
	
	/**
	 * 
	 * @param elevators
	 * @return the next elevator being used
	 */
	private int nextElevator(LinkedList<Elevator> elevators, LinkedList<Person> people)
	{
		int nextPerson = 0;
		int nextElevator = 0;
		
		while(nextPerson < people.size() && people.get(nextPerson).getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
		{
			nextPerson++;
		}
		
		if(nextPerson < people.size())
		{
			for(int i = nextPerson; i < people.size()-1; i++)
			{
				if(people.get(nextPerson).getNextRelevantTime() > people.get(i).getNextRelevantTime() 
						&& people.get(i).getState().compareTo(PersonStates.IN_ELEVATOR) != 0)
				{
					nextPerson = i;
				}
			}
			
			while(nextElevator < elevators.size() && elevators.get(nextElevator).getState().compareTo(ElevatorStates.IDLE) != 0)
			{
				nextElevator++;
			}
			
			if(nextElevator < elevators.size())
			{
				for(int i = nextElevator; i < elevators.size(); i++)
				{
					if(debug)
					{
						System.out.println("Movement of elevator times: ");
						System.out.println(nextElevator + ": " + Math.abs(elevators.get(nextElevator).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor()));
						System.out.println(i + ": " + Math.abs(elevators.get(i).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor()));
						Scanner input = new Scanner(System.in);
						input.nextLine();
					}
					if(Math.abs(elevators.get(nextElevator).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor())
							>
					Math.abs(elevators.get(i).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor())
					&&
					elevators.get(i).getState().compareTo(ElevatorStates.IDLE) == 0)
					{
						nextElevator = i;
					}
				}
			}
			else
			{
				nextElevator = -1;
			}

		}
		else
		{
			while(nextElevator < elevators.size() && elevators.get(nextElevator).getState().compareTo(ElevatorStates.IDLE) != 0)
			{
				nextElevator++;
			}
			
			if(nextElevator < elevators.size())
			{
				for(int i = nextElevator; i < elevators.size(); i++)
				{
					if(elevators.get(nextElevator).getCurrentFloorPosition() 
							> elevators.get(i).getCurrentFloorPosition()
					&&
					elevators.get(i).getState().compareTo(ElevatorStates.IDLE) == 0)
					{
						nextElevator = i;
					}
				}
			}
			else
			{
				nextElevator = -1;
			}
		}
		
		if(debug)
		{
			System.out.println("Next elevator is " + nextElevator);
		}
		
		if(nextElevator == -1)
		{
			nextElevator = elevatorDecision.nextInt(elevators.size());
			if(debug)
			{
				System.out.println("All elevators busy");
			}
		}
		
/*		while(nextElevator < elevators.size() && elevators.get(nextElevator).getState().compareTo(ElevatorStates.IDLE) != 0)
		{
			nextElevator++;
		}
		
		if(nextElevator < elevators.size())
		{
			for(int i = nextElevator; i < elevators.size(); i++)
			{
				if(Math.abs(elevators.get(nextElevator).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor())
						>
				Math.abs(elevators.get(i).getCurrentFloorPosition() - people.get(nextPerson).getCurrentFloor())
				&&
				elevators.get(i).getState().compareTo(ElevatorStates.IDLE) == 0)
				{
					nextElevator = i;
				}
			}
		}*/
/*		if(nextElevator == -1)
		{
			nextElevator = elevatorDecision.nextInt(elevators.size());
			if(debug)
			{
				System.out.println("All elevators busy");
			}
		}*/
		return nextElevator;
		
	}
	
	/**
	 * 
	 * Checks if the people are arriving, and sets them to waiting if they are
	 * if the elevator is on the same floor they requested, and they aren't
	 * inside the elevator, then they go inside the elevator
	 * and vice versa
	 * 
	 * @param people the people in the building
	 * @param elevators the elevators in the building
	 * @param nextElevator the elevator the people might be in
	 */
	
	private void setPeopleStates(LinkedList<Person> people, LinkedList<Elevator> elevators, int nextElevator)
	{
		for(Person person : people)
		{
			if(person.getState().compareTo(PersonStates.ARRIVING) == 0 && person.getAbsArrival() == timeStamp)
			{
				if(noFloorRequests(elevators,person))
				{
					if(debug)
					{
						int i;
						i = 0;
					}
					
					elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
				}
				person.setState(PersonStates.WAITING);
				person.setElevatorIn(-1);
			}
			else if(person.getState().compareTo(PersonStates.WAITING) == 0 && elevators.get(nextElevator).getCurrentFloorPosition() == person.getCurrentFloor())
			{
				if(person.getCurrentFloor() != 0)
				{
					person.setFloor(0);
				}
				if(noFloorRequests(elevators,person))
				{
					if(debug)
					{
						int i;
						i = 0;
					}
					elevators.get(nextElevator).addFloorRequest((int)person.getFloor());
				}
				person.setState(PersonStates.IN_ELEVATOR);
				person.setElevatorIn(elevators.get(nextElevator).getName());
			}
			else if(person.getState().compareTo(PersonStates.WORKING) == 0 && person.getNextRelevantTime() == timeStamp)
			{
				person.setState(PersonStates.WAITING);
				person.setFloor(0);
				person.setElevatorIn(-1);
				if(noFloorRequests(elevators,person))
				{
					if(debug)
					{
						int i;
						i = 0;
					}
					elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
				}
			}
			else if(person.getState().compareTo(PersonStates.WAITING) == 0 && noFloorRequests(elevators,person))
			{
				if(debug)
				{
					int i;
					i = 0;
				}
				elevators.get(nextElevator).addFloorRequest((int)person.getCurrentFloor());
			}
			else if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0)
			{
				if(person.getElevatorIn() == elevators.get(nextElevator).getName())
				{
					elevators.get(nextElevator).addFloorRequest(person.getFloor());
				}
			}
		}
		
		for(Elevator elevator : elevators)
		{
			for(Person person : people)
			{
				if(person.getElevatorIn() == elevator.getName())
				{
					if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getFloor() == elevator.getCurrentFloorPosition())
					{
						if(elevator.getCurrentFloorPosition() == 0)
						{
							person.setState(PersonStates.LEFT);
							person.setLeaveDelay(person.getLeaveDelay()+person.getElevatorWait());
						}
						else
						{
							person.setState(PersonStates.WORKING);
							person.setFloor(0);
							person.setNextRelevantTime(person.getAbsArrival()+person.getAddedWaitTime()+person.getElevatorWait()+person.getWorkTime());
							person.setWorkDelay(person.getWorkDelay()+person.getElevatorWait());
							person.setElevatorWait(0);
							person.setElevatorIn(-1);;
						}
					}
				}
			}
		}
	}
	
	
	private boolean noFloorRequests(LinkedList<Elevator> elevators, Person person)
	{
		for(Elevator elevator : elevators)
		{
			for(int i = 0; i < elevator.floorRequestSize(); i++)
			{
				if(elevator.getFloorRequest(i) == person.getCurrentFloor() || elevator.getFloorRequest(i) == person.getFloor())
				{
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param elevators the elevators inside of the building
	 * @param shortestElevatorRequestTime the next floor the elevator can go on
	 * @return
	 */
	
	private double findNextEvent(LinkedList<Elevator> elevators, int shortestElevatorRequestTime)
	{
		int nextFloorRequest = elevators.get(shortestElevatorRequestTime).getNextFloorRequest();
		double aDouble;
		if(debug)
		{
			if(elevators.get(shortestElevatorRequestTime).getCurrentFloorPosition() < 0)
			{
				try {
					throw new Exception("Less than 0");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Next floor request is " + nextFloorRequest);
			System.out.println(elevators.get(shortestElevatorRequestTime).getFloorRequest(elevators.get(shortestElevatorRequestTime).getNextFloorRequest()));
			System.out.println(elevators.get(shortestElevatorRequestTime).getCurrentFloorPosition());
			
		}
		aDouble = method.nextFloor(elevators.get(shortestElevatorRequestTime));
		if(debug)
		{
			Scanner input = new Scanner(System.in);
			System.out.println("aDouble is " + aDouble);
			input.nextLine();
		}
		aDouble += timeStamp;
		return aDouble;
	}
	
	/**
	 * Moves the people inside of the elevators
	 * 
	 * @param people the people inside of the building
	 * @param elevators the elevators inside of the building
	 * @param nextElevator the elevator the people are in
	 * @param moveTime the movement of time
	 */
	
	public void movePeopleInElevators(LinkedList<Person> people, LinkedList<Elevator> elevators, int nextElevator, double moveTime)
	{
		for(Elevator elevator : elevators)
		{
			for(Person person : people)
			{
				if(person.getState().compareTo(PersonStates.IN_ELEVATOR) == 0 && person.getElevatorIn() == elevator.getName())
				{
					if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
					{
						person.setCurrentFloor(person.getCurrentFloor()+moveTime);
						if(debug)
						{
							System.out.println("Moved person " + person.getName() + " up " + moveTime);
						}
					}
					else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
					{
						person.setCurrentFloor(person.getCurrentFloor()-moveTime);
						if(debug)
						{
							System.out.println("Moved person " + person.getName() + " down " + moveTime);
						}
					}
					if(debug)
					{
						Scanner input = new Scanner(System.in);
						input.nextLine();
					}
					
				}
		}
		}
	}
	
	/**
	 * Moves the elevators a certain length
	 * 
	 * @param elevators the elevators inside of the building
	 * @param moveTime the movement of time
	 */
	
	public void moveElevators(LinkedList<Elevator> elevators, double moveTime)
	{
		for(Elevator elevator : elevators)
		{
			if(elevator.getState().compareTo(ElevatorStates.MOVING_UP) == 0)
			{
				elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()+moveTime);
			}
			else if(elevator.getState().compareTo(ElevatorStates.MOVING_DOWN) == 0)
			{

				elevator.setCurrentFloorPosition(elevator.getCurrentFloorPosition()-moveTime);
			}
			else if(elevator.getState().compareTo(ElevatorStates.IDLE) == 0)
			{
				if(elevator.floorRequestSize() > 0)
				{
					if(elevator.getFloorRequest(elevator.getNextFloor()) > elevator.getCurrentFloorPosition())
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
	}
	
	
	/**
	 * 
	 * @param people The people inside of the building
	 * @param elevators the elevators
	 * @param shortestNext the next time something will change states
	 * @param nextPerson the next person whose state will change
	 * @param nextElevator the next elevator whose state will change
	 * @return the new time that will move
	 */
	public double newMoveTime(LinkedList<Person> people, LinkedList<Elevator> elevators,double shortestNext, int nextPerson, int nextElevator)
	{
		for(int j = 0; j < people.size(); j++)
		{
			if(timeStamp + shortestNext > people.get(j).getNextRelevantTime() && timeStamp < people.get(j).getNextRelevantTime())
			{
				if(debug)
				{
					Scanner input = new Scanner(System.in);
					System.out.println("ShortestNext is " + shortestNext);
					System.out.println("TimeStamp is " + timeStamp);
					System.out.println("Person " + people.get(j).getName() + " time is " + people.get(j).getNextRelevantTime());
					input.nextLine();
				}
				shortestNext = people.get(j).getNextRelevantTime() - timeStamp;
				nextPerson = j;
			}
		}
		if(people.get(nextPerson).getState().compareTo(PersonStates.ARRIVING) == 0 
				|| people.get(nextPerson).getState().compareTo(PersonStates.WORKING) == 0)
		{
			people.get(nextPerson).setState(PersonStates.WAITING);
			people.get(nextPerson).setElevatorIn(-1);
			if(noFloorRequests(elevators,people.get(nextPerson)))
			{
				if(debug)
				{
					int i;
					i = 0;
				}
				elevators.get(nextElevator).addFloorRequest((int)people.get(nextPerson).getCurrentFloor());
			}
		}
		return shortestNext;
	}
	
	
	/**
	 * 
	 * @param elevators the elevators in the building
	 * @return the next elevator that will complete a floor transaction next
	 * or -1 if all elevators are idle
	 */
/*	
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
				
				if(Math.abs(activeElevators.get(shortest).getCurrentFloorPosition()-method.nextFloor(activeElevators.get(shortest))) > 
				Math.abs(activeElevators.get(i).getCurrentFloorPosition()-method.nextFloor(activeElevators.get(i))))
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
		
	}*/
	
	/**
	 * 
	 * @param people the list of people inside of the building
	 * @return the next time that a person will change states
	 */
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
	
	
	/**
	 * 
	 * @param people the list of people currently in the building
	 * @param aPerson a person who may or may not be inside the building
	 * @return true if the person is arriving, false if not
	 */
	public boolean isArrivalNextEvent(LinkedList<Person> people, Person aPerson)
	{
		for(Person person : people)
		{
			if(person.getState().compareTo(PersonStates.LEFT) != 0 && (person.getNextRelevantTime() < aPerson.getNextRelevantTime() 
					|| person.getState().compareTo(PersonStates.ARRIVING) == 0))
			{
				return false;
			}
		}
		return true;
	}
	
	private void resetElevatorNames()
	{
		this.elevatorNumber = 0;
	}
	
	private void resetPersonNames()
	{
		this.personNumber = 0;
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
