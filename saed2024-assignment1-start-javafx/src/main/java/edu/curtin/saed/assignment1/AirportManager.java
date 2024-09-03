package edu.curtin.saed.assignment1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        this.flightRequestPool = Executors.newFixedThreadPool(10); // fixed size of 10
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
            FlightRequest flightRequest = new FlightRequest(nAirports, airportId, this);
            flightRequestPool.submit(flightRequest); // use the thread pool to handle flight requests

        }
    }

    public void shutdown()
    {
        flightRequestPool.shutdown(); // shutdown thread pool

        try
        {
            if (!flightRequestPool.awaitTermination(5, TimeUnit.SECONDS))
            {
                System.out.println("Forcefully shutting down flight request pool...");
                flightRequestPool.shutdownNow(); // force shutdown
            }
        }
        catch (InterruptedException e)
        {
            flightRequestPool.shutdownNow(); // if thread is interrupted force threadpool to shutdown
            Thread.currentThread().interrupt();
        }

        System.out.println("Shutting down flight request pool...");
        for (Airport airport : airportMap.values()) // call shutdown on each airport
        {
            airport.shutdown();
        }
    }
}
