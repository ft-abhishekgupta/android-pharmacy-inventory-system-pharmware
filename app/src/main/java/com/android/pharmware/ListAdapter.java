package com.android.pharmware;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListModel> {

    Context context;
    ArrayList<ListModel> listModels = new ArrayList<>();
    int res;

    public ListAdapter(Context context, int resource, ArrayList<ListModel> listModels) {
        super(context, resource, listModels);
        this.res = resource;
        this.listModels = listModels;
        this.context = context;
    }

    private class ViewHolder {

        TextView med, pr, amt;
        Button del, qty;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder = null;
        final ListModel model = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(res, null);
            holder = new ViewHolder();
            holder.med = (TextView) convertView.findViewById(R.id.med);
            holder.pr = (TextView) convertView.findViewById(R.id.pr);
            holder.qty = (Button) convertView.findViewById(R.id.qty);
            holder.amt = (TextView) convertView.findViewById(R.id.amt);
            holder.del = (Button) convertView.findViewById(R.id.btndel);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.med.setText(model.getMed());
        Double up = roundOff(Double.parseDouble(model.getPr()) / Double.parseDouble(model.getUnit()));
        holder.pr.setText(up + "");
        holder.qty.setText(model.getQty());
        holder.amt.setText(model.getAmount());
        final String a = holder.amt.getText().toString();
        final String p = holder.pr.getText().toString();
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Trans.totamount = Trans.totamount - Double.valueOf(a);
                Trans.totamount = roundOff(Trans.totamount);
                Trans.totamt.setText(String.valueOf(Trans.totamount));
                Trans.gen.setText("GENERATE BILL (₹" + Trans.totamount + ")");
                remove(getItem(position));
                notifyDataSetChanged();
                ListView lv = (ListView) parent.findViewById(R.id.list);
                Utility.setListViewHeightBasedOnChildren(lv);
            }
        });

        holder.qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.input_dia, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

                alertDialogBuilder.setCancelable(false).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String qt = editText.getText().toString().trim();

                        if (!qt.equals("") && !qt.equals("0")) {
                            if (Integer.parseInt(qt) <= Integer.parseInt(model.getOrgqty())) {
                                Double u = Double.valueOf(model.getUnit());
                                double am = Double.valueOf(qt) * Double.valueOf(p);
                                am = roundOff(am);
                                Trans.totamount = (Trans.totamount - Double.valueOf(a)) + am;
                                Trans.totamount = roundOff(Trans.totamount);
                                model.setQty(qt);
                                model.setAmount(String.valueOf(am));
                                Trans.totamt.setText(String.valueOf(Trans.totamount));
                                Trans.gen.setText("GENERATE BILL (₹" + Trans.totamount + ")");
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            else {
                                Toast.makeText(context, "Quantity not in stock", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Enter valid quantity", Toast.LENGTH_SHORT).show();
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
            }
        });

        return convertView;
    }

    public double roundOff(double d) {
        d = Math.round(d * 100.0);
        d = d / 100.0;
        return d;
    }
}
