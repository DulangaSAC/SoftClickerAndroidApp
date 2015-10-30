package com.example.dulangasac.mytestfirstproject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulangasac.mytestfirstproject.Utils.Values;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AnswerActivity extends AppCompatActivity {


    private Button ansA;
    private Button ansB;
    private Button ansC;
    private Button ansD;
    private Button ansE;
    private Button submit;
    private Button back;
    private ObjectOutputStream oos;
    private ObjectInputStream  ois;
    private String answer;
    private String sendAnswer;
    private String macAddress;
    private String aCK;
    private Socket clientSocket;
    private Thread m_objThreadClient;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_answer_activity);
        final String index = getIntent().getExtras().getString("index");
        initView();
        title.setText("Answering for question " +Values.QUESTION_NUMBER+" as "+index);
       // Log.v("NNN",question_number.)
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        macAddress = info.getMacAddress();

        ansA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ansA.setBackgroundResource(R.drawable.btn_selected);
                setBg("1");
                setPadding();
                answer = "1";
            }
        });

        ansB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ansB.setBackgroundResource(R.drawable.btn_selected);
                setBg("2");
                setPadding();
                answer = "2";
            }
        });

        ansC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ansC.setBackgroundResource(R.drawable.btn_selected);
                setBg("3");
                setPadding();
                answer = "3";
            }
        });

        ansD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ansD.setBackgroundResource(R.drawable.btn_selected);
                setBg("4");
                setPadding();
                answer = "4";
            }
        });

        ansE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ansE.setBackgroundResource(R.drawable.btn_selected);
                setBg("5");
                setPadding();
                answer = "5";
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_objThreadClient = new Thread( new Runnable(){
                    public void run()
                    {
                        try {
                            clientSocket = new Socket(Values.SERVER_IP, Integer.parseInt(Values.PORT_NUMBER));
                            oos = new ObjectOutputStream(clientSocket.getOutputStream());
                            sendAnswer = macAddress+","+index+ "," + Values.QUESTION_NUMBER + "," + answer;
                            oos.writeObject(sendAnswer);
                            ois=new ObjectInputStream(clientSocket.getInputStream());
                            try {
                                aCK=(String) ois.readObject();
                                System.out.println("FromServer :"+aCK);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally{
                            if (clientSocket != null){
                                try {
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (oos != null){
                                try {
                                    oos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (ois != null){
                                try {
                                    ois.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                //Network code goes here, send response from client to server
                m_objThreadClient.start();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initView(){
        title = (TextView)findViewById(R.id.textView);
        ansA = (Button)findViewById(R.id.ansA);
        ansB = (Button)findViewById(R.id.ansB);
        ansC = (Button)findViewById(R.id.ansC);
        ansD = (Button)findViewById(R.id.ansD);
        ansE = (Button)findViewById(R.id.ansE);
        submit = (Button)findViewById(R.id.submit);
        back = (Button)findViewById(R.id.back);
        ansA.setBackgroundResource(R.drawable.btn_unselected);
        ansB.setBackgroundResource(R.drawable.btn_unselected);
        ansC.setBackgroundResource(R.drawable.btn_unselected);
        ansD.setBackgroundResource(R.drawable.btn_unselected);
        ansE.setBackgroundResource(R.drawable.btn_unselected);
    }

    public void setPadding(){
        ansA.setPadding(0, 60, 0, 60);
        ansB.setPadding(0, 60, 0, 60);
        ansC.setPadding(0, 60, 0, 60);
        ansD.setPadding(0, 60, 0, 60);
        ansE.setPadding(0, 60, 0, 60);
    }

    public void setBg(String ans){
        if(ans.equals("A")){
            ansB.setBackgroundResource(R.drawable.btn_unselected);
            ansB.setClickable(false);
            ansC.setBackgroundResource(R.drawable.btn_unselected);
            ansC.setClickable(false);
            ansD.setBackgroundResource(R.drawable.btn_unselected);
            ansD.setClickable(false);
            ansE.setBackgroundResource(R.drawable.btn_unselected);
            ansE.setClickable(false);
        }else if(ans.equals("B")){
            ansA.setBackgroundResource(R.drawable.btn_unselected);
            ansA.setClickable(false);
            ansC.setBackgroundResource(R.drawable.btn_unselected);
            ansC.setClickable(false);
            ansD.setBackgroundResource(R.drawable.btn_unselected);
            ansD.setClickable(false);
            ansE.setBackgroundResource(R.drawable.btn_unselected);
            ansE.setClickable(false);
        }else if(ans.equals("C")){
            ansA.setBackgroundResource(R.drawable.btn_unselected);
            ansA.setClickable(false);
            ansB.setBackgroundResource(R.drawable.btn_unselected);
            ansB.setClickable(false);
            ansD.setBackgroundResource(R.drawable.btn_unselected);
            ansD.setClickable(false);
            ansE.setBackgroundResource(R.drawable.btn_unselected);
            ansE.setClickable(false);
        }else if(ans.equals("D")){
            ansA.setBackgroundResource(R.drawable.btn_unselected);
            ansA.setClickable(false);
            ansB.setBackgroundResource(R.drawable.btn_unselected);
            ansB.setClickable(false);
            ansC.setBackgroundResource(R.drawable.btn_unselected);
            ansC.setClickable(false);
            ansE.setBackgroundResource(R.drawable.btn_unselected);
            ansE.setClickable(false);
        }else if(ans.equals("E")){
            ansA.setBackgroundResource(R.drawable.btn_unselected);
            ansA.setClickable(false);
            ansB.setBackgroundResource(R.drawable.btn_unselected);
            ansB.setClickable(false);
            ansC.setBackgroundResource(R.drawable.btn_unselected);
            ansC.setClickable(false);
            ansD.setBackgroundResource(R.drawable.btn_unselected);
            ansD.setClickable(false);
        }
    }

}
