package com.android.pharmware;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView item, btc, qty, mrp, exp, sp, cName, code, cp, desc, unit;
    ImageView editBtn, delbtn;
    LinearLayout gone;
    CardView cv;

    public MyViewHolder(View itemView) {
        super(itemView);

        cv = (CardView) itemView.findViewById(R.id.cv);
        gone = (LinearLayout) itemView.findViewById(R.id.gone_layout);

        item = (TextView) itemView.findViewById(R.id.itemName);
        btc = (TextView) itemView.findViewById(R.id.batchText);
        qty = (TextView) itemView.findViewById(R.id.qtyText);
        mrp = (TextView) itemView.findViewById(R.id.mrpText);
        exp = (TextView) itemView.findViewById(R.id.expText);
        sp = (TextView) itemView.findViewById(R.id.spText);
        cName = (TextView) itemView.findViewById(R.id.cNameText);
        cp = (TextView) itemView.findViewById(R.id.cpText);
        code = (TextView) itemView.findViewById(R.id.codeText);
        desc = (TextView) itemView.findViewById(R.id.descText);
        unit = (TextView) itemView.findViewById(R.id.unitText);

        editBtn = (ImageView) itemView.findViewById(R.id.editbtn);
        delbtn = (ImageView) itemView.findViewById(R.id.delbtn);
    }
}
