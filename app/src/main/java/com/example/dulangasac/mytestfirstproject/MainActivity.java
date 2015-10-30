package com.example.dulangasac.mytestfirstproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dulangasac.mytestfirstproject.Utils.Values;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    public static DatagramSocket ds;
    private Button btn_add;
    private Button btn_check;
    private int count;
    private Spinner selectIndex;

    Thread m_objThreadClient;
    Socket clientSocket;
    ObjectOutputStream oos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        setTitle("Soft Clicker");

        btn_add =  (Button) findViewById(R.id.btn_add_student);
        btn_check = (Button) findViewById(R.id.btn_check_for_broadcast);
        selectIndex =  (Spinner) findViewById(R.id.spinner_select_index);

        SharedPreferences appSP = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        String stuCount = appSP.getString("count", null);
        ArrayList indexList = new ArrayList<String>();

        if(stuCount!=null){
            count = Integer.parseInt(stuCount);
            for(int i=1;i<=count;i++){
                indexList.add(appSP.getString("index"+i,null));
            }
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item);
        spinAdapter.addAll(indexList);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectIndex.setAdapter(spinAdapter);
        System.out.println("");

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToService();
            }
        });

    }

    public void addStudent(){

        SharedPreferences appSP = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSP.edit();
        if(count>=3){
            editor.putString("firstRun","more");
            editor.commit();
            Intent mainIntent = new Intent(MainActivity.this, InitialActivity.class);
            startActivity(mainIntent);
            finish();
            //not allowed
//            Toast.makeText(MainActivity.this, "No more students allowed", Toast.LENGTH_SHORT).show();
        }else if(count<3){
            //allowed

            editor.putString("firstRun","Yes");
            editor.commit();
            Intent mainIntent = new Intent(MainActivity.this, InitialActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    public void receiveBroadcast()  {
        System.out.println("for quitting client press ctrl+c");
        try {
            ds = new DatagramSocket(8080);
        } catch (Exception ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        m_objThreadClient = new Thread( new Runnable(){
            public void run()
            {
                byte buffer[] = new byte[1024];
                try {

                    while (true) {
                        DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                        ds.receive(p);
                        String ip_port = new String(p.getData(), 0, p.getLength());
                        String[] parts = ip_port.split(",");
                        String ip = parts[1];
                        String port = parts[2];
                        String questionNum=parts[3];
                        Values.SERVER_IP = ip;
                        Values.PORT_NUMBER = port;
                        Values.QUESTION_NUMBER=questionNum;
                        ds.close();

                        if (Values.SERVER_IP!=""&& Values.PORT_NUMBER!="") {
                            break;
                        }
                    }
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        });
        //Network code goes here, send response from client to server
        m_objThreadClient.start();
    }

    public void listenToService(){
        receiveBroadcast();
        if(Values.SERVER_IP!=""&& Values.PORT_NUMBER!=""){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("New Quiz");
            alertDialogBuilder
                    .setMessage("New Quiz detected")
                    .setCancelable(false)
                    .setPositiveButton("Go",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            //go to quiz answer screen
                            Intent mainIntent = new Intent(MainActivity.this, AnswerActivity.class);
                            mainIntent.putExtra("index",selectIndex.getSelectedItem().toString());
                            startActivity(mainIntent);
//                        finish();

                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            tryAgain();
        }
    }

    public void tryAgain(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Connection Fail");
        alertDialogBuilder
                .setMessage("Server Not Detected")
                .setCancelable(false)
                .setPositiveButton("Try again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //go to quiz answer screen
                        receiveBroadcast();
                        dialog.dismiss();
//                        finish();

                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
