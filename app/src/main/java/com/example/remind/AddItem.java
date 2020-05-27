package com.example.remind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddItem extends AppCompatActivity {

    public static ArrayList<Data> dataArrayList;
    public static ArrayList<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        LoadData();
        itemList = new ArrayList<>();

        for(Data data:dataArrayList){
            itemList.add(data.getItem());
        }

        EditText item_name = (EditText) findViewById(R.id.item_name);
        EditText item_location = (EditText) findViewById(R.id.item_location);
        Button save_item = (Button) findViewById(R.id.save_item);

        save_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = item_name.getText().toString().trim();
                String location = item_location.getText().toString().trim();
                if(!item.isEmpty() && !location.isEmpty()){
                    if(itemList.contains(item)){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddItem.this);
                        alertDialog.setMessage("Item already exists");
                        alertDialog.setPositiveButton("Add another copy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HomeFragment.insertItem(item.toLowerCase(),location.toLowerCase(),getApplicationContext());
                                onBackPressed();
                            }
                        }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                    }
                    else {
                        HomeFragment.insertItem(item.toLowerCase(),location.toLowerCase(),getApplicationContext());
                        onBackPressed();
                    }
                }
                else{
                    Toast.makeText(AddItem.this, "enter the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Data>>() {}.getType();
        dataArrayList = gson.fromJson(json, type);
        Log.d("TAG", "LoadData: "+dataArrayList);

        if (dataArrayList == null) {
            dataArrayList = new ArrayList<>();
        }
    }
}
