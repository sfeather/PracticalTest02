package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        setContentView(R.layout.activity_practical_test02_main);
    }
}