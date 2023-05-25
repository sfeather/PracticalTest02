package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String key;
    private final String value;

    private final String informationType;
    private final TextView dataTextView;

    private Socket socket;

    public ClientThread(String address, int port, String key, String value, String informationType, TextView dataTextView) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.value = value;
        this.informationType = informationType;
        this.dataTextView = dataTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            if (informationType.compareTo("get") == 0) {
                printWriter.println(informationType);
                printWriter.flush();
                printWriter.println(key);
                printWriter.flush();
            } else if (informationType.compareTo("put") == 0) {
                printWriter.println(informationType);
                printWriter.flush();
                printWriter.println(key);
                printWriter.flush();
                printWriter.println(value);
                printWriter.flush();
            }

            String dataInformation;

            // reads the data from the server
            while ((dataInformation = bufferedReader.readLine()) != null) {
                final String finalizedDataInformation = dataInformation;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                dataTextView.post(() -> dataTextView.setText(finalizedDataInformation));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}

