/*
Name: Giovanni Hernandez
Name: Fred Taft
*/

import java.util.concurrent.*;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;

class SynchedBridge {

    public static void main(String[] args) {
        Thread eastBoundThread = new Thread(new Eastbound());
        // Thread westBoundThread = new Thread(new Westbound());
        // eastbound.createRandomCars();
        eastBoundThread.start();
    }

    static class Eastbound implements Runnable {
        protected Deque<Integer> eastboundCars;

        public Eastbound() {
            eastboundCars = new ArrayDeque<>();
        }

        public void createRandomCars() throws InterruptedException{
            Random rand = new Random();
            while(true){
                int car = rand.nextInt(1, 10000);
                while (car % 2 == 0) {
                    car = rand.nextInt(1, 10000);
                }
                eastboundCars.addLast(car);
                Thread.sleep(2000);
                System.out.printf("Car %d is passing.\n", car);
            }            
        }

        public Deque<Integer> getCars() {
            return eastboundCars;
        }
        
        @Override
        public void run() {
            try {
                createRandomCars();
            }
            catch(InterruptedException ie) {
                System.out.println(ie);
            }
        }

    }

    static class Westbound implements Runnable {
        protected Deque<Integer> westboundCars;

        public Westbound() {
            westboundCars = new ArrayDeque<>();
        }

        public void createRandomCars() throws InterruptedException {
            Random rand = new Random();
            while(true){
                int car = rand.nextInt(1, 10000);
                while (car % 2 != 0) {
                    car = rand.nextInt(1, 10000);
                }
                westboundCars.addLast(car);       
                Thread.sleep(2000);
                System.out.printf("Car %d is passing.\n", car);
            }  
        }

        @Override
        public void run() {
            try {
                createRandomCars();
            }
            catch(InterruptedException ie) {
                System.out.println(ie);
            }
        }

    }
}