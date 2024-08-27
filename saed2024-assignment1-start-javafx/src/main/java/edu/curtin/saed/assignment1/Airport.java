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
        Service service = new Service(this.id, plane.getId());
        Thread serviceThread = new Thread(service); // make a new thread to run the service on
        
        try
        {
            serviceQueue.put(plane); // put the plane that just landed into service queue
            System.out.println("Plane ID: " + plane.getId() + "added to service queue at airport" + this.id); // for testing, will switch to ui statements later
            
            // TO DO: make service updates print to UI
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Failed to add Plane ID: " + plane.getId() + " to service queue at airport ID: " + this.id);
        }
    }
}
