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

    public Plane(int id, int xPos, int yPos, Airport currentAirport, GridArea area)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.currentAirport = currentAirport;
        this.destinationAirport = null;
        this.area = area;

        Random random = new Random();
        this.planeIcon = new GridAreaIcon(
            this.xPos, this.yPos, random.nextDouble() * 360, 1.0,
            App.class.getClassLoader().getResourceAsStream("pikachu.png"),
            "Plane " + this.id);
        area.getIcons().add(planeIcon);
    }

    // setters & getters
    
    // will have to receive from flight request
    public void setDestinationAirport(Airport destination)
    {
        this.destinationAirport = destination;
    }

    public void setCurrentAirport(Airport currentAirport)
    {
        this.currentAirport = currentAirport;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setX(int x)
    {
        this.xPos = x;
    }

    public void setY(int y)
    {
        this.yPos = y;
    }

    public int getId()
    {
        return id;
    }

    public int getX()
    {
        return xPos;
    }

    public int getY()
    {
        return yPos;
    }

    public Airport getDestinationAirport()
    {
        return this.destinationAirport;
    }

    public Airport getCurrentAirport()
    {
        return this.currentAirport;
    }

     // movement logic was inspired from here: https://gamedev.stackexchange.com/questions/68790/how-can-i-move-an-object-towards-another-along-a-straight-line
    public void flyToDestination()
    {
        //checking if a destination exists and that the plane isn't already there
        if(destinationAirport != null)
        {
            if(xPos != destinationAirport.getX() && yPos != destinationAirport.getY())
            {

                // calculate direction movements based on current position
                int movementX = Integer.compare(destinationAirport.getX(), xPos);
                int movementY = Integer.compare(destinationAirport.getY(), yPos); 

                // Move towards the destination in a loop
                while (xPos != destinationAirport.getX() || yPos != destinationAirport.getY())
                {
                    if (xPos != destinationAirport.getX())
                    {
                        xPos += movementX;
                    }

                    if (yPos != destinationAirport.getY())
                    {
                        yPos += movementY;
                    }

                    System.out.println("Plane " + id + " moving to (" + xPos + ", " + yPos + ") towards destination (" + destinationAirport.getX() + ", " + destinationAirport.getY() + ")");

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
                            break;
                        }
                    }

                    // once plane has reached new destination
                    setCurrentAirport(destinationAirport);
                    destinationAirport.receivePlane(this);
                    System.out.println("Plane " + id + " has reached the destination at (" + destinationAirport.getX() + ", " + destinationAirport.getY() + ")");
            }
        }        
    }
}
