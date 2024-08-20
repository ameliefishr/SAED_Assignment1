package edu.curtin.saed.assignment1;
import java.util.random.*;
import java.util.*;
import java.io.*;

public class FlightRequest implements Runnable
{
    private int nAirports; // command input
    private int originAirport; // command input
    private BufferedReader reader; // for reading command

    // constructor
    public FlightRequest(int numberOfAirports, int originAirportID)
    {
        this.nAirports = numberOfAirports;
        this.originAirport = originAirportID;
    }

    @Override
    public void run()
    {
        Process proc = null; // for running FQ
        try
        {
            System.out.println("Starting flight requests for origin airport ID: " + originAirport);
            proc = Runtime.getRuntime().exec(
                new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_flight_requests.bat",
                             String.valueOf(nAirports),
                             String.valueOf(originAirport)});
                             
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null)
            {
                int destinationAirportID = Integer.parseInt(line);
                System.out.println("Received flight request to airport ID: " + destinationAirportID);
                Airport destinationAirport = null; // TO DO: add a way to get destination airports

                // TO DO: add a way to assign FQ to airport's planes
                System.out.println("Processing flight from airport ID " + originAirport + " to airport ID " + destinationAirportID);
            }
            System.out.println("Flight requests completed for origin airport ID: " + originAirport);
        }
        catch (IOException e)
        {
            System.err.println("Error while running flight requests for origin airport ID: " + originAirport);
            e.printStackTrace();
        }
    }
}
