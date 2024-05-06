import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseBatchInsertComparison extends Application {

    private Connection connection;
    private final String url = "";
    private final String user = "";
    private final String password = "";

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label statusLabel = new Label();

        Button connectButton = new Button("Connect to Database");
        connectButton.setOnAction(e -> {
            DBConnectionPanel connectionPanel = new DBConnectionPanel();
            connectionPanel.showAndWait();
            if (connectionPanel.isConnected()) {
                connection = connectionPanel.getConnection();
                statusLabel.setText("Connected to database.");
            }
        });

        Button insertButton = new Button("Insert Records");
        insertButton.setOnAction(e -> {
            if (connection != null) {
                try {
                    long startTimeWithoutBatch = System.currentTimeMillis();
                    insertRecordsWithoutBatch();
                    long endTimeWithoutBatch = System.currentTimeMillis();
                    long timeTakenWithoutBatch = endTimeWithoutBatch - startTimeWithoutBatch;

                    long startTimeWithBatch = System.currentTimeMillis();
                    insertRecordsWithBatch();
                    long endTimeWithBatch = System.currentTimeMillis();
                    long timeTakenWithBatch = endTimeWithBatch - startTimeWithBatch;

                    statusLabel.setText("Time taken without batch: " + timeTakenWithoutBatch + " ms\n" +
                            "Time taken with batch: " + timeTakenWithBatch + " ms");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                statusLabel.setText("Please connect to the database first.");
            }
        });

        root.getChildren().addAll(connectButton, insertButton, statusLabel);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Database Batch Insert Comparison");
        primaryStage.show();
    }

    private void insertRecordsWithoutBatch() throws SQLException {
        Random random = new Random();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)");
        for (int i = 0; i < 1000; i++) {
            statement.setDouble(1, random.nextDouble());
            statement.setDouble(2, random.nextDouble());
            statement.setDouble(3, random.nextDouble());
            statement.executeUpdate();
        }
        statement.close();
    }

    private void insertRecordsWithBatch() throws SQLException {
        Random random = new Random();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)");
        for (int i = 0; i < 1000; i++) {
            statement.setDouble(1, random.nextDouble());
            statement.setDouble(2, random.nextDouble());
            statement.setDouble(3, random.nextDouble());
            statement.addBatch();
        }
        statement.executeBatch();
        statement.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
