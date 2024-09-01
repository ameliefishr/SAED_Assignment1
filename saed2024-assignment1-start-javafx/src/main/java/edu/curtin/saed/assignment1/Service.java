package edu.curtin.saed.assignment1;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class Service implements Runnable
{
    private int planeId; // command input
    private Plane unservicedPlane; // plane object (to add to availableQueue afterwards)
    private int airportId; // command input
    private BlockingQueue<Plane> availableQueue;

    // constructor
    public Service(int airportID, Plane plane, BlockingQueue<Plane> availableQueue)
    {
        this.unservicedPlane = plane;
        this.planeId = plane.getId();
        this.airportId = airportID;
        this.availableQueue = availableQueue;
    }

    @Override
    public void run()
    {
        Process proc; // for running service command
        try
        {
            System.out.println("Starting service for plane ID: " + planeId + " at airport ID: " + airportId);
            proc = Runtime.getRuntime().exec(
                new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_plane_service.bat",
                             String.valueOf(airportId),
                             String.valueOf(planeId)});
                             
            try(BufferedReader br = proc.inputReader())
            {
                String line;
            
                while ((line = br.readLine()) != null)
                {
                    String endMessage = line;

                    // TO DO: link service to plane/airport/main sim
                    System.out.println("Completed service for plane ID: " + planeId + " at airport ID: " + airportId);
                }
                availableQueue.put(unservicedPlane);
                System.out.println("Plane ID: " + unservicedPlane.getId() + " is now available for flights.");
            }
        }
        catch (IOException | InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Error while running service for plane ID: " + planeId + " at airport ID: " + airportId);
        }
    }
}
