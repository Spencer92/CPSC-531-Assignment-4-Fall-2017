package methods;

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
	public int nextFloor() 
	{
		return floorRequests.getFirst();
	}

}
