package com.example.remind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private recyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Data> dataArrayList;

    SharedPreferences prefs,profile;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,container,false);

        FloatingActionButton additem = view.findViewById(R.id.add_button);
        final EditText search_et = view.findViewById(R.id.home_search_et);
        Button search_button = view.findViewById(R.id.home_search_bt);


        prefs = getActivity().getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        profile = getActivity().getSharedPreferences("com.Profile.ReMIND", MODE_PRIVATE);
        String Susername = profile.getString("UserName","");

        TextView greeting = view.findViewById(R.id.greeting);
        greeting.setText("Hi ! "+Susername);

        CircularImageView profilePic = view.findViewById(R.id.home_profile_pic);
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f=new File(directory, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            profilePic.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(), "imamge not found", Toast.LENGTH_SHORT).show();
        }



        LoadData();

        mRecyclerView = view.findViewById(R.id.search_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new recyclerViewAdapter(dataArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new recyclerViewAdapter.OnItemClickedListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
                SaveData(getContext());
            }
            @Override
            public void onFavClick(int position) {
                if(dataArrayList.get(position).getFav()){
                    dataArrayList.get(position).isFav(false);
                    SaveData(getContext());
                    mAdapter.notifyItemChanged(position);
                }
                else{
                    dataArrayList.get(position).isFav(true);
                    SaveData(getContext());
                    mAdapter.notifyItemChanged(position);
                }
            }
            @Override
            public void onEditClick(int position){
                Data d = dataArrayList.get(position);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                final EditText inputItem = new EditText(getContext());
                final EditText inputLocation = new EditText(getContext());
                final TextView inputItemText = new TextView(getContext());
                final TextView inputLocationText = new TextView(getContext());

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                inputItem.setText(d.getItem());
                inputLocation.setText(d.getLocation());
                inputItemText.setText("Item Name");
                inputLocationText.setText("Item Location");

                layout.addView(inputItemText);
                layout.addView(inputItem);
                layout.addView(inputLocationText);
                layout.addView(inputLocation);

                alertDialog.setView(layout);
                alertDialog.setTitle("Edit");

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        d.item=inputItem.getText().toString().toLowerCase();
                        d.location=inputLocation.getText().toString().toLowerCase();
                        dataArrayList.set(position,d);
                        mAdapter.notifyItemChanged(position);
                    }
                });
                alertDialog.show();
            }
        });

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String item_name = search_et.getText().toString().trim();
                search(item_name);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_et.getText().toString().length()>0){
                    String item_name = search_et.getText().toString().trim();
                    search(item_name);
                    Toast.makeText(getContext(), "search success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Enter something to search", Toast.LENGTH_SHORT).show();
                }
            }
        });


        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddItem.class);
                startActivity(i);
            }
        });

        FloatingActionButton mic = view.findViewById(R.id.mic_button);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput(view);
            }
        });

        return view;
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(getContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //textViewResult.setText(result.get(0));
                    NLP("my "+result.get(0).toString().toLowerCase(),dataArrayList.size());
                    //Toast.makeText(getContext(), result.get(0).toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void LoadData(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Data>>() {}.getType();
        dataArrayList = gson.fromJson(json, type);
        Log.d("TAG", "LoadData: "+dataArrayList);

        if (dataArrayList == null) {
            dataArrayList = new ArrayList<>();
        }
    }

    public static void SaveData(Context context){
        SharedPreferences sharedPreferences =  context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dataArrayList);
        editor.putString("task list", json);
        editor.apply();
    }

    public static void insertItem(String line1, String line2,Context context) {
        dataArrayList.add(new Data(line1, line2,false));
        Log.d("TAG", "insertItem: aehrfg ");
        SaveData(context);
    }

    public void search(String searchText){
        ArrayList<Data> searchresults = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();

        int i = 0;
        for(Data data: dataArrayList){
            if(data.getItem().contains(searchText)){
                searchresults.add(data);
                index.add(i);
            }
            i++;
        }

        mRecyclerView = getView().findViewById(R.id.search_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new recyclerViewAdapter(searchresults);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnClickListener(new recyclerViewAdapter.OnItemClickedListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(index.get(position));
                searchresults.remove(position);
                index.remove(position);
                SaveData(getContext());
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFavClick(int position) {
                if(dataArrayList.get(index.get(position)).getFav()){
                    dataArrayList.get(index.get(position)).isFav(false);
                    searchresults.get(position).isFav(false);
                    SaveData(getContext());
                    mAdapter.notifyItemChanged(position);
                }
                else{
                    dataArrayList.get(index.get(position)).isFav(true);
                    searchresults.get(position).isFav(true);
                    SaveData(getContext());
                    mAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onEditClick(int position) {
                Data d = searchresults.get(position);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                final EditText inputItem = new EditText(getContext());
                final EditText inputLocation = new EditText(getContext());
                final TextView inputItemText = new TextView(getContext());
                final TextView inputLocationText = new TextView(getContext());

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                inputItem.setText(d.getItem());
                inputLocation.setText(d.getLocation());
                inputItemText.setText("Item Name");
                inputLocationText.setText("Item Location");

                layout.addView(inputItemText);
                layout.addView(inputItem);
                layout.addView(inputLocationText);
                layout.addView(inputLocation);

                alertDialog.setView(layout);
                alertDialog.setTitle("Edit");

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        d.item=inputItem.getText().toString().toLowerCase();
                        d.location=inputLocation.getText().toString().toLowerCase();
                        dataArrayList.set(index.get(position),d);
                        searchresults.set(position,d);
                        mAdapter.notifyItemChanged(position);
                    }
                });
                alertDialog.show();
            }
        });
    }

    public void removeItem(int position){
        dataArrayList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void NLP(String text, int pos) {
        String location = " ";
        String itemname = prcessItemName(text);
        if(itemname==null) {
            Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();
            return;
        }
        location = prcessLocationName(itemname, text);

        Toast.makeText(getContext(), "item "+ itemname +"      loc  "+location, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        final EditText inputItem = new EditText(getContext());
        final EditText inputLocation = new EditText(getContext());
        final TextView inputItemText = new TextView(getContext());
        final TextView inputLocationText = new TextView(getContext());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        inputItem.setText(itemname);
        inputLocation.setText(location);
        inputItemText.setText("Item Name");
        inputLocationText.setText("Item Location");

        layout.addView(inputItemText);
        layout.addView(inputItem);
        layout.addView(inputLocationText);
        layout.addView(inputLocation);

        alertDialog.setView(layout);
        alertDialog.setMessage("Edit if necessary");

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertItem(inputItem.getText().toString().toLowerCase(),inputLocation.getText().toString().toLowerCase(),getContext());
                mAdapter.notifyItemInserted(pos);
            }
        });
        alertDialog.show();
    }

    public String prcessItemName(String text){
        String[] split_string = text.split(" ");
        String itemName = " ";

        String[] stopwords ={"i","I","kept","keep", "me", "my", "myself", "we","get", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves",
                "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
                "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have",
                "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
                "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below",
                "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when",
                "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only",
                "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};


        for(int i=0 ; i<split_string.length -1 ; i++){

            if(!Arrays.asList(stopwords).contains(split_string[i]) ){
                itemName = itemName.concat(split_string[i]+" ");
                if(Arrays.asList(stopwords).contains(split_string[i+1])){
                    return itemName;
                }
            }
        }
        return itemName;
    }

    public String prcessLocationName(String item,String text){
        String[] split_string = text.split(item);
        if(split_string.length>1){
            return  split_string[1];
        }
        return null;
    }

}
