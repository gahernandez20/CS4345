/*
 * Name: Giovanni Hernandez
 *       Fred Taft
 * 
 * Course: CS 4345, Spring 2023
 * Assignment: Project 1
 * 
 * To compile: javac project1_hernandez_taft.java
 * To run: java BridgeTrafficController
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.*;

// Shared bridge
class Bridge {
    static Car passingCar = null;
}

// Driver class; creates threads to manage both sides of bridge
class BridgeTrafficController {

    public static void main(String[] args) {
        // Semaphore used to maintain mutual exclusion
        Semaphore light = new Semaphore(1);

        // Creates two threads, one for each side of the bridge (east side and west
        // side)
        Direction eastbound = new Direction("East", light);
        Direction westbound = new Direction("West", light);

        // Starts each thread
        eastbound.start();
        westbound.start();
    }
}

class Direction extends Thread {
    private Random rand = new Random(); // Random number generator used to create random sleep times
    private int carID; // Instance variable used by each thread to create ids for new cars
    protected Semaphore s; // Instance variable used to store instance of semaphore for each thread
    protected Deque<Car> cars; // Local deque (queue) used to maintain aisle of cars on both sides of bridge;
                               // Each thread has a deque (queue)

    public Direction(String threadNameDirection, Semaphore s) {
        super(threadNameDirection); // Calls the thread class's constructor
        this.s = s; // Sets the each thread's semaphore to the passed semaphore; Same semaphore
        this.cars = new ArrayDeque<>(); // Initializes the local deque to an empty deque
    }

    protected void arrive() throws InterruptedException {
        // arrive() will call tryAcquire(), which will check to see if the semaphore is available; This method is non-blocking,
        //      meaning if the semaphore is not available, the thread will not wait for it to
        //      become available and will instead continue back to the infinite while-loop that will create additional cars
        if (s.tryAcquire()) {
            // If the semaphore is available, the current thread will acquire it and move
            //      first car in its deque onto the bridge
            Bridge.passingCar = cars.removeFirst();
            System.out.printf("Car %d has started passing on the bridge.\n", Bridge.passingCar.getID());
            Thread.sleep(Bridge.passingCar.getSpeed() * 1000); // This simulates the car taking its time passing over the bridge;
                                                               // Multiplied by 1000 to convert millisecondds to seconds
            passed(); // After car has passed, passed() is called
        }
    }

    protected void passed() {
        // passed() declares the car as finished passing the bridge and releases the
        //      semaphore to be acquired by any thread
        System.out.printf("Car %d has finished passing the bridge.\n", Bridge.passingCar.getID());
        s.release();
    }

    // Working thread code; Each thread will infinitely create cars and check 
    //      if first car in its deque can pass over the bridge
    @Override
    public void run() {
        try {
            if (this.getName().equals("East")) { // East side thread code block 
                carID = 1; // East side cars going west are identified with odd numbers
                while (true) { // Infinite loop
                    addMoreCars(); // Calls helper method that contains the working code
                }
            } else { // West side thread code block
                carID = 2; // West side cars going east are identified with even numbers
                while (true) { // Infinite loop
                    addMoreCars(); // Calls helper method that contains the working code
                }
            }
        } catch (InterruptedException ie) { // Multiple methods throw Interrupted Exception; this single catch block catches all
            ie.printStackTrace();
        }
    }

    /*
     * Helper methods used for concise, clean code
     */

    private int createRandomCarSpeed() {
        return rand.nextInt(1,20);
    }

    private int createRandomSleepTime() {
        return rand.nextInt(5, 10) * 1000; // Multiplied by 1000 to convert 5-10 milliseconds to 5-10 seconds
    }

    // Helper method that each thread calls; Creates new cars and calls arrive() after each car 'arrives' at bridge
    private void addMoreCars() throws InterruptedException {
        Car c = new Car(carID, createRandomCarSpeed());
        cars.addLast(c);
        System.out.printf("Car %d has arrived at the bridge and is awaiting passage\n", c.getID());
        Thread.sleep(createRandomSleepTime()); 
        arrive(); // Calls arrive() method that checks for bridge's availability
        carID += 2;
    }
}

// Car object class
class Car {
    private int id;
    private int speed;

    public Car(int id, int speed) {
        this.id = id;
        this.speed = speed;
    }

    public int getID() {
        return this.id;
    }

    public int getSpeed() {
        return this.speed;
    }
}