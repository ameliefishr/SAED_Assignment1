package edu.curtin.saed.assignment1;
import java.util.random.*;
import java.util.*;
import java.io.*;

public class Service implements Runnable
{
    private int planeId; // command input
    private int airportId; // command input
    private AirportManager airportManager;
    private BufferedReader reader; // for reading command

    // constructor
    public Service(int airport_id, int plane_id)
    {
        this.planeId = plane_id;
        this.airportId = airport_id;
    }

    @Override
    public void run()
    {
        Process proc = null; // for running service command
        try
        {
            System.out.println("Starting service for plane ID: " + planeId + " at airport ID: " + airportId);
            proc = Runtime.getRuntime().exec(
                new String[]{"C:/Users/ameli/SAED_Assignment1/saed2024-assignment1-start-javafx/comms/bin/saed_plane_service.bat",
                             String.valueOf(airportId),
                             String.valueOf(planeId)});
                             
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null)
            {
                int destinationAirportID = Integer.parseInt(line);
                Airport destinationAirport = airportManager.getAirportById(destinationAirportID);

                // TO DO: link service to plane/airport/main sim
                System.out.println("Processing service for plane ID: " + planeId + " at airport ID: " + airportId);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error while running service for plane ID: " + planeId + " at airport ID: " + airportId);
            e.printStackTrace();
        }
    }
}
