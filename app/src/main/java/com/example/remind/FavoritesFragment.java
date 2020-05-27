package com.example.remind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class FavoritesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private recyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Data> dataArrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites,container,false);

        loadData();

        ArrayList<Data> favList = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();


        int i = 0;
        for(Data fav:dataArrayList){

            if(fav.getFav()){
                favList.add(fav);
                index.add(i);
            }
            i++;
        }

        mRecyclerView = v.findViewById(R.id.favorites_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new recyclerViewAdapter(favList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new recyclerViewAdapter.OnItemClickedListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(index.get(position));
                favList.remove(position);
                index.remove(position);
                SaveData(getContext());
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFavClick(int position) {
                dataArrayList.get(index.get(position)).isFav(false);
                favList.remove(position);
                index.remove(position);
                SaveData(getContext());
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onEditClick(int position){

            }
        });

        return v;
    }

    public void loadData(){
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

    public void removeItem(int position){
        dataArrayList.remove(position);
        //mAdapter.notifyItemRemoved(position);
    }
}
