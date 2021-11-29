package semaEle;

import java.util.concurrent.Semaphore;

//A failed iteration one of my work, mostly brainstorming

/*
public class semaEle {
	//private final int maxCap = 7;
	boolean groundOpen;
	boolean doorOpen;
	Semaphore eleFull;

	public static void main(String[] args) {
		Semaphore eleFull = new Semaphore(7,true);	//max size 7, order matters
		
		initThreads();
	}
	
	void initThreads() {
		Thread person1 = new Thread("1");
	}

	void openDoor(int floor) {
		System.out.println("Elevator door opens at floor " + floor);
		doorOpen = true;
	}

	void closeDoor() {
		System.out.println("Elevator door closes");
		doorOpen = false;
	}

	void enterEle(int floor) {
		System.out
				.println("Person " + Thread.currentThread().getName() + " enters the elevator to go to floor " + floor);
	}

	void leaveEle() {
		System.out.println("Person " + Thread.currentThread().getName() + " leaves elevator");
	}

}

class Elevator extends semaEle {
	// This is the code for the elevator.
	int i = 1;
	while(true)
	{
		openDoor(i);
		signal(groundOpen);
		wait(eleFull);
		closeDoor();
		for (i = 2; i < 10; i++) {
			openDoor(i);
			signal(floor[i]);		// guesswork
			wait(allExit); 			// everyone should exit the elevator before closing doors
			closeDoor();
		}
		i = 1;
	}
}

class StairHater extends semaEle {
	 This is the code for people who use the elevator.
	 Logically, they are "stairhaters" because they refuse to use the stairs
	 and would rather wait for six batches of other people to use the elevator
	 first, and simply wait in the lobby even if they only wanted to go to floor
	 2. 
	int dest = (int) (Math.random() * 9 + 1); 
	wait(groundOpen);
	signal(eleFull); 	// should only add one
	enterEle(dest);
	wait(floor[i]);
	signal(allExit); 	// should only increment 1
	leaveEle();	
	*/
//}

