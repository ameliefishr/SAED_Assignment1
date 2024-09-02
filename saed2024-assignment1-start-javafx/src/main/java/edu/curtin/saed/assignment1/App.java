package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


/**
 * This is demonstration code intended for you to modify. Currently, it sets up a rudimentary
 * JavaFX GUI with the basic elements required for the assignment.
 *
 * (There is an equivalent Swing version of this, which you can use if you have trouble getting
 * JavaFX as a whole to work.)
 *
 * You will need to use the GridArea object, and create various GridAreaIcon objects, to represent
 * the on-screen map.
 *
 * Use the startBtn, endBtn, statusText and textArea objects for the other input/output required by
 * the assignment specification.
 *
 * Break this up into multiple methods and/or classes if it seems appropriate. Promote some of the
 * local variables to fields if needed.
 */
import java.util.Random;
public class App extends Application
{
    private static final int gridWidth = 10;
    private static final int gridHeight = 10;
    private int startCount = 0;
    private AirportManager airportManager = new AirportManager();

    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage)
    {
        // Set up the main "top-down" display area. This is an example only, and you should
        // change this to set it up as you require.

        GridArea area = new GridArea(gridWidth, gridHeight);
        area.setGridLines(false); // If desired
        area.setStyle("-fx-background-color: #73c8a1;");

        Random random = new Random();
        for (int i = 0; i < 10; i++)
        {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);
            String airportName = "Airport " + (i + 1);
            int airportId = i + 1;

            // creating airports (default 10)
            // airport creation also handles service queue & available queue set-up
            Airport newAirport = new Airport(airportId, x, y, airportManager);
            airportManager.addAirport(newAirport);            

            // adding airports to grid 
            GridAreaIcon airportIcon = new GridAreaIcon(
                x, y, 0.0, 1.0, 
                App.class.getClassLoader().getResourceAsStream("pokecentre.jpg"),
                airportName);
            area.getIcons().add(airportIcon);

            // creating planes for each airport (default 10 per airport)
            for (int j = 0; j < 10; j++)
            {
                int planeId = j;
                Plane plane = new Plane(planeId, x, y, newAirport, area);
                newAirport.receivePlane(plane);

                // TO DO: thread pool of planes <-- wrong, probably want thread pool of flight requests or services instead
                //new Thread(plane).start();
            }
            // using airport manager to start a flight request proccess for each created airport
        }
    

        // Set up other key parts of the user interface. You'll also want to adjust this.

        var startBtn = new Button("Start");
        var endBtn = new Button("End");

        startBtn.setOnAction((event) ->
        {
            if(startCount == 0)
            {
                airportManager.startFlightRequests(); // ensuring flight request process is only started once
            }
            System.out.println("Start button pressed");
            startCount++;
        });
        endBtn.setOnAction((event) ->
        {
            System.out.println("End button pressed");
        });
        stage.setOnCloseRequest((event) ->
        {
            System.out.println("Close button pressed");
        });
        var statusText = new Label("Label Text");
        var textArea = new TextArea();
        textArea.appendText("Sidebar\n");
        textArea.appendText("Text\n");


        // Below is basically just the GUI "plumbing" (connecting things together).

        var toolbar = new ToolBar();
        toolbar.getItems().addAll(startBtn, endBtn, new Separator(), statusText);

        var splitPane = new SplitPane();
        splitPane.getItems().addAll(area, textArea);
        splitPane.setDividerPositions(0.75);

        stage.setTitle("Air Traffic Simulator");
        var contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        var scene = new Scene(contentPane, 1200, 1000);
        stage.setScene(scene);
        stage.show();
    }
}