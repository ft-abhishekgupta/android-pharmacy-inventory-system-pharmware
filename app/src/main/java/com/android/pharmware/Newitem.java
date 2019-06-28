package com.android.pharmware;

import android.app.DatePickerDialog;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Newitem extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout iName, batchNo, qty, expdate, mrp, sp, cName, code, cp, desc, unit;
    EditText einame, ebatchNo, eqty, eexpdate, emrp, esp, ecName, ecode, ecp, edesc, eunit;
    private DatabaseReference du;

    private FirebaseAuth mAuth;
    String uid;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_info);

        setTitle("Add Item");

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        batchNo = (TextInputLayout) findViewById(R.id.batchLayout);
        qty = (TextInputLayout) findViewById(R.id.qtyLayout);
        expdate = (TextInputLayout) findViewById(R.id.expLayout);
        mrp = (TextInputLayout) findViewById(R.id.mrpLayout);
        sp = (TextInputLayout) findViewById(R.id.spLayout);
        cName = (TextInputLayout) findViewById(R.id.cNameLayout);
        code = (TextInputLayout) findViewById(R.id.codeLayout);
        cp = (TextInputLayout) findViewById(R.id.cpLayout);
        desc = (TextInputLayout) findViewById(R.id.descLayout);
        unit = (TextInputLayout) findViewById(R.id.unitLayout);

        einame = (EditText) findViewById(R.id.nameText);
        ebatchNo = (EditText) findViewById(R.id.batchText);
        eqty = (EditText) findViewById(R.id.qtyText);
        eexpdate = (EditText) findViewById(R.id.expText);
        emrp = (EditText) findViewById(R.id.mrpText);
        esp = (EditText) findViewById(R.id.spText);
        ecName = (EditText) findViewById(R.id.cNameText);
        ecode = (EditText) findViewById(R.id.codeText);
        ecp = (EditText) findViewById(R.id.cpText);
        edesc = (EditText) findViewById(R.id.descText);
        eunit = (EditText) findViewById(R.id.unitText);

        emrp.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
        esp.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
        ecp.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        eexpdate.setInputType(InputType.TYPE_NULL);
        eexpdate.setFocusable(false);
        eexpdate.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        du.keepSynced(true);
    }

    public void addInfo(View v) {
        String siname = einame.getText().toString();
        String sbatchNo = ebatchNo.getText().toString();
        String sqty = eqty.getText().toString();
        String sexpDate = eexpdate.getText().toString();
        String smrp = emrp.getText().toString();
        String ssp = esp.getText().toString();
        String scName = ecName.getText().toString();
        String scode = ecode.getText().toString();
        String scp = ecp.getText().toString();
        String sdesc = edesc.getText().toString();
        String sunit = eunit.getText().toString();

        if (validate()) {
            String k = du.child("Items").push().getKey();
            Model m = new Model(siname, sbatchNo, sqty, sexpDate, smrp, ssp, scName, scode, scp, sdesc, k, sunit);
            du.child("Items").child(k).setValue(m);

            // log
            final String item = siname;
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
                                + fm.format(calendar.getTime()) + "] " + item + " Item Added\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Item Added\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            Toast.makeText(this, "Your Item is saved", Toast.LENGTH_SHORT).show();
            m_clear();
        } else {
            Toast.makeText(this, "Sorry.. Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        int flag = 0;
        if (einame.getText().toString().trim().isEmpty()) {
            flag = 1;
            iName.setError("Cannot be Empty");
        } else
            iName.setErrorEnabled(false);
        if (eqty.getText().toString().trim().isEmpty()) {
            flag = 1;
            qty.setError("Cannot be Empty");
        } else
            qty.setErrorEnabled(false);
        if (ebatchNo.getText().toString().trim().isEmpty()) {
            flag = 1;
            batchNo.setError("Cannot be Empty");
        } else
            batchNo.setErrorEnabled(false);
        if (eexpdate.getText().toString().trim().isEmpty()) {
            flag = 1;
            expdate.setError("Cannot be Empty");
        } else
            expdate.setErrorEnabled(false);
        if (emrp.getText().toString().trim().isEmpty()) {
            flag = 1;
            mrp.setError("Cannot be Empty");
        } else
            mrp.setErrorEnabled(false);
        if (esp.getText().toString().trim().isEmpty()) {
            flag = 1;
            sp.setError("Cannot be Empty");
        } else
            sp.setErrorEnabled(false);
        if (ecName.getText().toString().trim().isEmpty()) {
            flag = 1;
            cName.setError("Cannot be Empty");
        } else
            cName.setErrorEnabled(false);
        if (ecode.getText().toString().trim().isEmpty()) {
            flag = 1;
            code.setError("Cannot be Empty");
        } else
            code.setErrorEnabled(false);
        if (ecp.getText().toString().trim().isEmpty()) {
            flag = 1;
            cp.setError("Cannot be Empty");
        } else
            cp.setErrorEnabled(false);

        if (eunit.getText().toString().trim().isEmpty()) {
            flag = 1;
            unit.setError("Cannot be Empty");
        } else
            unit.setErrorEnabled(false);
        if (flag == 1)
            return false;
        else
            return true;
    }

    public void clear(View v) {
        m_clear();
    }

    public void m_clear() {
        einame.setText("");
        ebatchNo.setText("");
        emrp.setText("");
        esp.setText("");
        eexpdate.setText("");
        eqty.setText("");
        ecName.setText("");
        ecode.setText("");
        ecp.setText("");
        edesc.setText("");
        eunit.setText("");
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
                SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
                eexpdate.setText(format.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
