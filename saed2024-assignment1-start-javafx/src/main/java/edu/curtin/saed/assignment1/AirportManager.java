package edu.curtin.saed.assignment1;

import java.util.HashMap;
import java.util.Map;

public class AirportManager
{
    private Map<Integer, Airport> airportMap;

    // constructor
    public AirportManager()
    {
        this.airportMap = new HashMap<>();
    }

    public void addAirport(Airport airport)
    {
        airportMap.put(airport.getId(), airport);
    }

    // getters / setters
    public Airport getAirportById(int id)
    {
        return airportMap.get(id);
    }

    public void removeAirportById(int id)
    {
        airportMap.remove(id);
    }

    public Map<Integer, Airport> getAllAirports()
    {
        return airportMap;
    }

    // check if airport exists
    public boolean airportExists(int id)
    {
        return airportMap.containsKey(id);
    }
}
