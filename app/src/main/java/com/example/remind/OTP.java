package com.example.remind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    EditText OTP;
    String verificationID;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;
    String number;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        prefs = getSharedPreferences("com.Login.ReMIND", MODE_PRIVATE);
        OTP = findViewById(R.id.otp);
        Button verify = (Button) findViewById(R.id.verify);
        final EditText phonenumber = (EditText)findViewById(R.id.phone);

        if (prefs.getBoolean("Login", false)) {
            number = prefs.getString("Number",null);
            Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OTP.this,MainActivity.class);
            intent.putExtra("phone",number);
            startActivity(intent);
        }

        ccp = (CountryCodePicker) findViewById(R.id.ccp);


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = phonenumber.getText().toString().trim();
                ccp.registerCarrierNumberEditText(phonenumber);
                number = ccp.getFullNumberWithPlus();
                Toast.makeText(OTP.this, number, Toast.LENGTH_SHORT).show();

                if(phone.length()>9) {
                    sendVerificationCode(number);
                }
                else{
                    Toast.makeText(OTP.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = OTP.getText().toString().trim();
                if(otp.isEmpty()) {
                    Toast.makeText(OTP.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyCode(otp);
                }
            }
        });

        Button skip = (Button)  findViewById(R.id.skipotp);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTP.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verifyCode(String code){
        if(verificationID!=null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
            signInWithPhoneAuthCredential(credential);
            String t = credential.toString();
        }
        else{
            Toast.makeText(this, "invalid otp", Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    prefs.edit().putBoolean("Login", true).apply();
                    prefs.edit().putString("Number",number).apply();
                    Toast.makeText(OTP.this, "login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTP.this,MainActivity.class);
                    intent.putExtra("phone",number);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(OTP.this, "Signin fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
                        super.onCodeSent(s,forceResendingToken);
                        verificationID = s;
                        Toast.makeText(OTP.this, "OTP has been sent to your mobile number", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if(code!= null){
                            verifyCode(code);
                        }
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTP.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
