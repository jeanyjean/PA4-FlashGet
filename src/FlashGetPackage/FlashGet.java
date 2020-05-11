package FlashGetPackage;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FlashGet app for downloading files using multiple threads.
 *
 * @author Purich Trainorapong
 */
public class FlashGet extends Application {
    private TextField urlField;
    private Label labelName;
    private Label labelProgress;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;
    private ProgressBar progressBar5;
    private ProgressBar progressBar6;
    private Button cancelButton;
    private FlowPane secondBox;
    private FlowPane thirdBox;
    private String urlName;
    private DownloadTask downloadTaskClass;
    private DownloadTask downloadTaskClass2;
    private DownloadTask downloadTaskClass3;
    private DownloadTask downloadTaskClass4;
    private DownloadTask downloadTaskClass5;
    private URL url;
    private VBox root;
    private File directory = new File(System.getProperty("user.home") + "/Desktop");
    private String extension;
    private boolean validURL = false;
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private long progressValue = 0L;

    /**
     * Start method for the app. Make a vbox and add 3 flowpanne to it.
     *
     * @param stage is the stage for the app.
     */
    @Override
    public void start(Stage stage) {
        FlowPane firstBox = initComponents1();
        secondBox = initComponents2();
        secondBox.setVisible(false);
        thirdBox = initComponents3();
        thirdBox.setVisible(false);
        root = new VBox();
        root.getChildren().addAll(firstBox, secondBox, thirdBox);


        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(180);
        stage.setWidth(650);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("cloud.png")));
        stage.setTitle("Multi-threaded file downloader");
        stage.show();
    }

    /**
     * initialize and add components to the first FlowPane.
     *
     * @return the first FlowPane.
     */
    private FlowPane initComponents1() {
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(10.0));
        flowPane.setHgap(10.0);

        Label labelURL = new Label("URL to Download");
        urlField = new TextField();
        Button downloadButton = new Button("Download");
        Button clearButton = new Button("Clear");
        downloadButton.setOnAction(new DownloadHandler());
        clearButton.setOnAction(new ClearHandler());

        flowPane.getChildren().addAll(labelURL, urlField, downloadButton, clearButton);
        return flowPane;
    }

    /**
     * initialize and add components to the second FlowPane.
     *
     * @return the second FlowPane.
     */
    private FlowPane initComponents2() {
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(10.0));
        flowPane.setHgap(10.0);

        labelName = new Label("");
        progressBar = new ProgressBar();
        labelProgress = new Label("");
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new CancelHandler());
        progressBar.setPrefWidth(200);
        progressBar.setProgress(0.0);

        flowPane.getChildren().addAll(labelName, progressBar, labelProgress, cancelButton);
        return flowPane;
    }

    /**
     * initialize and add components to the third FlowPane.
     *
     * @return the third FlowPane.
     */
    private FlowPane initComponents3() {
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPadding(new Insets(10.0));
        flowPane.setHgap(10.0);

        Label labelThreads = new Label("Threads:");
        progressBar2 = new ProgressBar();
        progressBar3 = new ProgressBar();
        progressBar4 = new ProgressBar();
        progressBar5 = new ProgressBar();
        progressBar6 = new ProgressBar();
        progressBar2.setProgress(0.0);
        progressBar3.setProgress(0.0);
        progressBar4.setProgress(0.0);
        progressBar5.setProgress(0.0);
        progressBar6.setProgress(0.0);

        flowPane.getChildren().addAll(labelThreads, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6);
        return flowPane;
    }

    /**
     * Class for clearing the text box when an event happens.
     */
    class ClearHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            urlField.setText("");
        }
    }

    /**
     * Class for canceling all the threads.
     */
    class CancelHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                downloadTaskClass.cancel();
                downloadTaskClass2.cancel();
                downloadTaskClass3.cancel();
                downloadTaskClass4.cancel();
                downloadTaskClass5.cancel();
                alertBox("Download Canceled!");
            } catch (NullPointerException a) {
                alertBox("Download is completed or canceled!");
            }
        }
    }

    /**
     * Class for downloading the file using multiple threads.
     */
    class DownloadHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            progressValue = 0;
            String[] link = urlField.getText().split("/");
            String progress = progressValue + "/" + getUrlLength();
            while (validURL) {
                secondBox.setVisible(true);
                thirdBox.setVisible(true);
                labelName.setText(link[link.length - 1]);
                labelProgress.setText(progress);


                FileChooser fileChooser = new FileChooser();
                Stage stage2 = (Stage) root.getScene().getWindow();
                int indexExtension = link[link.length - 1].lastIndexOf(".");
                try {
                    extension = link[link.length - 1].substring(indexExtension);
                } catch (StringIndexOutOfBoundsException s) {
                    alertBox("Can't find file's type");
                    break;
                }


                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Type", "*" + extension));
                fileChooser.setInitialFileName(link[link.length - 1]);
                fileChooser.setInitialDirectory(directory);


                File selectedFile = fileChooser.showSaveDialog(stage2);
                if (selectedFile != null) {
                    directory = selectedFile.getParentFile();
                }

                try {
                    url = new URL(urlField.getText());
                } catch (MalformedURLException e) {
                    alertBox("Can't access URL or URL don't exist");
                }

                ChangeListener<Long> valueListener = new ChangeListener<Long>() {
                    @Override
                    public void changed(ObservableValue<? extends Long> observableValue, Long integer, Long t1) {
                        if (integer == null) {
                            integer = 0L;
                        }
                        progressValue += (t1 - integer);
                        labelProgress.setText(progressValue + "/" + getUrlLength());
                    }
                };

                Long chunk = (long) Math.ceil((double) (getUrlLength() / 16384));
                Long part = ((chunk / 2) * 16384);

//                executor = Executors.newFixedThreadPool(5);

                if (getUrlLength() <= 300000) {
                    progressBar2.setPrefWidth(250);
                    progressBar3.setPrefWidth(250);
                    progressBar4.setVisible(false);
                    progressBar5.setVisible(false);
                    progressBar6.setVisible(false);

                    downloadTaskClass = new DownloadTask(url, selectedFile, 0L, part);
                    downloadTaskClass2 = new DownloadTask(url, selectedFile, part, getUrlLength() - part);

                    progressBar.progressProperty().bind(downloadTaskClass.progressProperty().multiply(0.5).add(downloadTaskClass2.progressProperty().multiply(0.5)));
                    progressBar2.progressProperty().bind(downloadTaskClass.progressProperty());
                    progressBar3.progressProperty().bind(downloadTaskClass2.progressProperty());

                    executor.execute(downloadTaskClass);
                    executor.execute(downloadTaskClass2);

                    downloadTaskClass.valueProperty().addListener(valueListener);
                    downloadTaskClass2.valueProperty().addListener(valueListener);
                } else {
                    progressBar2.setPrefWidth(100);
                    progressBar3.setPrefWidth(100);
                    progressBar4.setVisible(true);
                    progressBar5.setVisible(true);
                    progressBar6.setVisible(true);
                    part = ((chunk / 5) * 16384);
                    downloadTaskClass = new DownloadTask(url, selectedFile, 0L, part);
                    downloadTaskClass2 = new DownloadTask(url, selectedFile, part, part);
                    downloadTaskClass3 = new DownloadTask(url, selectedFile, part * 2, part);
                    downloadTaskClass4 = new DownloadTask(url, selectedFile, part * 3, part);
                    downloadTaskClass5 = new DownloadTask(url, selectedFile, part * 4, (getUrlLength() - (part * 4)));


                    progressBar.progressProperty().bind(downloadTaskClass.progressProperty().multiply(0.2).add(downloadTaskClass2.progressProperty().multiply(0.2).add(downloadTaskClass3.progressProperty().multiply(0.2).add(downloadTaskClass4.progressProperty().multiply(0.2).add(downloadTaskClass5.progressProperty().multiply(0.2))))));
                    progressBar2.progressProperty().bind(downloadTaskClass.progressProperty());
                    progressBar3.progressProperty().bind(downloadTaskClass2.progressProperty());
                    progressBar4.progressProperty().bind(downloadTaskClass3.progressProperty());
                    progressBar5.progressProperty().bind(downloadTaskClass4.progressProperty());
                    progressBar6.progressProperty().bind(downloadTaskClass5.progressProperty());


                    executor.execute(downloadTaskClass);
                    executor.execute(downloadTaskClass2);
                    executor.execute(downloadTaskClass3);
                    executor.execute(downloadTaskClass4);
                    executor.execute(downloadTaskClass5);


                    downloadTaskClass.valueProperty().addListener(valueListener);
                    downloadTaskClass2.valueProperty().addListener(valueListener);
                    downloadTaskClass3.valueProperty().addListener(valueListener);
                    downloadTaskClass4.valueProperty().addListener(valueListener);
                    downloadTaskClass5.valueProperty().addListener(valueListener);
                }
                break;
            }
        }
    }

    /**
     * Class for finding the URL's length or size.
     *
     * @return the URL's size
     */
    private Long getUrlLength() {
        urlName = urlField.getText();
        URL url = null;
        long length = 0;
        try {
            url = new URL(urlName);
            URLConnection connection = url.openConnection();
            length = connection.getContentLengthLong();
        } catch (MalformedURLException ex) {
            alertBox("Can't find the url file's size or wrong url type");
            validURL = false;
        } catch (IOException ioe) {
            alertBox("Can't find the url file's size or wrong url type");
            validURL = false;
        }
        validURL = true;
        return length;
    }

    /**
     * Class for making the AlertBox with the message and show it.
     *
     * @param message is the message to be shown in the AlertBox.
     */
    public void alertBox(String message) {
        Alert alertBox = new Alert(Alert.AlertType.NONE);
        alertBox.setAlertType(Alert.AlertType.ERROR);
        alertBox.setTitle("Error!");
        alertBox.setHeaderText(null);
        alertBox.setContentText(message);
        alertBox.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

