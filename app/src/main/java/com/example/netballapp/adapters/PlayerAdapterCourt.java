package com.example.netballapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netballapp.Model.Player;
import com.example.netballapp.R;
import com.example.netballapp.listeners.OnPlayerClickListener;

import java.util.List;

public class PlayerAdapterCourt extends RecyclerView.Adapter<PlayerAdapterCourt.PlayerViewHolder>
{
    public final List<Player> playerList;
    private final Context context;
    private OnPlayerClickListener listener;

    public PlayerAdapterCourt(List<Player> playerList, Context context, OnPlayerClickListener listener) {
        this.playerList = playerList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_row2,parent,false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapterCourt.PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);

        holder.lblName.setText(player.getPlayer_FirstName());
        holder.lblSurname.setText(player.getPlayer_Surname());

        holder.itemView.setOnClickListener(v -> listener.onPlayerClick(player));
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder
    {
        public TextView lblName;
        public TextView lblSurname;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            lblName= itemView.findViewById(R.id.txtFirstName);
            lblSurname=itemView.findViewById(R.id.txtSurname);
        }
    }
}
