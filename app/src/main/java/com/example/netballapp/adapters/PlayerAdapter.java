package com.example.netballapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netballapp.Model.Player;
import com.example.netballapp.R;
import com.example.netballapp.activities.UpdatePlayerProfile;
import com.example.netballapp.listeners.OnPlayerActionListener;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private final Context context;
    private final List<Player> playerList;
    private final OnPlayerActionListener listener;

    public PlayerAdapter(Context context, List<Player> playerList, OnPlayerActionListener listener) {
        this.context = context;
        this.playerList = playerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_row, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);

        holder.txtFirstName.setText(player.getPlayer_FirstName());
        holder.txtSurname.setText(player.getPlayer_Surname());

        // Update button click
        holder.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdatePlayerProfile.class);
            intent.putExtra("player_id", player.getPlayer_ID());
            context.startActivity(intent);
        });

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            listener.onDeletePlayer(player, position);
            removePlayer(position);
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView txtFirstName, txtSurname;
        Button btnUpdate, btnDelete;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFirstName = itemView.findViewById(R.id.txtFirstName);
            txtSurname = itemView.findViewById(R.id.txtSurname);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Method to remove player from list
    public void removePlayer(int position) {
        if (position >= 0 && position < playerList.size()) {
            playerList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Player> getPlayers() {
        return playerList;
    }
}
