package edu.curtin.saed.assignment1;
import java.io.*;

// Flight Request Class
// Responsible for generating flight requests and passing them off to the thread's associated airport

public class FlightRequest implements Runnable
{
    private int nAirports; // command input
    private int originAirport; // command input
    private AirportManager airportManager; // airport manager instance
    private Airport destinationAirport; // airport id of destination

    public static final FlightRequest POISON = new FlightRequest(-1, -1, null); // Unique IDs for poison pill

    // constructor
    public FlightRequest(int numberOfAirports, int originAirportID, AirportManager airportManager)
    {
        this.nAirports = numberOfAirports;
        this.originAirport = originAirportID;
        this.airportManager = airportManager;
    }

    // returns destination airport generatred by flight request
    public Airport getDestinationAirport()
    {
        return destinationAirport;
    }

    @Override
    public void run()
    {
        Process proc; // for running flight request proccess
        if (nAirports >= 2 && originAirport >= 0) // checks to see if it satisfies command line arg structure
        {
            try
            {
                System.out.println("Starting flight requests for origin airport ID: " + originAirport);
                try
                {
                    // try UNIX version
                    proc = Runtime.getRuntime().exec(
                        new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_flight_requests",
                                    String.valueOf(nAirports),
                                    String.valueOf(originAirport)});
                }
                catch (IOException e1)
                {
                    try
                    {
                        // try windows version
                        proc = Runtime.getRuntime().exec(
                            new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_flight_requests.bat",
                                        String.valueOf(nAirports),
                                        String.valueOf(originAirport)});
                    }
                    catch(IOException e2)
                    {
                        System.err.println("Error executing batch file: " + e2.getMessage());
                        proc = null;
                    }
                }
                
                if(proc != null) // null check incase batch file reading failed
                {
                    try(BufferedReader br = proc.inputReader())
                    {
                        String line;
                        while ((line = br.readLine()) != null)
                        {
                            try
                            {
                                int destinationAirportID = Integer.parseInt(line.trim());
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
                                        Thread.currentThread().interrupt();
                                    }
                                }
                                else
                                {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                            // I had to add this in because I kept getting jvm memory errors that would print from the bat file and throw this exception
                            // after I fixed my threading they went away but I left this in just incase
                            catch(NumberFormatException e) 
                            {
                                System.out.println("Flight Request output not an integer: " + e.getMessage());
                            }

    
                            proc.destroy();  // make sure to destroy proccess when sim is complete
                        
                        }
                    }
                }
            }
            catch (IOException e)
            {
                System.err.println("Error while proccessing file request");
            }
        }

    }
}
