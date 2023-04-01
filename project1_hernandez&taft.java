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
        Thread westBoundThread = new Thread(new Westbound());
        //eastbound.createRandomCars();
        
    }
    
    static class Eastbound implements Runnable {
        protected Deque<Integer> eastboundCars;

        public Eastbound() {
            eastboundCars = new ArrayDeque<>();
        }

        public void createRandomCars() {
            Random rand = new Random();
            for(int i=0; i<10; i++) {
                int car = rand.nextInt(1,10000);
                while(car % 2 == 0) {
                    car = rand.nextInt(1,10000);
                }
                eastboundCars.addLast(car);
            }
        }

        public Deque<Integer> getCars() {
            return eastboundCars;
        }

        @Override
        public void run() {
            createRandomCars();
        }

    }

    static class Westbound implements Runnable {
        protected Deque<Integer> westboundCars;

        public Westbound() {
            westboundCars = new ArrayDeque<>();
        }

        public void createRandomCars() {
            Random rand = new Random();
            for(int i=0; i<10; i++) {
                int car = rand.nextInt(1,10000);
                while(car % 2 == 0) {
                    car = rand.nextInt(1,10000);
                }
                westboundCars.addLast(car);
            }
        }

        @Override
        public void run() {
            createRandomCars();
        }

    }
}