package com.kossine.opendmcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.kossine.opendmcontroller.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.net.telnet.TelnetClient;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private EditText address;
    private Button[] inputs = new Button[16];
    private int selected_input;
    private boolean[] selected_outputs = new boolean[16];
    private boolean[] selected_routes = new boolean[3];
    private Button[] outputs = new Button[16];
    private Button connect;
    private Button send;
    private Button audio;
    private Button video;
    private Button usb;
    private Socket connection;
    private boolean connected;
    private CompletableFuture exec = new CompletableFuture();
    private TelnetClient telnet = new TelnetClient();
    private PrintWriter out;
    private BufferedReader in;
    /*private String[] hexColors = {
            "#FF0000", // Red
            "#800000", // Maroon
            "#A0522D", // Sienna
            "#D2691E",  // Chocolate
            "#00FF00", // Lime
            "#008000", // Green
            "#808000", // Olive
            "#00FFFF", // Cyan
            "#0000FF", // Blue
            "#008080", // Teal
            "#000080", // Navy
            "#FFFF00", // Yellow
            "#FF00FF", // Magenta
            "#800080", // Purple
            "#2b2b2b", // Dark Grey
            "#000000", // Black
    };*/
    /*private String[] darkHexColors = {
            "#1A237E", "#283593", "#303F9F", "#3F51B5",
            "#5C6BC0", "#7986CB", "#9FA8DA", "#C5CAE9",
            "#0D47A1", "#1565C0", "#1976D2", "#1E88E5",
            "#2196F3", "#42A5F5", "#64B5F6", "#90CAF9"
    };*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        InitAddress();
        InitButtons();

    }

    private void InitButtons(){
        inputs[0] = findViewById(R.id.input_button0);
        inputs[1] = findViewById(R.id.input_button1);
        inputs[2] = findViewById(R.id.input_button2);
        inputs[3] = findViewById(R.id.input_button3);
        inputs[4] = findViewById(R.id.input_button4);
        inputs[5] = findViewById(R.id.input_button5);
        inputs[6] = findViewById(R.id.input_button6);
        inputs[7] = findViewById(R.id.input_button7);
        inputs[8] = findViewById(R.id.input_button8);
        inputs[9] = findViewById(R.id.input_button9);
        inputs[10] = findViewById(R.id.input_button10);
        inputs[11] = findViewById(R.id.input_button11);
        inputs[12] = findViewById(R.id.input_button12);
        inputs[13] = findViewById(R.id.input_button13);
        inputs[14] = findViewById(R.id.input_button14);
        inputs[15] = findViewById(R.id.input_button15);

        outputs[0] = findViewById(R.id.output_button0);
        outputs[1] = findViewById(R.id.output_button1);
        outputs[2] = findViewById(R.id.output_button2);
        outputs[3] = findViewById(R.id.output_button3);
        outputs[4] = findViewById(R.id.output_button4);
        outputs[5] = findViewById(R.id.output_button5);
        outputs[6] = findViewById(R.id.output_button6);
        outputs[7] = findViewById(R.id.output_button7);
        outputs[8] = findViewById(R.id.output_button8);
        outputs[9] = findViewById(R.id.output_button9);
        outputs[10] = findViewById(R.id.output_button10);
        outputs[11] = findViewById(R.id.output_button11);
        outputs[12] = findViewById(R.id.output_button12);
        outputs[13] = findViewById(R.id.output_button13);
        outputs[14] = findViewById(R.id.output_button14);
        outputs[15] = findViewById(R.id.output_button15);

        connect = findViewById(R.id.connect_button);
        send = findViewById(R.id.send_button);
        audio = findViewById(R.id.audio_button);
        video = findViewById(R.id.video_button);
        usb = findViewById(R.id.usb_button);

        selected_input = 0;
        for (int loopTimes = 0; loopTimes < 16; loopTimes++) {
            //inputs[loopTimes].setBackgroundColor(Color.parseColor(hexColors[loopTimes]));
            int index = loopTimes + 1;
            inputs[loopTimes].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    for(Button input : inputs){
                        input.setBackgroundColor(Color.parseColor("#505050"));
                    }
                    if(selected_input != index){
                        view.setBackgroundColor(Color.parseColor("#FF0000"));
                        selected_input = index;
                    }
                    else{
                        view.setBackgroundColor(Color.parseColor("#505050"));
                        selected_input = 0;
                    }
                }

            });
        }

        for(boolean active : selected_outputs){active = false;}
        for (int loopTimes = 0; loopTimes < 16; loopTimes++) {
            //inputs[loopTimes].setBackgroundColor(Color.parseColor(hexColors[loopTimes]));
            int index = loopTimes;
            outputs[loopTimes].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(selected_outputs[index] == false){
                        selected_outputs[index] = true;
                        view.setBackgroundColor(Color.parseColor("#FF0000"));

                    }
                    else{
                        selected_outputs[index] = false;
                        view.setBackgroundColor(Color.parseColor("#505050"));
                    }

                }
            });
        }

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               SendIt();
            }
        });

        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Connect(true);
            }
        });

        selected_routes[0] = true;
        video.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(selected_routes[0] == false){
                    selected_routes[0] = true;
                    view.setBackgroundColor(Color.parseColor("#FF0000"));

                }
                    else{
                    selected_routes[0] = false;
                    view.setBackgroundColor(Color.parseColor("#505050"));
                }
            }
        });

        selected_routes[1] = true;
        audio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(selected_routes[1] == false){
                    selected_routes[1] = true;
                    view.setBackgroundColor(Color.parseColor("#FF0000"));

                }
                else{
                    selected_routes[1] = false;
                    view.setBackgroundColor(Color.parseColor("#505050"));
                }
            }
        });

        selected_routes[2] = true;
        usb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(selected_routes[2] == false){
                    selected_routes[2] = true;
                    view.setBackgroundColor(Color.parseColor("#FF0000"));

                }
                else{
                    selected_routes[2] = false;
                    view.setBackgroundColor(Color.parseColor("#505050"));
                }
            }
        });
    }

    private void InitAddress(){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        address = findViewById(R.id.entered_address);
        address.setText(sharedPref.getString("ADDRESS", null));
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("ADDRESS", address.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                editor.putString("ADDRESS", address.getText().toString());
                editor.commit();

            }
        });

    }

    private void SendIt(){
        exec.cancel(true);
        if(!connected){
            Connect(false);
        }
        exec.runAsync(() -> {
            String[] commands = BuildCommands();
            out.println("shortmessage " + "Executing routing command(s) from " + android.os.Build.MODEL);
            for(String command : commands){
                out.println(command);
            }
            out.println("BYE");
            connected = false;
            //connect.setText("Test");
            //connect.setBackgroundColor(Color.parseColor("#505050"));
            //String test = "shortmessage " + android.os.Build.MODEL + " connected";
            //out.println(test);
            //telnet.disconnect();
        });
    }

    private String[] BuildCommands(){
        int commandCount = 0;
        int commandMultiplier = 3;
        boolean noRoutes = false;
        if(selected_routes[0] == false){
            commandMultiplier--;
        }
        if(selected_routes[1] == false){
            commandMultiplier--;
        }
        if(selected_routes[2] == false){
            commandMultiplier--;
        }
        for(boolean active : selected_outputs){
            if(active){commandCount++;}
        }
        commandCount *= commandMultiplier;
        String[] commands = new String[commandCount];
        //if(selected_routes[])
        for(int loopTimes = 0, commandIndex = 0; loopTimes < 16; loopTimes++){
            if(selected_outputs[loopTimes]){
                if(selected_routes[0] == true){
                    commands[commandIndex] = "setvideoroute " + selected_input + " " + (loopTimes + 17);
                    commandIndex++;
                }
                if(selected_routes[1] == true){
                    commands[commandIndex] = "setaudioroute " + selected_input + " " + (loopTimes + 17);
                    commandIndex++;
                }
                if(selected_routes[2] == true){
                    commands[commandIndex] = "setusbroute " + selected_input + " " + (loopTimes + 17);
                    commandIndex++;
                }

            }
        }

        return commands;
    }

    private void Connect(boolean fromTest){
        exec.cancel(true);
        String host = address.getText().toString();
        int port = 23;
        //PrintWriter out;
        //BufferedReader in;
        exec.runAsync(() -> {
            try {
                telnet.connect(host, port);
                //connection = new Socket(host, port);
                out = new PrintWriter(telnet.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(telnet.getInputStream()));
                if(fromTest) {
                    String test = "shortmessage " + android.os.Build.MODEL + " Connected";
                    out.println(test);
                }
                //telnet.disconnect();
                connected = true;
            } catch (IOException e) {
                connected = false;
                System.err.println("Error connecting over Telnet to " + host + " over port " + port + ":" + e.getMessage());
                Toast errorToast = Toast.makeText(MainActivity.this, "Error connecting over Telnet to " + host + " over port " + port + ":" + e.getMessage(), Toast.LENGTH_LONG);
                errorToast.show();
            }
        });
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(connected) {
            connect.setText("Connected");
            connect.setBackgroundColor(Color.parseColor("#00FF00"));
        }
        else{
            connect.setText("Error");
            connect.setBackgroundColor(Color.parseColor("#FF0000"));
        }

    }
}

