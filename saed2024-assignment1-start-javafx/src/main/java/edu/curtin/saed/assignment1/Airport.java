package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Airport implements Runnable
{
    // initializing variables
    private boolean running;
    private int id;
    private int xPos;
    private int yPos;
    private BlockingQueue<Plane> serviceQueue; // queue for servicing
    private BlockingQueue<Plane> availableQueue; // queue for available planes
    private BlockingQueue<FlightRequest> flightRequestQueue; // queue for fight requests
    private ExecutorService servicePool;

    // constructor
    public Airport(int id, int xPos, int yPos)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.serviceQueue = new LinkedBlockingQueue<>(); // linked blocking queue for producer/consumer scenario
        this.availableQueue = new LinkedBlockingQueue<>();
        this.flightRequestQueue = new LinkedBlockingQueue<>();
        this.running = true;
        this.servicePool = Executors.newFixedThreadPool(10);
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

    public boolean isRunning()
    {
        return running;
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
        }
    }

    public void addFlightRequestToQueue(FlightRequest flightRequest)
    {
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
        if(running)
        {
            try
            {
                Plane plane = serviceQueue.take(); // take next plane from service queue
                Service service = new Service(this.id, plane, availableQueue);
                servicePool.submit(service);
                // TO DO: make service updates print to UI
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void receivePlane(Plane plane)
    {
        if(running)
        {
            // if this is not the beginning of the sim
            if(plane.getDestinationAirport() != null)
                {
                    plane.setDestinationAirport(null); // plane no longer has a destination
                    addPlaneToServiceQueue(plane); // add plane to destination airport's service queue
                    servicePlane(); // run service process on plane
                }

            // if it is beginning of sim (planes dont need to be serviced)
            else
            {
                try
                {
                    availableQueue.put(plane); // put plane straight into available planes queue
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
        while(running & !Thread.currentThread().isInterrupted())
        {
            try
            {
                FlightRequest flightRequest = flightRequestQueue.take(); // take a reques from the queue
                Plane availablePlane = availableQueue.take(); // take an available plane from the queue
                
                Airport destination = flightRequest.getDestinationAirport();
                if (destination == null) 
                {
                    System.out.println("Error: destination was null");
                } 
                else
                {
                    availablePlane.setDestinationAirport(destination);
                    availablePlane.flyToDestination();
                }
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break; // exit looop
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
        running = false;
        servicePool.shutdown();// shut down service thread pool 
        Thread.currentThread().interrupt(); // stop airport thread
        System.out.println("Shutting down airport ID: " + this.id);
        try
        {
            if (!servicePool.awaitTermination(5, TimeUnit.SECONDS))
            {
                servicePool.shutdownNow(); // force shutdown if necessary
            }
        }
        catch (InterruptedException e)
        {
            servicePool.shutdownNow(); // force shutdown on interruption
        }
    }
}
