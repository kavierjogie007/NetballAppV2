package com.example.netballapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private int selectedPosition = RecyclerView.NO_POSITION;

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

        // Highlight selected item
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onPlayerClick(player);
        });
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

    public void removePlayer(Player player) {
        int index = playerList.indexOf(player);
        if (index != -1) {
            playerList.remove(index);
            notifyItemRemoved(index);

            if (selectedPosition == index) {
                selectedPosition = RecyclerView.NO_POSITION;
            } else if (selectedPosition > index) {
                selectedPosition--;
            }
        }
    }
}
