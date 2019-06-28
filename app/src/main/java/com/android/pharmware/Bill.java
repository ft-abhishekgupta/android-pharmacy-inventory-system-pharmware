package com.android.pharmware;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
import java.util.StringTokenizer;

public class Bill extends AppCompatActivity {

    TextView ph1, ph2, storename, storeaddr, transid, dlnum, date, gst, ptname, age, docname, totamt, name;
    TextView medname[], unit[], batch[], qty[], rate[], exp[], amt[];
    String ids[];
    private DatabaseReference du;
    private DatabaseReference di;
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

        medname = new TextView[8];
        unit = new TextView[8];
        batch = new TextView[8];
        qty = new TextView[8];
        rate = new TextView[8];
        exp = new TextView[8];
        amt = new TextView[8];

        ids = new String[] { "medname", "unit", "batch", "qty", "rate", "exp", "amt" };
        for (int j = 1; j <= 8; j++) {
            medname[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[0] + j, "id", getPackageName()));
            unit[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[1] + j, "id", getPackageName()));
            batch[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[2] + j, "id", getPackageName()));
            qty[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[3] + j, "id", getPackageName()));
            rate[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[4] + j, "id", getPackageName()));
            exp[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[5] + j, "id", getPackageName()));
            amt[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[6] + j, "id", getPackageName()));
        }

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

        Intent intent = getIntent();
        String id = intent.getStringExtra("key");
        di = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Invoice").child("Bill")
                .child(id);
        di.keepSynced(true);
        di.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                totamt.setText(map.get("gtotamt"));
                date.setText(map.get("gdate"));
                docname.setText(map.get("gdoc"));
                ptname.setText(map.get("gpt"));
                transid.setText(map.get("gtranid"));
                age.setText(map.get("age"));
                String items = map.get("gitems");
                StringTokenizer st = new StringTokenizer(items, "®");
                int k = -1;
                while (st.hasMoreTokens()) {
                    k++;
                    // Toast.makeText(Bill.this,""+k , Toast.LENGTH_SHORT).show();
                    StringTokenizer sti = new StringTokenizer(st.nextToken(), "©");
                    while (sti.hasMoreTokens()) {
                        medname[k].setText(sti.nextToken());
                        unit[k].setText("1x" + sti.nextToken());
                        batch[k].setText(sti.nextToken());
                        qty[k].setText(sti.nextToken());
                        rate[k].setText(sti.nextToken());
                        exp[k].setText(sti.nextToken());
                        amt[k].setText(sti.nextToken());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

}
