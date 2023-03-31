import java.util.concurrent.*;
import java.util.Deque;
import java.util.ArrayDeque;

class SynchedBridge {

    public static void main(String[] args) {
        
    }
    
    static class Eastbound implements Runnable {
        protected Deque<Integer> eastboundCars;

        @Override
        public void run() {
            throw new UnsupportedOperationException("Unimplemented method 'run'");
        }

    }

    static class Westbound implements Runnable {
        protected Deque<Integer> westboundCars;

        @Override
        public void run() {
            throw new UnsupportedOperationException("Unimplemented method 'run'");
        }

    }
}