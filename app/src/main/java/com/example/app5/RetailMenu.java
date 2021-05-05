package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetailMenu extends AppCompatActivity {

    TextView name;
    Button itemPM,orders,shop,logout,feedback,cancel;
    FirebaseDatabase db;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_menu);

        db = FirebaseDatabase.getInstance();

        name = findViewById(R.id.nameText);
        itemPM = findViewById(R.id.itemButton);
        orders = findViewById(R.id.ordersButton);
        shop = findViewById(R.id.shoppingButton);
        logout = findViewById(R.id.logoutButton);
        feedback = findViewById(R.id.feedbackButton);
        cancel = findViewById(R.id.cancelButton);

        fAuth = FirebaseAuth.getInstance();
        String uID = fAuth.getCurrentUser().getUid();

        db.getReference().child("Users").child(uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String adder = snapshot.child("title").getValue(String.class);
                name.setText("Name : " + adder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orders.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),OrderList.class));
            }
        });

        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShopActivity.class));
            }
        });

        itemPM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddRemoveActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CancelOrder.class));
            }
        });

    }
}