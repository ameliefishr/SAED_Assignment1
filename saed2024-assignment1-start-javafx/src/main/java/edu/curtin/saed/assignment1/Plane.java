package edu.curtin.saed.assignment1;

public class Plane implements Runnable
{
    private int id;
    private int x_pos;
    private int y_pos;
    private Airport departingAirport;
    private Airport destinationAirport;

    public Plane(int id, int x_pos, int y_pos, Airport departing)
    {
        this.id = id;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
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
        this.x_pos = x;
    }

    public void setY(int y)
    {
        this.y_pos = y;
    }

    public int getId()
    {
        return id;
    }

    public int getX()
    {
        return x_pos;
    }

    public int getY()
    {
        return y_pos;
    }

    // need to override runnable's run()
    @Override
    public void run()
    {
        // TO DO: add flight logic here;
    }
}
