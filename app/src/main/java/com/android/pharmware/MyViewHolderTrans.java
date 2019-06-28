package com.android.pharmware;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyViewHolderTrans extends RecyclerView.ViewHolder {

    TextView trans, date, money, custname;
    ImageView delbtn;
    LinearLayout gone;
    CardView cv;

    public MyViewHolderTrans(View itemView) {
        super(itemView);

        cv = (CardView) itemView.findViewById(R.id.cv);

        trans = (TextView) itemView.findViewById(R.id.id);
        date = (TextView) itemView.findViewById(R.id.date);
        money = (TextView) itemView.findViewById(R.id.money);
        custname = (TextView) itemView.findViewById(R.id.custname);

        delbtn = (ImageView) itemView.findViewById(R.id.deletebutton);
    }
}
