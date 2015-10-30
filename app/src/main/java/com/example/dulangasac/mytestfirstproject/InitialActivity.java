package com.example.dulangasac.mytestfirstproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dulangasac.mytestfirstproject.Utils.Values;

import java.util.ArrayList;


public class InitialActivity extends AppCompatActivity {

    private Button btn_save;
    private Button btn_remove;
    private EditText txt_index;
    private int count;
    private int removeItem;
    private String err_string;
    private Spinner selectIndex;
    private TextView lable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_initial_activity);

        btn_save =  (Button) findViewById(R.id.btn_save);
        btn_remove=  (Button) findViewById(R.id.button_remove);
        lable=(TextView)findViewById(R.id.textView3);
        selectIndex =  (Spinner) findViewById(R.id.spinner_select_index);
        txt_index = (EditText) findViewById(R.id.txt_index);

        btn_remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

//                Intent mainIntent = new Intent(InitialActivity.this, MainActivity.class);
//                startActivity(mainIntent);
//                finish();
            }
        });

        if(firstRun()){
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveIndex();
                    if(err_string.equals("")){
                        Toast.makeText(InitialActivity.this,"Index number saved successfully",Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(InitialActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }else{
                        Toast.makeText(InitialActivity.this,"An error has occured:"+err_string,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Intent mainIntent = new Intent(InitialActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        SharedPreferences appSP = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        String stuCount = appSP.getString("count", null);
        ArrayList indexList = new ArrayList<String>();
        if(stuCount!=null){
            count = Integer.parseInt(stuCount);
            for(int i=1;i<=count;i++){
                indexList.add(appSP.getString("index"+i,null));
            }
        }
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(InitialActivity.this, android.R.layout.simple_spinner_item);
        spinAdapter.addAll(indexList);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectIndex.setAdapter(spinAdapter);

    }

    public boolean firstRun(){
        SharedPreferences appSP = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        String firstRun = appSP.getString("firstRun", null);

        System.out.println("Fuck0"+firstRun);
        if(firstRun != null) {
            System.out.println("Fuck1");
            if(firstRun.equals("No")){
                System.out.println("Fuck2");
                return false;
            }else if(firstRun.equals("more")){
                addMore();
                return true;
            }
            else {
                System.out.println("Fuck3");
                return true;
            }
        }else{
            System.out.println("Fuck4");
            return true;
        }
    }


    public void saveIndex(){
        SharedPreferences appSP = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSP.edit();
        String value = txt_index.getText().toString();
        //first check how many student ids has been saved
        String count = appSP.getString("count",null);
        int studentKey = 1;
        err_string="";

        if(count !=null){
            if(Integer.parseInt(count)==1){
                studentKey=2;
                editor.putString("index"+String.valueOf(studentKey), value);
                editor.putString("count","2");
            }else if(Integer.parseInt(count)==2){
                studentKey=3;
                editor.putString("index"+String.valueOf(studentKey), value);
                editor.putString("count","3");
            }else {
                //err_string="Cannot use by more than 3 students";


            }
        }else{
            editor.putString("index"+String.valueOf(studentKey), value);
            editor.putString("count","1");
        }
        editor.putString("firstRun","No");
        editor.commit();
    }

    public void removeStudent(int item){
        SharedPreferences preferences = getSharedPreferences(Values.PREFS_INDEX, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("index" + (item + 1));
        int count = Integer.parseInt(preferences.getString("count", null));
        editor.putString("count", "" + (count - 1));
        editor.commit();
        lable.setText("Please enter your valid student ID number");
        selectIndex.setVisibility(View.INVISIBLE);
        btn_remove.setVisibility(View.INVISIBLE);
        txt_index.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
        Toast.makeText(InitialActivity.this,"Removed :" +item+" "+count, Toast.LENGTH_SHORT).show();
    }

    public void addMore(){
        lable.setText("Please select & Remove a existing index number");
        txt_index.setVisibility(View.INVISIBLE);
        btn_save.setVisibility(View.INVISIBLE);
        selectIndex.setVisibility(View.VISIBLE);
        btn_remove.setVisibility(View.VISIBLE);
        removeItem=selectIndex.getSelectedItemPosition();
        removeStudent(removeItem);

    }
}
