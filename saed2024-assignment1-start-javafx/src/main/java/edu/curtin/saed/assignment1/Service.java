package edu.curtin.saed.assignment1;

import java.io.*;

// Service class
// Runs service process on provided plane 
public class Service implements Runnable
{
    private int planeId; // command input
    private Plane unservicedPlane; // plane object (to add to availableQueue afterwards)
    private int airportId; // command input
    private Airport airport; // airport service is running from

    // constructor
    public Service(int airportID, Plane plane, Airport airport)
    {
        this.unservicedPlane = plane;
        this.planeId = plane.getId();
        this.airportId = airportID;
        this.airport = airport;
    }

    @Override
    public void run()
    {
        Process proc; // for running service command
        try
        {
            // run service proccess
            try
            {
                // try UNIX version
            proc = Runtime.getRuntime().exec(
                new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_plane_service",
                             String.valueOf(airportId),
                             String.valueOf(planeId)});
            }
            catch (IOException e1)
            {
                try
                {
                    // try windows version
                    proc = Runtime.getRuntime().exec(
                        new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_plane_service.bat",
                                     String.valueOf(airportId),
                                     String.valueOf(planeId)});
                }
                catch (IOException e2)
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
                
                    while ((line = br.readLine()) != null && !Thread.currentThread().isInterrupted())
                    {
                        String endMessage = line;
                        airport.printEndMessage(endMessage);
                    }

                    try
                    {
                        airport.putNextAvailablePlane(unservicedPlane); // put freshly serviced plane into queue of available planes (PRODUCER)
                    }
                    
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }

                    proc.destroy();  // make sure to destroy proccess when sim is complete
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error while proccessing file request");
        }
    }
}
