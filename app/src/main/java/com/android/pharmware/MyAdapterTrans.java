package com.android.pharmware;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MyAdapterTrans extends RecyclerView.Adapter<MyViewHolderTrans> implements Filterable {

    Context c;

    String uid;
    ArrayList<Modeltrans> models, filterList;
    DatabaseReference db;
    private FirebaseAuth mAuth;
    CustomFilterTrans filter;

    public MyAdapterTrans(Context c, ArrayList<Modeltrans> models) {

        this.c = c;
        this.models = models;
        this.filterList = models;
    }

    @Override
    public MyViewHolderTrans onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.transcard, parent, false);
        return new MyViewHolderTrans(v);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolderTrans holder, final int position) {

        holder.trans.setText(models.get(position).getGtranid());
        holder.date.setText(models.get(position).getGdate());
        holder.money.setText(models.get(position).getGtotamt());
        holder.custname.setText(models.get(position).getGpt());

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Invoice").child("Bill")
                .child(models.get(position).getKey());
        db.keepSynced(true);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, Bill.class);
                i.putExtra("key", models.get(position).getKey());
                c.startActivity(i);

            }
        });

        holder.delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String item = models.get(position).getGtranid();
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete this transaction?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSnapshot.getRef().removeValue();
                                dialog.dismiss();
                                models.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, models.size());

                                // log
                                db.getParent().getParent().getParent().child("Log").child("log")
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
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Transaction Deleted\n" + l);
                                                } else {
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Transaction Deleted\n\n" + l);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });

                                Toast.makeText(c, item + " has been removed succesfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }

        });

    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new CustomFilterTrans(filterList, this);
        }
        return filter;
    }
}
