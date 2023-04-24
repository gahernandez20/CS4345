//Modified from the source code @ Simplelearning

import java.util.concurrent.*;
import java.util.Random;

//this class is to create a shared object

class sharedCounter{
    static int counter = 0; // a shared counter variable
} 

// this class implements the concurrent threads
class updateCounter extends Thread{
    int LIMIT = 10;
    int SLEEP_TIME = 1000; //change to 100 and observe the output
    
    Random rand = new Random();
    String threadName;
    Semaphore s;

    public updateCounter(String threadName, Semaphore s){
        super(threadName);
        this.threadName = threadName;
        this.s = s;
    }

    public void run(){   
        // The incrementing thread working
        if(this.getName().equals("Inc")){
            System.out.println("Starting " + threadName + " thread...");
            try {
                System.out.println("Thread " + this.getName() + " is acquring");
                for(int i=0; i < LIMIT; i++){
                    s.acquire();
                    System.out.println("Thread " + this.getName() + " got the semaphore");

                    sharedCounter.counter++;
                    System.out.println(threadName + " current value: " + sharedCounter.counter);    
                    // Now incrementer thread will sleep so that the decrementer thread (if a context switch happen) get a chance to update
                    System.out.println("Thread " + this.getName() + " is releasing");
                    
                    s.release();
                    SLEEP_TIME = rand.nextInt(100)*10;

                    Thread.sleep(SLEEP_TIME); //change to 100, and then 1000

                }
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }         
      // The decrement thread working
       else {
            System.out.println("Starting " + threadName + " thread...");
            try { 
                System.out.println("Thread " + this.getName() + " is acquring");
                  
                for(int i=0; i < LIMIT; i++) {
                    s.acquire();
                    System.out.println("Thread " + this.getName() + " got the semaphore");
                    sharedCounter.counter--;
                    System.out.println(threadName + " current value: " + sharedCounter.counter);         
                    // Now, decrementer thread will sleep to allow a context switch, if any, for the other thread to execute
                    System.out.println("Thread " + this.getName() + " is releasing");
                    
                    s.release();
                    SLEEP_TIME = rand.nextInt(100)*10;
                    Thread.sleep(SLEEP_TIME);
                    
                }
            } catch(InterruptedException e) {
                System.out.println(e);
            }
            
        }
    }
}

// the driver class

public class Synch{

    public static void main(String args[]) throws InterruptedException{
    
        System.out.println("Shared counter value at start: " + sharedCounter.counter);    
        
        // Create semaphore object
        Semaphore s = new Semaphore(1);

        // creating two threads with name Inc and Dec that will execute concurrently to update the shared counter
        updateCounter ci = new updateCounter("Inc", s);  // Incrementer thread
        updateCounter cd = new updateCounter("Dec", s);  // Decrementer thread

        ci.start();      cd.start();        
        ci.join();       cd.join();    

        System.out.println("Shared counter value after both threads are done: " + sharedCounter.counter);
    }
}