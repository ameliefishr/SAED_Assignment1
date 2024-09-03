package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Airport implements Runnable
{
    // initializing variables
    private int id;
    private int xPos;
    private int yPos;
    private boolean running;
    private BlockingQueue<Plane> availableQueue; // queue for available planes
    private BlockingQueue<FlightRequest> flightRequestQueue; // queue for fight requests
    private ExecutorService servicePool;

    // constructor
    public Airport(int id, int xPos, int yPos)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.availableQueue = new LinkedBlockingQueue<>(); // linked blocking queue for producer/consumer scenario
        this.flightRequestQueue = new LinkedBlockingQueue<>();
        this.servicePool = Executors.newFixedThreadPool(10);
        this.running = true;
        Thread airportThread = new Thread(this);
        airportThread.start();
    }

    // setters & getters (a few of these are not used but still included for good coding standard)
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

    public BlockingQueue<Plane> getAvailablePlanesQueue()
    {
        return availableQueue;
    }

    public BlockingQueue<FlightRequest> getFlightRequests()
    {
        return flightRequestQueue;
    }

    public boolean isRunning()
    {
        return running;
    }

    // so far I have not found a need for airport setters, may add later

    public void putNextFlightRequest(FlightRequest flightRequest) throws InterruptedException
    {
        flightRequestQueue.put(flightRequest);
    }

    public void putNextAvailablePlane(Plane availablePlane) throws InterruptedException
    {
        availableQueue.put(availablePlane);
    }

    // code for service simulation
    public void servicePlane(Plane newPlane)
    {
        if(!Thread.currentThread().isInterrupted())
        {
            Service service = new Service(this.id, newPlane, availableQueue);
            servicePool.submit(service);
            
        }
    }

    public void receivePlane(Plane newPlane)
    {
        if(!Thread.currentThread().isInterrupted())
        {
            // if this is not the beginning of the sim
            if(newPlane.getDestinationAirport() != null)
                {
                    newPlane.setDestinationAirport(null); // plane no longer has a destination until it receives a new one
                    servicePlane(newPlane); // run service process on plane
                }

            // if it is beginning of sim (planes dont need to be serviced)
            else
            {
                try
                {
                    availableQueue.put(newPlane); // put plane straight into available planes queue
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void handleFlightRequests()
    {
        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                FlightRequest flightRequest = flightRequestQueue.take(); // take a request from the queue (CONSUME)
                Plane availablePlane = availableQueue.take(); // take an available plane from the queue (CONSUME)
                
                Airport destination = flightRequest.getDestinationAirport(); // get destination from flight request
                if (destination == null) // null checks
                {
                    System.out.println("Error: destination was null");
                } 
                else
                {
                    availablePlane.setDestinationAirport(destination); // setting new destination from flight request
                    availablePlane.flyToDestination(); // begin flying to destination
                }
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break; // exit looop if thread  is interrupted
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
        running = false; // make sure no planes try to fly to airport after it's shutdown
        servicePool.shutdown();// shut down service thread pool 
        Thread.currentThread().interrupt(); // stop airport thread
        System.out.println("Shutting down airport ID: " + this.id);
        try
        {
            if (!servicePool.awaitTermination(5, TimeUnit.SECONDS))
            {
                servicePool.shutdownNow(); // force shutdown if regular shutdown takes too long
            }
        }
        catch (InterruptedException e)
        {
            servicePool.shutdownNow(); // force shutdown on interruption
        }
    }
}
