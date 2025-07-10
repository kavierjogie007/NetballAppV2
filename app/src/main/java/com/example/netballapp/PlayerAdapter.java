package com.example.netballapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class PlayerAdapter extends BaseAdapter {
    private Context context;
    private List<Player> playerList;

    public PlayerAdapter(Context context, List<Player> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playerList.get(position).getPlayer_ID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = View.inflate(context, R.layout.player_row, null);

        TextView txtFirst = view.findViewById(R.id.txtFirstName);
        TextView txtSurname = view.findViewById(R.id.txtSurname);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        Player p = playerList.get(position);
        txtFirst.setText(p.getPlayer_FirstName());
        txtSurname.setText(p.getPlayer_Surname());

        btnUpdate.setOnClickListener(v -> {
            // handle update
        });

        btnDelete.setOnClickListener(v -> {
            // handle delete
        });

        return view;
    }
}

