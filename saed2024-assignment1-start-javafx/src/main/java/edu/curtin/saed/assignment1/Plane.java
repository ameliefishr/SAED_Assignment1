package edu.curtin.saed.assignment1;

public class Plane implements Runnable
{
    private int id;
    private int xPos;
    private int yPos;
    private Airport currentAirport;
    private Airport destinationAirport;

    public Plane(int id, int xPos, int yPos, Airport currentAirport)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.currentAirport = currentAirport;
        this.destinationAirport = null;
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

    public void flyToDestination(Airport destination)
    {
        // TO DO: add flight logic here;
        System.out.println("Plane ID: " + id + " flying to Airport ID: " + destination.getId());
    }

    // need to override runnable's run()
    @Override
    public void run()
    {

        
    }
}
