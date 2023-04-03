import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.*;

//Shared bridge
class Bridge {
    static Car passingCar = null;
}

public class TrafficController {
    static Semaphore light = new Semaphore(1);

    public static void main(String[] args) {
        Direction eastbound = new Direction("East", light);
        Direction westbound = new Direction("West", light);
        eastbound.start();
        westbound.start();
    }
}

class Direction extends Thread {
    private Random rand = new Random();
    protected Semaphore s;
    protected Deque<Car> cars;

    public Direction(String threadNameDirection, Semaphore s) {
        super(threadNameDirection);
        this.s = s;
        this.cars = new ArrayDeque<>();
    }

    public void arrive() throws InterruptedException {
        if(s.tryAcquire()) {
            Bridge.passingCar = cars.removeFirst();
            System.out.printf("Car %d has started passing on the bridge.\n", Bridge.passingCar.getID());
            Thread.sleep(Bridge.passingCar.getSpeed() * 1000);
            passed();
        }
    }

    public void passed() {
        System.out.printf("Car %d has finished passing the bridge.\n", Bridge.passingCar.getID());
        s.release();
    }

    @Override
    public void run() {
        try {
            if (this.getName().equals("East")) {
                int eastCarID = 1;
                while (true) {
                    Car c = new Car(eastCarID, rand.nextInt(20));
                    cars.addLast(c);
                    System.out.printf("Car %d created\n", c.getID());
                    Thread.sleep(rand.nextInt(5,10)*1000);
                    arrive();
                    eastCarID += 2;
                }
            } else {
                int westCarID = 2;
                while (true) {
                    Car c = new Car(westCarID, rand.nextInt(20));
                    cars.addLast(c);
                    System.out.printf("Car %d created\n", c.getID());
                    Thread.sleep(rand.nextInt(5,10)*1000);
                    arrive();
                    westCarID += 2;
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

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