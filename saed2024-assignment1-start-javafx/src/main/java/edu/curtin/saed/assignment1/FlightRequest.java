package edu.curtin.saed.assignment1;
import java.util.random.*;
import java.util.*;
import java.io.*;

public class FlightRequest implements Runnable
{
    private int airportCount; // command input
    private int originAirportID; // command input
    private Process proc; // for running FQ
    private BufferedReader reader; // for reading command

    // constructor
    public FlightRequest(int numberOfAirports, int originAirportID)
    {
        this.airportCount = numberOfAirports;
        this.originAirportID = originAirportID;
    }

    @Override
    public void run()
    {
        try
        {
            proc = Runtime.getRuntime().exec(
                new String[]{"comms/bin/saed_flight_requests",
                             String.valueOf(airportCount),
                             String.valueOf(originAirportID)});
                             
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null)
            {
                int destinationAirportID = Integer.parseInt(line);
                Airport destinationAirport = null; // TO DO: add a way to get destination airports

                // TO DO: add a way to assign FQ to airport's planes
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
