package com.example.remind;

public class Data {
    String item;
    String location;
    boolean fav;

    public Data(String i,String l,boolean f){
        item = i;
        location = l;
        fav =f;
    }

    public String getItem(){
        return item;
    }
    public String getLocation(){
        return location;
    }
    public boolean getFav(){
        return fav;
    }
    public boolean isFav(boolean b){
        this.fav=b;

        return fav;
    }
}
