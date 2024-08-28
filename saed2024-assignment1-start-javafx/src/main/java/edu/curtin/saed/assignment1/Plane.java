package edu.curtin.saed.assignment1;

public class Plane implements Runnable
{
    private int id;
    private int xPos;
    private int yPos;
    private Airport departingAirport;
    private Airport destinationAirport;

    public Plane(int id, int xPos, int yPos, Airport departing)
    {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.departingAirport = departing;
    }

    // setters & getters
    
    // will have to receive from flight request
    public void setDestinationAirport(Airport destination)
    {
        this.destinationAirport = destination;
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

    // need to override runnable's run()
    @Override
    public void run()
    {
        // TO DO: add flight logic here;
    }
}
