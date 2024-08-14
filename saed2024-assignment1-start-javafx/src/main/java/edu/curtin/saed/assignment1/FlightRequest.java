package edu.curtin.saed.assignment1;
import java.util.random.*;

public class FlightRequest implements Runnable
{
    private final int airportCount;
    private final int airportID;

    public FlightRequest(int numberOfAirports, int originAirportID) {
        this.airportCount = numberOfAirports;
        this.airportID = originAirportID;
    }
    @Override
    public void run() {}
}
