package edu.curtin.saed.assignment1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// this class is responsible for managing all of
// the airports and their flight requests
public class AirportManager
{
    // initializing class variables
    private Map<Integer, Airport> airportMap;
    private int airportCount;
    private ExecutorService flightRequestPool; // thread pool for flight requests

    // constructor
    public AirportManager()
    {
        this.airportMap = new HashMap<>();
        this.airportCount = 0;
        
        // taken from 'Listing 21: Some way to obtain executors', in the Week 3 Lecture Notes
        this.flightRequestPool = new ThreadPoolExecutor(
            4, 10, // min 4 threads max 10
            15, TimeUnit.SECONDS, // destroy any idle threads after 15 sec
            new SynchronousQueue<>(), // used to deliver new tasks to the threads
            new NamedThreadFactory("Flight Request") // naming thread for easy tracking
        );
   }

    // adds an airport to the hashmap
    public void addAirport(Airport airport)
    {
        airportMap.put(airport.getId(), airport);
        airportCount++;

    }

    // getters / setters
    // returns an airport from the hashmap based on it's id
    public Airport getAirportById(int id)
    {
        return airportMap.get(id);
    }

    // returns the current total of airports
    public int getAirportCount()
    {
        return airportCount;
    }

    //removes an airport from the map (not used)
    public void removeAirportById(int id)
    {
        airportMap.remove(id);
        airportCount--;
    }

    // returns hashmap containing airports and their id's
    public Map<Integer, Airport> getAllAirports()
    {
        return airportMap;
    }

    // check if airport exists
    public boolean airportExists(int id)
    {
        return airportMap.containsKey(id);
    }
    
    // function to start the flight request process for each airport
    public void startFlightRequests()
    {
        int nAirports = getAirportCount();

        for (Integer airportId : airportMap.keySet())
        {
            Airport airport = getAirportById(airportId);
            airport.start();
            FlightRequest flightRequest = new FlightRequest(nAirports, airportId, this);
            flightRequestPool.submit(flightRequest); // use the thread pool to handle flight requests

        }
    }

    // shutdown approach learnt from: https://www.alibabacloud.com/blog/java-development-practices-using-thread-pools-and-thread-variables-properly_600180#:~:text=We%20should%20call%20the%20shutdown,the%20thread%20pool%20is%20closed.
    public void shutdown()
    {
        try
        {
            flightRequestPool.shutdown(); // shutdown thread pool
            System.out.println("Shutting down flight request pool...");
            if (!flightRequestPool.awaitTermination(5, TimeUnit.SECONDS))
            {
                System.out.println("Forcefully shutting down flight request pool...");
                flightRequestPool.shutdownNow(); // force shutdown after 5 secs
            }
        }
        catch (InterruptedException e)
        {
            flightRequestPool.shutdownNow(); // if thread is interrupted force threadpool to shutdown
            Thread.currentThread().interrupt();
        }

        for (Airport airport : airportMap.values()) // call shutdown on each airport
        {
            airport.shutdown();
        }
    }
}
