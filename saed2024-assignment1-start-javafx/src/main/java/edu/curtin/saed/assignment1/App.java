package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.application.Platform;
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
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 10;
    private int startCount = 0;
    private int endCount = 0;
    private int inFlightCount = 0;
    private int undergoingServiceCount = 0;
    private int completedFlightsCount = 0;
    private AirportManager airportManager = new AirportManager(); // initializing an airport manager instance to be used by all classes
    private TextArea textArea; // had to promote textArea and statusArea to class variables
    private Label statusText;
    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage)
    {
        // Set up the main "top-down" display area. This is an example only, and you should
        // change this to set it up as you require.

        GridArea area = new GridArea(GRID_WIDTH, GRID_HEIGHT);
        area.setGridLines(false); // If desired
        area.setStyle("-fx-background-color: #73c8a1;");

        Random random = new Random();
        for (int i = 0; i < 10; i++)
        {
            int x = random.nextInt(GRID_WIDTH);
            int y = random.nextInt(GRID_HEIGHT);
            String airportName = "Airport " + (i + 1);
            int airportId = i + 1;

            // creating airports (default 10)
            // airport creation also handles service queue & available queue set-up
            Airport newAirport = new Airport(airportId, x, y, this);
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
                Plane plane = new Plane(planeId, x, y, newAirport, area, this);
                newAirport.receivePlane(plane);
            }
        }
    

        // Set up other key parts of the user interface. You'll also want to adjust this.
        var startBtn = new Button("Start");
        var endBtn = new Button("End");

        startBtn.setOnAction((event) ->
        {
            if(startCount == 0) // ensuring flight request process is only started once
            {
                airportManager.startFlightRequests(); // using airport manager to start a flight request proccess for each created airport
            }
            System.out.println("Start button pressed");
            startCount++;
        });
        endBtn.setOnAction((event) ->
        {
            airportManager.shutdown();
            System.out.println("End button pressed");
            endCount++;
        });
        stage.setOnCloseRequest((event) ->
        {
            if(endCount == 0) // if threads are already shutdown, no need to do it again
            {
                airportManager.shutdown(); 
            }
            System.out.println("Close button pressed");

            // ## FOR DEBUGGING THREAD TERMINATION ## //
            // Set<Thread> threads = Thread.getAllStackTraces().keySet();
            // System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
            // for (Thread t : threads) {
            // System.out.printf("%-15s \t %-15s \t %-15d \t %s\n", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
            // }
        });
        statusText = new Label("In Flight: 0 | Undergoing Service: 0 | Completed Trips: 0"); // TO DO: update status here
        textArea = new TextArea(); // TO DO: put simulation updates here
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

    // methods to increment/decrement counts, and update the status text
    public void incrementInFlightCount() {
        inFlightCount++;
        updateStatusText();
    }

    public void incrementUndergoingServiceCount() {
        undergoingServiceCount++;
        updateStatusText();
    }

    public void incrementCompletedFlightsCount() {
        completedFlightsCount++;
        updateStatusText();
    }

    public void decrementInFlightCount() {
        inFlightCount--;
        updateStatusText();
    }

    public void decrementUndergoingServiceCount() {
        undergoingServiceCount--;
        updateStatusText();
    }

    // update flight statuses to the UI
    public void addUpdate(String update)
    {
        textArea.appendText(update + "\n");
    }

    // update status text to reflect current counts
    private void updateStatusText() 
    {
        Platform.runLater(() -> {statusText.setText("In Flight: " + inFlightCount + " | Undergoing Service: " + undergoingServiceCount + " | Completed Trips: " + completedFlightsCount);});
    }
}