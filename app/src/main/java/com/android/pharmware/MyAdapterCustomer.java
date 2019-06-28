package com.android.pharmware;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MyAdapterCustomer extends RecyclerView.Adapter<MyViewHolderCustomer> implements Filterable {

    Context c;

    String uid;
    ArrayList<Modelcustomer> models, filterList;
    DatabaseReference db;
    private FirebaseAuth mAuth;
    CustomFilterCustomer filter;

    public MyAdapterCustomer(Context c, ArrayList<Modelcustomer> models) {

        this.c = c;
        this.models = models;
        this.filterList = models;
    }

    @Override
    public MyViewHolderCustomer onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.customercard, parent, false);
        return new MyViewHolderCustomer(v);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolderCustomer holder, final int position) {

        holder.item.setText(models.get(position).getEiname());
        holder.date.setText(models.get(position).getEdate());
        holder.money.setText(models.get(position).getEmoney());
        holder.addr.setText(models.get(position).getEaddr());
        holder.phone.setText(models.get(position).getEphoneNo());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.gone.getVisibility() == View.VISIBLE) {
                    holder.gone.setVisibility(View.GONE);
                } else {
                    holder.gone.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, UpdateCustomer.class);
                i.putExtra("Keys", models.get(position).getKey());
                c.startActivity(i);
            }
        });
        holder.log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, CustomerLog.class);
                i.putExtra("k", models.get(position).getKey());
                c.startActivity(i);

            }
        });
        holder.delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String item = models.get(position).getEiname();
                mAuth = FirebaseAuth.getInstance();
                uid = mAuth.getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer")
                        .child("Person").child(models.get(position).getKey());
                db.keepSynced(true);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete this customer?");
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
                                                                    + " Customer Deleted\n" + l);
                                                } else {
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Customer Deleted\n\n" + l);
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
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item = models.get(position).getEiname();
                mAuth = FirebaseAuth.getInstance();
                uid = mAuth.getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer")
                        .child("Person").child(models.get(position).getKey());
                db.keepSynced(true);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        final EditText edittext = new EditText(c);
                        edittext.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

                        GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                        };
                        final Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        edittext.setMaxLines(1);
                        edittext.setHint("Enter amount");
                        // alert.setMessage("Enter amount :");
                        // alert.setTitle("Enter amount :");

                        alert.setView(edittext);

                        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // What ever you want to do with the value
                                final Editable value = edittext.getText();
                                if (!value.toString().trim().isEmpty()) {
                                    BigDecimal a = new BigDecimal(value.toString());
                                    BigDecimal due = BigDecimal.valueOf(Double.parseDouble(map.get("emoney"))).add(a);

                                    db.child("emoney").setValue(due.toString());

                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                    String formattedDate = df.format(c.getTime());
                                    db.child("edate").setValue(formattedDate);

                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                    db.child("log").setValue(map.get("log") + "[" + format.format(calendar.getTime())
                                            + "] (" + value.toString() + ") Balance:" + due.toString() + "\n");

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
                                                                        + fm.format(calendar.getTime()) + "] "
                                                                        + map.get("einame")
                                                                        + " Customer Balance update ("
                                                                        + value.toString() + ")\n" + l);
                                                    } else {
                                                        db.getParent().getParent().getParent().child("Log").child("log")
                                                                .setValue(format.format(calendar.getTime()) + "\n["
                                                                        + fm.format(calendar.getTime()) + "] "
                                                                        + map.get("einame")
                                                                        + " Customer Balance update ("
                                                                        + value.toString() + ")\n\n" + l);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            });

                                    notifyDataSetChanged();
                                }

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                                dialog.dismiss();
                            }
                        });
                        AlertDialog b = alert.create();
                        b.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        b.show();
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
            filter = new CustomFilterCustomer(filterList, this);
        }
        return filter;
    }
}
