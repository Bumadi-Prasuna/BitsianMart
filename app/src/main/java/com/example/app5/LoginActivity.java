package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LoginActivity extends AppCompatActivity {

    Button log,sendOtp;
    EditText mailId,pass,otpText;
    FirebaseAuth firebase;
    FirebaseDatabase database;
    String category,uID,message,phone;
    Integer otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailId = findViewById(R.id.mailEditText);
        pass = findViewById(R.id.passwordEditText);
        otpText = findViewById(R.id.otpEditText);
        log = findViewById(R.id.loginButton);
        sendOtp = findViewById(R.id.otpButton);
        firebase = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        otp = ThreadLocalRandom.current().nextInt(100000,999999);

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mailId.getText().toString().trim();
                String p = pass.getText().toString().trim();

                firebase.signInWithEmailAndPassword(mail, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mailId.setVisibility(View.INVISIBLE);
                            pass.setVisibility(View.INVISIBLE);
                            sendOtp.setVisibility(View.INVISIBLE);
                            otpText.setVisibility(View.VISIBLE);
                            log.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"An OTP has been sent your email address for verification" , Toast.LENGTH_SHORT).show();
                            database.getReference().child("Users").addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    uID = firebase.getCurrentUser().getUid();
                                    phone = snapshot.child(uID).child("phone").getValue(String.class);
                                    category = snapshot.child(uID).child("category").getValue().toString();
                                 // message = "Your otp for e-mart " +  category.toLowerCase() + " login is " + otp.toString() + ". Do not share your otp with others.\n\nTeam E-Mart";
//WRITE HERE
                                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                    String otp1=otp.toString();
                                    System.out.println("Otp entante "+otp1);
                                    String url ="https://d80fda00c62b.ngrok.io/calender/sendotp/"+mail+"/"+otp1;
                                  //  Toast.makeText(LoginActivity.this,"URLLL "+url,Toast.LENGTH_SHORT).show();
                                    System.out.println("Url got assigned. Dw. "+url);
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    // Display the first 500 characters of the response string.
                                                    //    textView.setText("Response is: "+ response.substring(0,500));
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //  textView.setText("That didn't work!");
                                        }
                                    });
                                    queue.add(stringRequest);


                                    log.setOnClickListener(new View.OnClickListener(){

                                        @Override
                                        public void onClick(View view) {

                                            if(otpText.getText().toString().trim().equals(otp.toString())){
                                                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();

                                                if(category.equals("Customer")) {
                                                    startActivity(new Intent(getApplicationContext(), CustomerMenu.class));
                                                }else if(category.equals("Retailer")){
                                                    startActivity(new Intent(getApplicationContext(), RetailMenu.class));
                                                }else {
                                                    startActivity(new Intent(getApplicationContext(), WholesaleMenu.class));
                                                }
                                            }else{
                                                Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
                                                otpText.getText().clear();
                                            }

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                            pass.getText().clear();
                            mailId.getText().clear();
                        }



                    }
                });
            }
        });
    }


}
