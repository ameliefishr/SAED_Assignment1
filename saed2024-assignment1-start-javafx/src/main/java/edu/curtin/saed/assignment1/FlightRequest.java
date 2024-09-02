package edu.curtin.saed.assignment1;
import java.io.*;

public class FlightRequest implements Runnable
{
    private int nAirports; // command input
    private int originAirport; // command input
    private AirportManager airportManager; // airport manager instance
    private Airport destinationAirport; // airport id of destination

    // constructor
    public FlightRequest(int numberOfAirports, int originAirportID, AirportManager airportManager)
    {
        this.nAirports = numberOfAirports;
        this.originAirport = originAirportID;
        this.airportManager = airportManager;
    }

    public Airport getDestinationAirport()
    {
        return destinationAirport;
    }



    @Override
    public void run()
    {
        Process proc; // for running FQ
        if (nAirports >= 2)
        {
            try
            {
                System.out.println("Starting flight requests for origin airport ID: " + originAirport);
                proc = Runtime.getRuntime().exec(
                    new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_flight_requests.bat",
                                String.valueOf(nAirports),
                                String.valueOf(originAirport)});
                                
                try(BufferedReader br = proc.inputReader())
                {
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        try
                        {
                            int destinationAirportID = Integer.parseInt(line.trim());
                            System.out.println("Received flight request to airport ID: " + destinationAirportID);
                            this.destinationAirport = airportManager.getAirportById(destinationAirportID);
                            Airport currentAirport = airportManager.getAirportById(originAirport);
                            currentAirport.addFlightRequestToQueue(this);
                            // TO DO: add a way to assign FQ to airport's planes
                            System.out.println("Processing flight from airport ID " + originAirport + " to airport ID " + destinationAirportID);
                        }
                        // I had to add this in because I kept getting jvm memory errors (i have low space) that would print from the bat file and throw this exception
                        // after I restarted my computer they went away but I left this in just incase
                        catch(NumberFormatException e) 
                        {
                            System.out.println("Flight Request output not an integer: " + e.getMessage());
                        }
                    }
                }
                
            }
            catch (IOException e)
            {
                System.err.println("Error while running flight requests for origin airport ID: " + originAirport);
            }
        }
    }
}
