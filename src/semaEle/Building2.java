package semaEle;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

//Final version. Author: Vincent Wong, VYW180000, CS4348.001

public class Building2 extends Thread {

	private final int stairHaters = 49;

	Semaphore[] floorReached = new Semaphore[11];
	Semaphore max_cap = new Semaphore(7);
	Semaphore mutex = new Semaphore(1);
	Semaphore exited = new Semaphore(0);
	Semaphore entered = new Semaphore(0);

	Queue<Integer> floor = new LinkedList<>();

	public void simulate() {
		int i;

		// initialize all arrays of semaphores
		for (i = 0; i < 11; i++) {
			floorReached[i] = new Semaphore(0);
		}

		// Create threads for the people and elevator
		Thread[] people = new Thread[stairHaters];
		Thread elevator = new Thread();

		// Create and start threads for people
		for (i = 0; i < stairHaters; i++) {
			int dest = (int) (Math.random() * 9 + 2);
			people[i] = new StairHater(i, dest);
			people[i].start();
		}

		// Create and start thread for elevator
		elevator = new Elevator();
		elevator.start();

		// Join thread for people
		for (i = 0; i < stairHaters; i++) {
			try {
				people[i].join();
			} catch (InterruptedException ex) {
			}
		}

		// Join thread for elevator
		try {
			elevator.join();
		} catch (InterruptedException ex) {
		}

	}

	/* This is the code for people who use the elevator.
	 Logically, they are "stairhaters" because they refuse to use the stairs
	 and would rather wait for six batches of other people to use the elevator
	 first, and simply wait in the lobby even if they only wanted to go to floor
	 2. */
	class StairHater extends Thread {
		private int number;
		private int destination;

		// Basic constructor
		private StairHater(int n, int dest) {
			number = n;
			destination = dest;
		}

		public void run() {
			try {
				// Acquire max_cap and floorReached[1] to ensure that the elevator is on the
				// ground floor
				// and no more than 7 people can enter at a time.
				max_cap.acquire();
				floorReached[1].acquire();
				System.out.println("Person " + number + " enters the elevator to go to floor " + destination);
				// Release entered to tell the elevator that 1 person has entered
				entered.release();

				// Use mutex to add to destination to queue without writing faults
				mutex.acquire();
				floor.add(destination);
				mutex.release();

				// acquire floorReached[destination] when the elevator reaches the destination
				// floor
				floorReached[destination].acquire();
				System.out.println("Person " + number + " leaves the elevator");
				// Release exited to tell the elevator that 1 person has exited and other people
				// that one person has exited
				exited.release();
				max_cap.release();

			} catch (InterruptedException e) {

			}
		}
	}

	/* This is the code for the elevator thread. Naturally, it is simply an elevator and there
	 * is only one of them, so there does not need to be any special or funny name for it.
	 */
	class Elevator extends Thread {

		Elevator() {
		}

		public void openDoor(int i) {
			System.out.println("The elevator door opens at floor " + i);
		}

		public void closeDoor() {
			System.out.println("The elevator door closes");
		}

		public void errPrintQueue() {
			System.err.print("The queue contains: ");
			for (int s : floor) {
				System.err.print(s);
			}
			System.err.println();
		}

		public void errPrintVals(int i, int j) {
			// System.err.println("i is: " + i + ". j is: " + j);
		}

		public void run() {
			int cycles = 1;
			boolean running = true;
			while (running) {

				try {
					openDoor(1);
					
					//Announce that elevator reached ground floor, wait for 7 people to enter.
					floorReached[1].release(7);
					entered.acquire(7);

					closeDoor();

					int exitingPeople; 					// counter for people wanting to exit
					for (int i = 2; i < 11; i++) {
						exitingPeople = 0;
						for (int j : floor) {
							if (i == j) { 				// then someone wants to exit on floor i
								errPrintVals(i, j);
								exitingPeople++;		// increment count when queue.val == floor
							}
						}
						// if someone wants to exit on this floor...
						if (exitingPeople > 0) {
							System.err.println("Exiting people: " + exitingPeople + " for floor " + i);
							openDoor(i);
							for (int k = 0; k < exitingPeople; k++) {
								// release one permit for everyone wanting to exit
								floorReached[i].release();
							}
							// wait for everyone wanting to exit, to exit
							exited.acquire(exitingPeople);
							closeDoor();
						}
					}

					// empty the floor queue
					while (floor.isEmpty() == false)
						floor.remove();

					// hard code the limit of cycles to 7
					if (cycles == 7)
						running = false;
					cycles++;

				} catch (InterruptedException e) {

				}

			}
		}
	}

	public static void main(String[] args) {
		Building2 b = new Building2();
		b.simulate();

		// Once simulation finishes
		System.out.println("Simulation done");
	}

}
