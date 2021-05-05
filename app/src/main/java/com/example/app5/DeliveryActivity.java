package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DeliveryActivity extends AppCompatActivity {

    Button offline,online,proceed;
    TextView dateSet,storeText;
    FirebaseDatabase db1,db2,db3,db4;
    FirebaseAuth fAuth;
    DatePickerDialog.OnDateSetListener datePicker;
    String datePicked;
    Random rand = new Random();
    String select;
    Dialog dialog;
    ListView list;
    EditText txtsetTime;
Boolean flag=true;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
txtsetTime= (EditText) findViewById(R.id.setTime);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.shop);
        list = dialog.findViewById(R.id.shopList);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String>order = bundle.getStringArrayList("order");

        ArrayList<String> category = new ArrayList<String>();
        ArrayList<String> text = new ArrayList<String>();

        ArrayList<String> shops = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_item,shops);
        list.setAdapter(adapter);

        db1 = FirebaseDatabase.getInstance();
        db2 = FirebaseDatabase.getInstance();
        db3 = FirebaseDatabase.getInstance();
        db4 = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        online = findViewById(R.id.onlineButton);
        offline = findViewById(R.id.offlineButton);
        proceed = findViewById(R.id.proceedButton);

        dateSet = findViewById(R.id.dateTextView);
        storeText = findViewById(R.id.storeTextView);

        String id = String.valueOf(rand.nextInt(10000));
        String uID = fAuth.getCurrentUser().getUid();

        online.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                flag=false;
                offline.setClickable(false);
                dateSet.setVisibility(View.INVISIBLE);
                txtsetTime.setVisibility(View.INVISIBLE);
                order.add("Mode of delivery : Online");
                text.add("Mode of delivery : Online");
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                order.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                order.add("Delivery date : " + String.valueOf(day+1) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Delivery date : " + String.valueOf(day+1) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                proceed.setVisibility(View.VISIBLE);
            }
        });

        offline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                flag=true;
                storeText.setVisibility(View.VISIBLE);
                order.add("Mode of delivery : Offline");
                text.add("Mode of delivery : Offline");
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                order.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        });

        db1.getReference("Users").child(uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category.clear();
                category.add(snapshot.child("category").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storeText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (category.get(0).equals("Customer")) {
                    select = "R";
                } else {
                    select = "W";
                }
                db2.getReference(select).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        shops.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String company = snapshot.getKey() + ", " + snapshot.getValue(String.class);
                            shops.add(company);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.show();

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        dialog.cancel();
                        String storeName = "Pickup Point : " + shops.get(i).split(",")[0].trim();
                        storeText.setText(storeName);
                        order.add(storeName);
                        text.add(storeName);
                        dateSet.setVisibility(View.VISIBLE);
                        txtsetTime.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        dateSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        DeliveryActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePicker,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
             //   datePicked = String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
                datePicked=String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day);
                dateSet.setClickable(false);
                offline.setClickable(false);
                online.setClickable(false);
                proceed.setVisibility(View.VISIBLE);
                dateSet.setText("Pickup Date : " + datePicked);
                order.add("Pickup Date : " + datePicked);
                text.add("Pickup Date : " + datePicked);
            }
        };

        proceed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String oID = uID + id;
                String oString = order.toString();
                String message = "Order ID : " + oID + "\n" + text.toString().substring(1,text.toString().length()-1).replaceAll(",","\n") + "\n\nTeam BITSIAN-MART";

                System.out.println("message: "+message);
                System.out.println("message "+text);
                SmsManager sms = SmsManager.getDefault();

                sms.sendTextMessage( "+912300523005",null,message,null,null);
                Map<String,Object> orderMap = new HashMap<String,Object>();
                orderMap.put(oID,oString);
                db3.getReference("Orders").child(category.get(0)).updateChildren(orderMap);
                Map<String,Object> lastOrder = new HashMap<String,Object>();
                lastOrder.put(uID,oString);
                db4.getReference("Latest_Order").updateChildren(lastOrder);
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));



                RequestQueue queue = Volley.newRequestQueue(DeliveryActivity.this);
             //   String otp1=otp.toString();
             //   System.out.println("Otp entante "+otp1);
               // String url ="https://be45672e1287.ngrok.io/calender/sendotp/"+mail+"/"+otp1;
                //  Toast.makeText(LoginActivity.this,"URLLL "+url,Toast.LENGTH_SHORT).show();
//                System.out.println("Url got assigned. Dw. "+url);
                String mail=fAuth.getCurrentUser().getEmail();
String time=txtsetTime.getText().toString();
String url;
                if(flag){
                     url="https://d80fda00c62b.ngrok.io/calender/create/"+mail+"/"+datePicked+"T"+time+":00+05:30/";
                }
                else{
                     url="https://d80fda00c62b.ngrok.io/calender/send/"+mail;
                }
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




            }
        });

    }
}