package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText keyEditText;
    private EditText valueEditText;
    private Spinner informationTypeSpinner;
    private Button getDataButton;

    private ServerThread serverThread;

    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        connectButton = findViewById(R.id.connect_button);

        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        informationTypeSpinner = findViewById(R.id.information_type_spinner);
        getDataButton = findViewById(R.id.get_data_button);

        keyEditText = findViewById(R.id.key_edit_text);
        valueEditText = findViewById(R.id.value_edit_text);

        dataTextView = findViewById(R.id.data_text_view);

        connectButton.setOnClickListener(it -> {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        });

        getDataButton.setOnClickListener(it -> {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String key = keyEditText.getText().toString();
            String value = valueEditText.getText().toString();

            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (key.isEmpty() || informationType.isEmpty() || value.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (key / value / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), key, value, informationType, dataTextView);
            clientThread.start();
        });
    }
}