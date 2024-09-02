package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Airport implements Runnable
{
    // initializing variables
    private int id;
    private int xPos;
    private int yPos;
    private BlockingQueue<Plane> serviceQueue; // queue for servicing
    private BlockingQueue<Plane> availableQueue; // queue for available planes
    private BlockingQueue<FlightRequest> flightRequestQueue; // queue for fight requests

    // constructor
    public Airport(int id, int xPos, int yPos)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.serviceQueue = new LinkedBlockingQueue<>(); // linked blocking queue for producer/consumer scenario
        this.availableQueue = new LinkedBlockingQueue<>();
        this.flightRequestQueue = new LinkedBlockingQueue<>();
        Thread airportThread = new Thread(this);
        airportThread.start();
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

    public BlockingQueue<FlightRequest> getFlightRequests()
    {
        return flightRequestQueue;
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

    public void addFlightRequestToQueue(FlightRequest flightRequest)
    {
        //TO DO: code to add flight request to queue
        try
        {
            flightRequestQueue.put(flightRequest);

        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
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
                plane.setDestinationAirport(null); // plane no longer has a destination
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

    public void handleFlightRequests()
    {
        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                FlightRequest flightRequest = flightRequestQueue.take(); // take a reques from the queue
                Plane availablePlane = availableQueue.take(); // take an available plane from the queue
                
                availablePlane.setDestinationAirport(flightRequest.getDestinationAirport());
                availablePlane.flyToDestination();
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.out.println("Flight request processing interrupted at airport ID: " + this.id);
                break;
            }
        }
    }

    @Override
    public void run() 
    {
        handleFlightRequests();
    }

    public void shutdown()
    {
        Thread.currentThread().interrupt(); // stop thread
    }
}
