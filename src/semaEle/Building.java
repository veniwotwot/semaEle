package semaEle;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

//A second iteration of my work. Working with several bugs. Left this as a backup version.

public class Building extends Thread {

	private final int stairHaters = 49;
	Semaphore max_cap = new Semaphore(7);
	Queue<Integer> floor = new LinkedList<>();
	Semaphore mutex = new Semaphore(1);
	Semaphore[] floorReached = new Semaphore[11];
	// boolean groundFloor = false;
	Semaphore groundFloor = new Semaphore(7);

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
				// System.out.println("Joined customer " + i);
			} catch (InterruptedException ex) {

			}
		}

		// Join thread for elevator
		try {
			elevator.join();
			// System.out.println("elevator joined.");
		} catch (InterruptedException ex) {
		}

	}

	class StairHater extends Thread {
		private int number;
		private int destination;

		private StairHater(int n, int dest) {
			number = n;
			destination = dest;
		}

		public void run() {
			try {
				max_cap.acquire();
				floorReached[1].acquire();
				System.out.println("Person " + number + " enters the elevator to go to floor " + destination);

				mutex.acquire();
				floor.add(destination);
				mutex.release();

				floorReached[destination].acquire();
				System.out.println("Person " + number + " leaves the elevator");

				floorReached[destination].release(); // unsure if needed

				groundFloor.acquire(); // wait to be on ground floor before releasing cap
				max_cap.release();
				groundFloor.release();

			} catch (InterruptedException e) {

			}
		}
	}

	class Elevator extends Thread {

		Elevator() {
		}

		public void openDoor(int i) {
			System.out.println("The elevator door opens at floor " + i);
		}

		public void closeDoor() {
			System.out.println("The elevator door closes");
		}

		public void run() {
			int cycles = 1;
			boolean running = true;
			while (running) {

				try {
					openDoor(1);
					floorReached[1].release(7);
					Thread.sleep(1500); // waiting for people to enter, TBD
					closeDoor();

					int exitingPeople; //counter for people wanting to exit
					for (int i = 2; i < 11; i++) {
						exitingPeople = 0;
						for (int j : floor) {
							if (i == j) { // then someone wants to exit on floor i
								floorReached[i].release();
								openDoor(i);
								Thread.sleep(1000); // waiting for people to leave, TBD
								closeDoor();
								i++;
							}
						}
					}
					
					// for some reason, there was a bug causing leftover permits.
					// therefore, we drain permits to leave before returning to floor 1
					for (int i = 2; i < 11; i++) {
						floorReached[i].drainPermits();
					}

					// empty the floor queue
					while (floor.isEmpty() == false)
						floor.remove();

					groundFloor.release(7);

					// hard code the limit of cycles to 7
					if (cycles == 7)
						running = false;
					cycles++;

				} catch (InterruptedException e) {

				}

				/*
				 * openDoor(i); signal(groundOpen); wait(eleFull); closeDoor(); for (i = 2; i <
				 * 10; i++) { openDoor(i); signal(floor[i]); // guesswork wait(allExit); //
				 * everyone should exit the elevator before closing doors closeDoor();
				 */
			}
			// i = 1;
		}
	}

	public static void main(String[] args) {
		Building b = new Building();
		b.simulate();

		// Once simulation finishes
		System.out.println("Simulation done");
	}

}
