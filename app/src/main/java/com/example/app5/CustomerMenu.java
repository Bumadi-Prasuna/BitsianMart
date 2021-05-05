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

public class CustomerMenu extends AppCompatActivity {

    Button lastOrder,logout,shop,feedback;
    TextView name;
    FirebaseAuth fAuth;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        db = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        lastOrder = findViewById(R.id.ordersButton);
        logout = findViewById(R.id.logoutButton);
        shop = findViewById(R.id.shoppingButton);
        feedback = findViewById(R.id.feedbackButton);

        name = findViewById(R.id.nameText);

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

        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShopActivity.class));
            }
        });

        lastOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CancelOrder.class));
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

    }
}