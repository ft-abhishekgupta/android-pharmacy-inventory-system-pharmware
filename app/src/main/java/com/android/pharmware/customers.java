package com.android.pharmware;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.android.pharmware.MainActivity.fab;

public class customers extends Fragment implements SearchView.OnQueryTextListener {
    View v;
    String uid;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    MyAdapterCustomer adapter;
    private RecyclerView rv;
    ArrayList<Modelcustomer> models = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    boolean order = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.customers, container, false);
        getActivity().setTitle("Customers");
        MainActivity.flag = 1;
        setHasOptionsMenu(true);

        // INITIALIZE RV
        rv = (RecyclerView) v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        // layoutManager.setReverseLayout(true);
        // layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer");
        db.keepSynced(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Customer Fab", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                Intent i = new Intent(getContext(), Newcustomer.class);
                startActivity(i);
            }
        });
        fab.show();
        // ADAPTER
        adapter = new MyAdapterCustomer(getContext(), retrieve());
        rv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_updateinfo).setVisible(false);
        menu.findItem(R.id.order_ename).setVisible(false);
        menu.findItem(R.id.order_qty).setVisible(false);
        menu.findItem(R.id.order_exp).setVisible(false);
        // menu.findItem(R.id.order_transdate).setVisible(false);
        menu.findItem(R.id.order_due).setVisible(true);
        inflater.inflate(R.menu.search_bar, menu);
        final MenuItem searchitem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.findItem(R.id.order_due).setVisible(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.order_due).setVisible(false);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.order_due) {
            if (!item.isChecked()) {
                if (models.size() > 0) {
                    Collections.sort(models, new Comparator<Modelcustomer>() {
                        @Override
                        public int compare(Modelcustomer p1, Modelcustomer p2) {
                            return Double.compare(Double.parseDouble(p2.getEmoney()),
                                    Double.parseDouble(p1.getEmoney()));
                        }
                    });
                }
                order = true;
                item.setChecked(true);
                adapter.notifyDataSetChanged();
            } else {
                if (models.size() > 0) {
                    Collections.sort(models, new Comparator<Modelcustomer>() {
                        @Override
                        public int compare(final Modelcustomer object1, final Modelcustomer object2) {
                            return object1.getEiname().compareTo(object2.getEiname());
                        }
                    });
                }
                order = false;
                item.setChecked(false);
                adapter.notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        adapter.getFilter().filter(newText);
        return false;
    }

    // IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot) {
        models.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Modelcustomer model = ds.getValue(Modelcustomer.class);
            models.add(model);
        }
        if (order) {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Modelcustomer>() {
                    @Override
                    public int compare(Modelcustomer p1, Modelcustomer p2) {
                        return Double.compare(Double.parseDouble(p2.getEmoney()), Double.parseDouble(p1.getEmoney()));
                    }
                });
            }
        } else {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Modelcustomer>() {
                    @Override
                    public int compare(final Modelcustomer object1, final Modelcustomer object2) {
                        return object1.getEiname().compareTo(object2.getEiname());
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    // READ BY HOOKING ONTO DATABASE OPERATION CALLBACKS
    public ArrayList<Modelcustomer> retrieve() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (models.size() == 1) {
                    models.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return models;
    }

}
