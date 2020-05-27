package com.example.remind;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    SharedPreferences prefs,profile,mobile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        prefs = getActivity().getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        profile = getActivity().getSharedPreferences("com.Profile.ReMIND", MODE_PRIVATE);
        mobile = getActivity().getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        String Susername = profile.getString("UserName","username");
        String phone = mobile.getString("Number","Login to display number");

        TextView username = v.findViewById(R.id.username);
        username.setText(Susername);

        TextView phone_number = v.findViewById(R.id.mobile_number);
        phone_number.setText(phone);


        CircularImageView profilePic = v.findViewById(R.id.profile_picture);
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

        ImageView edit = v.findViewById(R.id.edit_profile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });



        TextView logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("Login", false).apply();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), OTP.class);
                startActivity(intent);
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs = getActivity().getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        profile = getActivity().getSharedPreferences("com.Profile.ReMIND", MODE_PRIVATE);
        mobile = getActivity().getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        String Susername = profile.getString("UserName","username");
        String phone = mobile.getString("Number","Login to display number");

        TextView username = getActivity().findViewById(R.id.username);
        username.setText(Susername);

        TextView phone_number = getActivity().findViewById(R.id.mobile_number);
        phone_number.setText(phone);


        CircularImageView profilePic = getActivity().findViewById(R.id.profile_picture);
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
    }
}
