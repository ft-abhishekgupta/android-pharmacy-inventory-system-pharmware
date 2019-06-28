package com.android.pharmware;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Newcustomer extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout iName, iaddr, iphone, ibalance, idate;
    EditText eName, eaddr, ephone, ebalance, edate;
    private DatabaseReference du;

    private FirebaseAuth mAuth;
    String uid;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);

        setTitle("Add Customer");

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        iaddr = (TextInputLayout) findViewById(R.id.addrLayout);
        iphone = (TextInputLayout) findViewById(R.id.phoneLayout);
        ibalance = (TextInputLayout) findViewById(R.id.balanceLayout);
        idate = (TextInputLayout) findViewById(R.id.dateLayout);

        eName = (EditText) findViewById(R.id.nameText);
        eaddr = (EditText) findViewById(R.id.addrText);
        ephone = (EditText) findViewById(R.id.phoneText);
        ebalance = (EditText) findViewById(R.id.balanceText);
        edate = (EditText) findViewById(R.id.dateText);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        edate.setText(format.format(c.getTime()));

        ebalance.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        edate.setInputType(InputType.TYPE_NULL);
        edate.setFocusable(false);
        edate.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer");
        du.keepSynced(true);
    }

    public void addInfo(View v) {
        String sName = eName.getText().toString();
        String saddr = eaddr.getText().toString();
        String sphone = ephone.getText().toString();
        String sbalance = ebalance.getText().toString();
        String sdate = edate.getText().toString();

        if (validate()) {
            String k = du.child("Person").push().getKey();
            Modelcustomer m = new Modelcustomer(sName, sphone, sdate, sbalance, saddr, k);
            du.child("Person").child(k).setValue(m);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            du.child("Person").child(k).child("log").setValue(
                    "[" + format.format(calendar.getTime()) + "] " + "Account created. Balance:" + sbalance + "\n");

            // log
            final String item = sName;
            du.getParent().child("Log").child("log").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String l = (String) dataSnapshot.getValue();
                    String date = l.substring(0, 10);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                    if (format.format(calendar.getTime()).equals(date)) {
                        l = l.substring(11);
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Customer Account Created\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Customer Account Created\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            Toast.makeText(this, "Customer is saved", Toast.LENGTH_SHORT).show();
            m_clear();
        } else {
            Toast.makeText(this, "Sorry.. Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        int flag = 0;
        if (eName.getText().toString().trim().isEmpty()) {
            flag = 1;
            iName.setError("Cannot be Empty");
        } else
            iName.setErrorEnabled(false);

        if (ephone.getText().toString().trim().isEmpty()) {
            flag = 1;
            iphone.setError("Cannot be Empty");
        } else
            iphone.setErrorEnabled(false);
        if (edate.getText().toString().trim().isEmpty()) {
            flag = 1;
            idate.setError("Cannot be Empty");
        } else
            idate.setErrorEnabled(false);
        if (ebalance.getText().toString().trim().isEmpty()) {
            flag = 1;
            ibalance.setError("Cannot be Empty");
        } else
            ibalance.setErrorEnabled(false);

        if (flag == 1)
            return false;
        else
            return true;
    }

    public void clear(View v) {
        m_clear();
    }

    public void m_clear() {
        eName.setText("");
        eaddr.setText("");
        ephone.setText("");
        ebalance.setText("");
        edate.setText("");

    }

    @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                edate.setText(format.format(calendar.getTime()));

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
