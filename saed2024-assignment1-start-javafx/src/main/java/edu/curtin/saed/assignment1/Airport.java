package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Airport 
{
    // initializing variables
    private int id;
    private int xPos;
    private int yPos;
    private BlockingQueue<Plane> serviceQueue; // queue for servicing
    private BlockingQueue<Plane> availableQueue; // queue for available planes

    // constructor
    public Airport(int id, int xPos, int yPos)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.serviceQueue = new LinkedBlockingQueue<>(); // linked blocking queue for producer/consumer scenario
        this.availableQueue = new LinkedBlockingQueue<>();
    }

    // getters & settters
    public int getId()
    {
        return id;
    }

    public int getX()
    {
        return xPos;
    }

    public int getY()
    {
        return yPos;
    }

    public BlockingQueue<Plane> getServiceQueue()
    {
        return serviceQueue;
    }

    public BlockingQueue<Plane> getAvailablePlanesQueue()
    {
        return availableQueue;
    }

    // so far I have not found a need for airport setters, may add later

    // add planes to queue to get serviced
    public void addPlaneToServiceQueue(Plane plane)
    {
        try
        {
            serviceQueue.put(plane);
            System.out.println("Plane ID: " + plane.getId() + " added to service queue at airport ID: " + this.id);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Failed to add Plane ID: " + plane.getId() + " to service queue at airport ID: " + this.id);
        }
    }

    // code for service simulation
    public void servicePlane()
    {
        
        try
        {
            Plane plane = serviceQueue.take(); // take next plane from service queue
            Service service = new Service(this.id, plane, availableQueue);
            Thread serviceThread = new Thread(service);
            serviceThread.start();
            // TO DO: make service updates print to UI
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("No planes available for servicing at airport ID: " + this.id);
        }
    }

    public void receivePlane(Plane plane)
    {
        // if this is not the beginning of the sim
        if(plane.getDestinationAirport() != null)
            {
                plane.setDestinationAirport(null);
                addPlaneToServiceQueue(plane);
                servicePlane();
            }

        // if it is beginning of sim (planes dont need to be serviced)
        else
        {
            try
            {
                availableQueue.put(plane);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }

    }
}
