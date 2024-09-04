package edu.curtin.saed.assignment1;

import java.util.Random;

import javafx.application.Platform;

public class Plane
{
    private int id;
    private int xPos;
    private int yPos;
    private Airport currentAirport;
    private Airport destinationAirport;
    private GridArea area;
    private GridAreaIcon planeIcon;
    private App app;
    public static final Plane POISON = new Plane(-1, -1, -1, null, null, null);

    public Plane(int id, int xPos, int yPos, Airport currentAirport, GridArea area, App app)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.currentAirport = currentAirport;
        this.destinationAirport = null;
        this.area = area;
        this.app = app;

        Random random = new Random();
        this.planeIcon = new GridAreaIcon(
            this.xPos, this.yPos, random.nextDouble() * 360, 1.0,
            App.class.getClassLoader().getResourceAsStream("pikachu.png"),
            "Plane " + this.id);
        
            if (area != null) // null check incase it's the poison plane
            {
                area.getIcons().add(planeIcon);
            }

        planeIcon.setShown(false);
    }

    // setters & getters (a few of these are not used but still included for good coding standard)
    
    // will have to receive from flight request
    public void setDestinationAirport(Airport destination)
    {
        this.destinationAirport = destination;
    }

    // sets plane's current airport
    public void setCurrentAirport(Airport currentAirport)
    {
        this.currentAirport = currentAirport;
    }

    // sets plane's id
    public void setId(int id)
    {
        this.id = id;
    }

    // sets plane's x coordinate
    public void setX(int x)
    {
        this.xPos = x;
    }

    // sets plane's y coordinate
    public void setY(int y)
    {
        this.yPos = y;
    }

    // retrieves plane's id
    public int getId()
    {
        return id;
    }

    // retrieves plane's x coordinate
    public int getX()
    {
        return xPos;
    }

    // retrieves plane's y coordinate
    public int getY()
    {
        return yPos;
    }

    // retrieves plane's destination airport
    public Airport getDestinationAirport()
    {
        return this.destinationAirport;
    }

    // retrieves plane's current airport
    public Airport getCurrentAirport()
    {
        return this.currentAirport;
    }

    // updates plane's visibility
    public void setVisibility(boolean shown)
    {
        Platform.runLater(() -> {
            planeIcon.setShown(shown); 
            area.requestLayout(); 
        });
    }

     // movement logic was inspired from here: https://gamedev.stackexchange.com/questions/68790/how-can-i-move-an-object-towards-another-along-a-straight-line
    public void flyToDestination()
    {
        setVisibility(true); // make plane visible

        //checking if a destination exists and that the plane isn't already there
        if(destinationAirport != null && destinationAirport.isRunning())
        {
            int destinationId = destinationAirport.getId();
            if(xPos != destinationAirport.getX() && yPos != destinationAirport.getY())
            {
                app.incrementInFlightCount();
                Platform.runLater(() -> app.addUpdate("Plane " + this.id + " has begun flying to Airport " + destinationId));
                
                // calculate direction movements based on current position
                int movementX = Integer.compare(destinationAirport.getX(), xPos);
                int movementY = Integer.compare(destinationAirport.getY(), yPos); 

                // move towards the destination in a loop until it is reached
                while (xPos != destinationAirport.getX() || yPos != destinationAirport.getY() && destinationAirport.isRunning())
                {
                    if (xPos != destinationAirport.getX())
                    {
                        xPos += movementX;
                    }

                    if (yPos != destinationAirport.getY())
                    {
                        yPos += movementY;
                    }

                    // updating UI
                    Platform.runLater(() -> {
                        planeIcon.setPosition(xPos, yPos); 
                        area.requestLayout(); 
                    });
                        try
                        {
                            Thread.sleep(100); 
                        }
                        catch (InterruptedException e)
                        {
                            Thread.currentThread().interrupt();
                            break; // exit loop if thread is interrupted
                        }
                    }

                    // once plane has reached new destination
                    if(destinationAirport.isRunning()) // check of airport is still running (not shutdown)
                    {
                        Platform.runLater(() -> app.addUpdate("Plane " + this.id + " has landed at Airport " + destinationId));
                        setCurrentAirport(destinationAirport);
                        destinationAirport.receivePlane(this);
                        app.decrementInFlightCount();
                        app.incrementCompletedFlightsCount();
                    }
            }
            setVisibility(false); //once landed, make plane invisible
        }
        else
        {
            setVisibility(false); // if plane cannot fly to airport, hide plane
        }       
    }
}
