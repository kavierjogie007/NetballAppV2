package com.example.netballapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.netballapp.Model.Game;
import com.example.netballapp.R;

import java.util.List;

public class GameAdapter extends BaseAdapter
{
    public interface GameActionListener {
        void onView(Game game, int position);
        void onUpdate(Game game, int position);
        void onDelete(Game game, int position);
    }

    private Context context;
    private List<Game> games;
    private GameActionListener listener;

    public GameAdapter(Context context, List<Game> games, GameActionListener listener) {
        this.context = context;
        this.games = games;
        this.listener = listener;
    }

    @Override
    public int getCount() { return games.size(); }

    @Override
    public Object getItem(int position) { return games.get(position); }

    @Override
    public long getItemId(int position) { return games.get(position).getGame_ID(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.game_row, parent, false);
        }

        Game game = games.get(position);

        TextView gameName = convertView.findViewById(R.id.gameName);
        TextView opposition = convertView.findViewById(R.id.opposition);
        TextView gameDate = convertView.findViewById(R.id.gameDate);

        Button viewBtn = convertView.findViewById(R.id.viewBtn);
        Button updateBtn = convertView.findViewById(R.id.updateBtn);
        Button deleteBtn = convertView.findViewById(R.id.deleteBtn);

        gameName.setText("Game Name: " + game.getGame_Name());
        opposition.setText("Opposition: " + game.getGame_OppositionName());
        gameDate.setText("Date: " + game.getGame_Date());

        viewBtn.setOnClickListener(v -> listener.onView(game, position));
        updateBtn.setOnClickListener(v -> listener.onUpdate(game, position));
        deleteBtn.setOnClickListener(v -> listener.onDelete(game, position));

        return convertView;
    }
}
