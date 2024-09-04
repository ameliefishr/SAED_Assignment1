package edu.curtin.saed.assignment1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
        this.running = true;
        Thread airportThread = new Thread(this);
        airportThread.setName("Airport " + id);
        airportThread.start();

        // taken from 'Listing 21: Some way to obtain executors', in the Week 3 Lecture Notes
        this.servicePool = new ThreadPoolExecutor(
            4, 10, // min 4 threads max 10
            15, TimeUnit.SECONDS, // destroy any idle threads after 15 sec
            new SynchronousQueue<>(), // used to deliver new tasks to the threads
            new NamedThreadFactory("Service") // naming thread for easy tracking
        );
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


    // service queue functionalities
    // PRODUCER
    public void putNextFlightRequest(FlightRequest flightRequest) throws InterruptedException
    {
        flightRequestQueue.put(flightRequest);
    }

    public void putNextAvailablePlane(Plane availablePlane) throws InterruptedException
    {
        availableQueue.put(availablePlane);
    }

    // CONSUMER
    public FlightRequest getNextFlightRequest() throws InterruptedException
    {
        return flightRequestQueue.take();
    }

    public Plane getNextAvailablePlane() throws InterruptedException
    {
        return availableQueue.take();
    }

    // code for service simulation
    public void servicePlane(Plane newPlane)
    {
        if(!Thread.currentThread().isInterrupted())
        {
            Service service = new Service(this.id, newPlane,this);
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
                    putNextAvailablePlane(newPlane); // put plane straight into available planes queue
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void handleFlightRequests() throws InterruptedException
    {
        while(running && !Thread.currentThread().isInterrupted())
        {
            try
            {
                FlightRequest flightRequest = getNextFlightRequest(); // take a request from the queue (CONSUME)
                
                if(flightRequest == FlightRequest.POISON)
                {
                    break; // if poison object detected break loop
                }
                
                Plane availablePlane = getNextAvailablePlane(); // take an available plane from the queue (CONSUME)
                
                if(availablePlane == Plane.POISON) // if poison object detected break loop
                {
                    break;
                }

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
                break; // exit loop if thread  is interrupted
            }
        }
    }

    public void shutDownAirport()
    {
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() 
    {
        try
        {
            handleFlightRequests();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown()
    {
        running = false; // make sure no planes try to fly to airport after it's shutdown
        try
        {
            servicePool.shutdown();// shut down service thread pool 
            if (!servicePool.awaitTermination(5, TimeUnit.SECONDS))
            {
                servicePool.shutdownNow(); // force shutdown if regular shutdown takes too long
            }
        }

        catch (InterruptedException e)
        {
            servicePool.shutdownNow(); // force shutdown on interruption
            shutDownAirport();
        }

        // passing poison object to blocking queue's
        try
        {
            putNextFlightRequest(FlightRequest.POISON);
            putNextAvailablePlane(Plane.POISON);
        }

        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    

        shutDownAirport();// stop airport thread
        System.out.println("Shutting down airport ID: " + this.id);
    }
}
