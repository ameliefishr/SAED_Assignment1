package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Airport 
{
    // initializing variables
    private int id;
    private int x_pos;
    private int y_pos;
    private BlockingQueue<Plane> serviceQueue; // queue for servicing

    // constructor
    public Airport(int id, int x_pos, int y_pos)
    {
        this.id = id;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.serviceQueue = new LinkedBlockingQueue<>(); // linked blocking queue for producer/consumer scenario
    }

    // getters & settters
    public int getId()
    {
        return id;
    }

    public int getX()
    {
        return x_pos;
    }

    public int getY()
    {
        return y_pos;
    }

    public BlockingQueue<Plane> getServiceQueue()
    {
        return serviceQueue;
    }

    // so far I have not found a need for airport setters, may add later

    // code for service simulation
    public void runService(Plane plane)
    {
        try
        {
        // using sleep to simulate the servicing time
        Thread.sleep(1000);
        // TO DO: make service updates print to UI
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
