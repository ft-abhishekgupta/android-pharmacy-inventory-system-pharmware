package com.android.pharmware;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Trans extends AppCompatActivity implements View.OnClickListener {

    View v;
    String uid;
    private DatabaseReference db, dbi, dbc, dbs;
    private FirebaseAuth mAuth;
    AutoCompleteTextView act, act2;
    AutoAdapter adapter;
    AutoCustAdapter autoCustAdapter;
    ListAdapter listadapter;
    ArrayList<Model> models = new ArrayList<>();
    TextInputLayout date, pt, doc, age;
    EditText edate, ept, edoc, eage, due;
    private int mYear, mMonth, mDay;
    ArrayList<ListModel> listmodel = new ArrayList<>();
    ArrayList<Modelcustomer> modelcustomers = new ArrayList<>();
    ListView lv;
    Button btn, custbtn;
    static Button gen;
    LinearLayout linearLayout;
    boolean set = false;
    static TextView totamt;
    static double totamount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.m_trans);
        setTitle("Transaction");

        getSupportActionBar().hide();

        date = (TextInputLayout) findViewById(R.id.dateLayout);
        pt = (TextInputLayout) findViewById(R.id.ptLayout);
        doc = (TextInputLayout) findViewById(R.id.docLayout);
        age = (TextInputLayout) findViewById(R.id.ageLayout);
        totamount = 0.0;

        edate = (EditText) findViewById(R.id.dateText);
        ept = (EditText) findViewById(R.id.ptText);
        edoc = (EditText) findViewById(R.id.docText);
        eage = (EditText) findViewById(R.id.ageText);
        due = (EditText) findViewById(R.id.amtdue);
        totamt = (TextView) findViewById(R.id.totamt);

        edate.setInputType(InputType.TYPE_NULL);
        edate.setFocusable(false);
        edate.setOnClickListener(this);
        act = (AutoCompleteTextView) findViewById(R.id.act);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        edate.setText(format.format(c.getTime()));

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        dbi = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Invoice");
        dbc = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer");
        dbs = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Summary");

        db.keepSynced(true);
        dbi.keepSynced(true);
        dbc.keepSynced(true);
        dbs.keepSynced(true);

        btn = (Button) findViewById(R.id.btnadd);
        gen = (Button) findViewById(R.id.genbtn);
        custbtn = (Button) findViewById(R.id.regcust);
        linearLayout = (LinearLayout) findViewById(R.id.cust);

        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    if (custValidate()) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
                        alert.setTitle("Confirm");
                        alert.setMessage("Generate Bill?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Updating customerdetail
                                if (set) {
                                    updateCust(autoCustAdapter.detcust()[0], due.getText().toString(),
                                            autoCustAdapter.detcust()[1]);
                                }

                                int size = listmodel.size();
                                String gdate = edate.getText().toString().trim();
                                String gdoc = edoc.getText().toString().trim();
                                String gpt = ept.getText().toString().trim();
                                String gage = eage.getText().toString().trim();

                                String[] itemlist = new String[size];
                                String items = "";

                                int itemcnt = 0;
                                for (int i = 0; i < size; i++) {
                                    itemlist[i] = listmodel.get(i).getMed() + "©" + listmodel.get(i).getUnit() + "©"
                                            + listmodel.get(i).getBatch() + "©" + listmodel.get(i).getQty() + "©"
                                            + listmodel.get(i).getPr() + "©" + listmodel.get(i).getExpdate() + "©"
                                            + listmodel.get(i).getAmount();

                                    if (items.equals(""))
                                        items = items + itemlist[i];
                                    else
                                        items = items + "®" + itemlist[i];

                                    itemcnt += Integer.parseInt(listmodel.get(i).getQty());

                                    // updating itemquantity
                                    updateQty(listmodel.get(i).getKey(), listmodel.get(i).getQty(),
                                            listmodel.get(i).getOrgqty());
                                }

                                final String gtotamt = totamt.getText().toString();

                                // save to firebase
                                final String k = dbi.child("Bill").push().getKey();
                                dbi.child("Bill").child(k).child("gdate").setValue(gdate);
                                dbi.child("Bill").child(k).child("gdoc").setValue(gdoc);
                                dbi.child("Bill").child(k).child("gpt").setValue(gpt);
                                dbi.child("Bill").child(k).child("gitems").setValue(items);
                                dbi.child("Bill").child(k).child("gtotamt").setValue(gtotamt);
                                dbi.child("Bill").child(k).child("key").setValue(k);
                                dbi.child("Bill").child(k).child("age").setValue(gage);

                                final int icount = itemcnt;
                                final double amt = totamount;

                                dbs.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("date")) {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                                            SimpleDateFormat fm = new SimpleDateFormat("MM");
                                            String date = (String) dataSnapshot.child("date").getValue();
                                            String month = (String) dataSnapshot.child("month").getValue();
                                            int tcount = Integer
                                                    .parseInt((String) dataSnapshot.child("trans").getValue()) + 1;
                                            int count = Integer
                                                    .parseInt((String) dataSnapshot.child("count").getValue());
                                            int mcount = Integer
                                                    .parseInt((String) dataSnapshot.child("monthcount").getValue());
                                            double value = Double
                                                    .parseDouble((String) dataSnapshot.child("amt").getValue());
                                            double mvalue = Double
                                                    .parseDouble((String) dataSnapshot.child("monthamt").getValue());
                                            value = roundOff(value);
                                            mvalue = roundOff(mvalue);
                                            if (format.format(c.getTime()).equals(date)) {
                                                dbs.child("count").setValue((count + icount) + "");
                                                dbs.child("amt").setValue(roundOff(value + amt) + "");
                                            } else {
                                                dbs.child("date").setValue(format.format(c.getTime()));
                                                dbs.child("count").setValue(icount + "");
                                                dbs.child("amt").setValue(amt + "");
                                            }
                                            if (fm.format(c.getTime()).equals(month)) {
                                                dbs.child("monthcount").setValue((mcount + icount) + "");
                                                dbs.child("monthamt").setValue(roundOff(mvalue + amt) + "");
                                            } else {
                                                dbs.child("month").setValue(fm.format(c.getTime()));
                                                dbs.child("monthcount").setValue(icount + "");
                                                dbs.child("monthamt").setValue(amt + "");
                                            }

                                            dbi.child("Bill").child(k).child("gtranid").setValue(tcount + "");
                                            dbs.child("trans").setValue(tcount + "");
                                        } else {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                                            SimpleDateFormat fm = new SimpleDateFormat("MM");
                                            dbs.child("date").setValue(format.format(c.getTime()));
                                            dbs.child("count").setValue(icount + "");
                                            dbs.child("amt").setValue(amt + "");
                                            dbs.child("month").setValue(fm.format(c.getTime()));
                                            dbs.child("monthamt").setValue(amt + "");
                                            dbs.child("monthcount").setValue(icount + "");
                                            dbs.child("trans").setValue("1");

                                            dbi.child("Bill").child(k).child("gtranid").setValue("1");

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                // log
                                db.getParent().child("Log").child("log")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String l = (String) dataSnapshot.getValue();
                                                String date = l.substring(0, 10);
                                                Calendar calendar = Calendar.getInstance();
                                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                                SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                                                if (format.format(calendar.getTime()).equals(date)) {
                                                    l = l.substring(11);
                                                    db.getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime())
                                                                    + "] Transaction. Amount:" + gtotamt + "\n" + l);
                                                } else {
                                                    db.getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime())
                                                                    + "] Transaction. Amount:" + gtotamt + "\n\n" + l);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });

                                Toast.makeText(Trans.this, "Bill Saved", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), Bill.class);
                                i.putExtra("key", k);
                                startActivity(i);
                                finish();
                                // clear();
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();
                    } else {
                        Toast.makeText(Trans.this, "Regular Customer Details not properly entered", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(Trans.this, "Fill Details Properly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbtn();
            }
        });

        custbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                    custbtn.setText("REGULAR CUSTOMER");
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    custbtn.setText("NOT REG. CUSTOMER");
                }

            }
        });

        db.child("Items").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                models.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Model model = ds.getValue(Model.class);
                    models.add(model);
                }

                act.setThreshold(1);
                adapter = new AutoAdapter(Trans.this, R.layout.m_trans, R.id.item_name, models);
                act.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbc.child("Person").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                modelcustomers.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Modelcustomer model = ds.getValue(Modelcustomer.class);
                    modelcustomers.add(model);
                }

                act2 = (AutoCompleteTextView) findViewById(R.id.actcust);
                act2.setThreshold(1);
                autoCustAdapter = new AutoCustAdapter(Trans.this, R.layout.m_trans, R.id.item_name, modelcustomers);
                act2.setAdapter(autoCustAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listmodel.clear();
        lv = (ListView) findViewById(R.id.list);
        listadapter = new ListAdapter(Trans.this, R.layout.list_items, listmodel);
        lv.setAdapter(listadapter);
        Utility.setListViewHeightBasedOnChildren(lv);
    }

    // Functions

    public boolean validate() {
        int flag = 0;
        if (edate.getText().toString().trim().isEmpty()) {
            flag = 1;
            date.setError("Cannot be Empty");
        } else
            date.setErrorEnabled(false);
        if (listmodel.size() == 0) {
            flag = 1;
        }
        if (flag == 1)
            return false;
        else
            return true;
    }

    public boolean custValidate() {
        if (!autoCustAdapter.detcust()[0].equals("") && !due.getText().toString().equals("")
                && autoCustAdapter.detcust()[2].equals(act2.getText().toString())) {
            set = true;
            return true;

        } else if (autoCustAdapter.detcust()[0].equals("") && due.getText().toString().equals("")
                && act2.getText().toString().equals("")) {
            set = false;
            return true;
        } else {
            return false;
        }
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

    protected void addbtn() {

        if (listmodel.size() < 8) {
            final String item = adapter.det()[0];
            final String mrp = adapter.det()[1];
            final String batch = adapter.det()[2];
            final String t = adapter.det()[3];
            final String k = adapter.det()[4];
            final String o = adapter.det()[5];
            final String u = adapter.det()[6];
            final String match = item + " (" + adapter.det()[7] + ")";

            if (!item.equals("") && !mrp.equals("") && match.equals(act.getText().toString())) {

                LayoutInflater layoutInflater = LayoutInflater.from(Trans.this);
                View promptView = layoutInflater.inflate(R.layout.input_dia, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Trans.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                alertDialogBuilder.setCancelable(false).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String qt = "";
                        qt = editText.getText().toString().trim();
                        if (!qt.equals("") && !qt.equals("0")) {

                            if (Integer.parseInt(qt) <= Integer.parseInt(o)) {

                                double d = (Double.valueOf(qt) / Double.valueOf(u)) * Double.valueOf(mrp);
                                d = roundOff(d);
                                String amount = String.valueOf(d);
                                totamount = totamount + Double.valueOf(amount);
                                totamount = roundOff(totamount);
                                ListModel list = new ListModel(item, mrp, qt, amount, batch, t, k, o, u);
                                listmodel.add(list);
                                listadapter.notifyDataSetChanged();
                                act.setText("");
                                totamt.setText(String.valueOf(totamount));
                                gen.setText("GENERATE BILL (₹" + totamount + ")");

                                Utility.setListViewHeightBasedOnChildren(lv);

                                dialog.dismiss();

                            } else {

                                Toast.makeText(Trans.this, "Quantity is not in stock", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(Trans.this, "Enter valid quantity", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alert.show();

            } else {
                Toast.makeText(Trans.this, "Select Item", Toast.LENGTH_SHORT).show();
            }
        }

        else {
            Toast.makeText(Trans.this, "No further items can be added", Toast.LENGTH_SHORT).show();
        }

    }

    public void clear() {
        edate.setText("");
        ept.setText("");
        edoc.setText("");
        eage.setText("");
        totamt.setText("");
        totamount = 0.0;
        listmodel.clear();
        act2.setText("");
        due.setText("");
        listadapter.notifyDataSetChanged();
    }

    public void updateQty(String key, String qty, String orgqty) {
        int oqt = Integer.parseInt(orgqty);
        int qt = Integer.parseInt(qty);
        oqt = oqt - qt;
        db.child("Items").child(key).child("eqty").setValue(String.valueOf(oqt));
        adapter.notifyDataSetChanged();
    }

    public void updateCust(String key, String amt, String orgamt) {

        double org = roundOff(Double.valueOf(orgamt));
        double am = roundOff(Double.valueOf(amt));
        double samt = totamount - am;
        org = org + samt;
        final String k = key;
        final double tot = totamount;
        final double balance = org;
        final double paid = am;
        dbc.child("Person").child(key).child("emoney").setValue(String.valueOf(org));
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        dbc.child("Person").child(key).child("edate").setValue(formattedDate);

        dbc.child("Person").child(key).child("log").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                dbc.child("Person").child(k).child("log").setValue(value + "[" + format.format(calendar.getTime())
                        + "] Transaction. Total:" + tot + " Paid:" + paid + " Balance:" + balance + "\n");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

    public double roundOff(double d) {
        d = Math.round(d * 100.0);
        d = d / 100.0;
        return d;
    }

}
