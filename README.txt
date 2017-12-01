This was compiled using eclipse, it should be able to be
compiled using any other method of compiling java.

In order to run the program, there's two ways

If nothing is submitted, it will run through a predetermined
set of values.

To set your own values, the method to submit them is

idlePolicy movementPolicy time lambda

where 	idlePolicy is a boolean (true - idles at first floor, false - idles at current position)
	movementPolicy is an integer (0 - FCFS, 1 - Linear Scan)
	time is a double
	lambda is a double


Technically, every person entity, if waiting, requests a floor every loop. I didn't
see this as a problem as the elevator removes all requests once it reaches a floor
anyway

More than one elevator is not implemented.