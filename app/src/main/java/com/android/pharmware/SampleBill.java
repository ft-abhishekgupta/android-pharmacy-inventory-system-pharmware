package com.android.pharmware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SampleBill extends AppCompatActivity {

    TextView ph1, ph2, storename, storeaddr, transid, dlnum, date, gst, ptname, age, docname, totamt, name;
    private DatabaseReference du;
    private FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.invoice);
        getSupportActionBar().hide();

        ph1 = (TextView) findViewById(R.id.ph1);
        ph2 = (TextView) findViewById(R.id.ph2);
        storename = (TextView) findViewById(R.id.storename);
        storeaddr = (TextView) findViewById(R.id.storeaddr);
        transid = (TextView) findViewById(R.id.transid);
        dlnum = (TextView) findViewById(R.id.dlnum);
        date = (TextView) findViewById(R.id.date);
        gst = (TextView) findViewById(R.id.gst);
        ptname = (TextView) findViewById(R.id.ptname);
        age = (TextView) findViewById(R.id.age);
        docname = (TextView) findViewById(R.id.docname);
        totamt = (TextView) findViewById(R.id.totamt);
        name = (TextView) findViewById(R.id.name);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("info");
        du.keepSynced(true);
        du.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                name.setText(map.get("name"));
                storename.setText(map.get("storename"));
                dlnum.setText(map.get("tin"));
                storeaddr.setText(map.get("addr"));
                ph1.setText("Ph: " + map.get("ph1"));
                ph2.setText("Ph: " + map.get("ph2"));
                gst.setText(map.get("gst"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

}
