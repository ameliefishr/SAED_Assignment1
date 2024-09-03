package edu.curtin.saed.assignment1;
import java.io.*;

public class FlightRequest implements Runnable
{
    private int nAirports; // command input
    private int originAirport; // command input
    private AirportManager airportManager; // airport manager instance
    private Airport destinationAirport; // airport id of destination
    private volatile boolean running = true;

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

    public void stop() 
    {
        running = false; // stop the run loop
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
                    while (running && (line = br.readLine()) != null)
                    {
                        if (Thread.currentThread().isInterrupted())
                        {
                            break; // exit loop if thread is interrupted
                        }

                        try
                        {
                            int destinationAirportID = Integer.parseInt(line.trim());
                            System.out.println("Received flight request to airport ID: " + destinationAirportID);
                            this.destinationAirport = airportManager.getAirportById(destinationAirportID);

                            if(destinationAirport != null && destinationAirport.isRunning())
                            {
                                Airport currentAirport = airportManager.getAirportById(originAirport);
                                try
                                {
                                    currentAirport.putNextFlightRequest(this);
                                }
                                catch (InterruptedException e)
                                {
                                    
                                }
                                System.out.println("Processing flight from airport ID " + originAirport + " to airport ID " + destinationAirportID);
                            }
                        }
                        // I had to add this in because I kept getting jvm memory errors that would print from the bat file and throw this exception
                        // after I fixed my threading they went away but I left this in just incase
                        catch(NumberFormatException e) 
                        {
                            System.out.println("Flight Request output not an integer: " + e.getMessage());
                        }
                        finally
                        {
                            if (proc != null)
                            {
                                proc.destroy(); 
                            }
                        }
                    }
                }
                
            }
            catch (IOException e)
            {
                System.err.println("IOException while proccessing file request");
            }
        }
    }
}
