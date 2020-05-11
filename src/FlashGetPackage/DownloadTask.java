package FlashGetPackage;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class for each download tasks to download the file that start from a given position and to the end position.
 */
class DownloadTask extends Task<Long> {
    private URL urlLink;
    private Long bytesRead;
    private File outputFile;
    private Long size;
    private Long start;

    /**
     * initialize the DownloadTask with parameter urlLink, output, start, and size.
     *
     * @param urlLink is the link for downloading the file.
     * @param output  is the output file.
     * @param start   is position to start download from.
     * @param size    is the position to download to.
     */
    public DownloadTask(URL urlLink, File output, Long start, Long size) {
        this.urlLink = urlLink;
        this.outputFile = output;
        this.bytesRead = 0L;
        this.size = size;
        this.start = start;
    }

    /**
     * Method for downloading the file from a given range and return the bytesRead.
     *
     * @return the bytes that have been read.
     */
    @Override
    public Long call() {
        final int BUFFERSIZE = 16 * 1024;
//        updateProgress(bytesRead, size);

        URLConnection conn = null;
        try {
            conn = urlLink.openConnection();
        } catch (IOException e) {
            alertBox("Can't connect to URL.");
        }

        String range = null;
        if (size > 0) {
            range = String.format("bytes=%d-%d", start, start + size - 1);
        } else {
            // size not given, so read from start byte to end of file
            range = String.format("bytes=%d-", start);
        }
        conn.setRequestProperty("Range", range);


        InputStream in = null;
        try {
            in = conn.getInputStream();
        } catch (IOException e) {
            alertBox("Can't read file.");
        }


        File file = outputFile;
        // Create a random access file for synchronous output ("rwd" flags)
        RandomAccessFile writer = null;
        try {
            writer = new RandomAccessFile(file, "rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // seek to location (in bytes) to start writing to
        try {
            writer.seek(start);
        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] buffer = new byte[BUFFERSIZE];
        try {
            do {
                int n = in.read(buffer);
                if (n < 0) break; // n < 0 means end of the input
                writer.write(buffer, 0, n); // write n bytes from buffer
                bytesRead += n;
                updateValue(bytesRead);
                updateProgress(bytesRead, size);

                if (isCancelled()) {
                    break;
                }
            } while (bytesRead < size);
        } catch (IOException ex) {
            alertBox("Can't write to file");

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                alertBox("Can't close the input");
                ;
            }
            try {
                writer.close();
            } catch (IOException e) {
                alertBox("Can't close the output");
                ;
            }
        }
        return bytesRead;
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
}

