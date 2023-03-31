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

    }
    
    static class Eastbound implements Runnable {
        protected Deque<Integer> eastboundCars;

        public Eastbound() {
            eastboundCars = new ArrayDeque<>();
        }

        public void createRandomCars() {

        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Unimplemented method 'run'");
        }

    }

    static class Westbound implements Runnable {
        protected Deque<Integer> westboundCars;

        public Westbound() {
            westboundCars = new ArrayDeque<>();
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Unimplemented method 'run'");
        }

    }
}