package com.android.pharmware;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Log extends AppCompatActivity {

    String uid;
    DatabaseReference db;
    private FirebaseAuth mAuth;
    TextView t;
    ValueEventListener v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        t = (TextView) findViewById(R.id.textView);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Log");
        db.keepSynced(true);
        v = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                final Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                setTitle("Log");
                t.setText(map.get("log"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        db.addValueEventListener(v);

    }

    @Override
    protected void onPause() {
        super.onPause();
        db.removeEventListener(v);
    }
}
