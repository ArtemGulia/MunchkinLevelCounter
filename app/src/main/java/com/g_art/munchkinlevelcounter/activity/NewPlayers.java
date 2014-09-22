package com.g_art.munchkinlevelcounter.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.g_art.munchkinlevelcounter.R;
import com.g_art.munchkinlevelcounter.bean.Player;
import com.g_art.munchkinlevelcounter.fragments.dialog.NewPlayerDialog;
import com.g_art.munchkinlevelcounter.listadapter.CustomListAdapter;

import java.util.ArrayList;

/**
 * Created by G_Art on 16/7/2014.
 */
public class NewPlayers extends Activity implements View.OnClickListener {

    final String PLAYER_KEY = "playersList";
    final int MIN_PLAYER_QUANTITY = 2;

    ListView listVPlayers;
    Button btnAddPlayers;
    Button btnClear;
    Button btnStartGame;

    private ArrayList<Player> listPlayers;
    CustomListAdapter customListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_players);

        listVPlayers = (ListView) findViewById(R.id.listVPlayers);
        btnAddPlayers = (Button) findViewById(R.id.btnAddPlayer);

        btnAddPlayers.setOnClickListener(this);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnStartGame = (Button) findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(this);


        if (savedInstanceState == null || !savedInstanceState.containsKey(PLAYER_KEY)) {
            listPlayers = new ArrayList<Player>();
        } else {
            listPlayers = savedInstanceState.getParcelableArrayList(PLAYER_KEY);
        }


        customListAdapter = new CustomListAdapter(this, listPlayers);
        listVPlayers.setAdapter(customListAdapter);
    }


    void showNewPlayerDialog() {
        DialogFragment newFragment = new NewPlayerDialog();
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClickNewPlayerDialog(String name) {
        listPlayers.add(new Player(name));
        customListAdapter.notifyDataSetChanged();
    }

    public void doNegativeClickNewPlayerDialog() {
        // Do stuff here.
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PLAYER_KEY, listPlayers);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPlayer:
                showNewPlayerDialog();
                break;
            case R.id.btnClear:
                listPlayers.clear();
                customListAdapter.notifyDataSetChanged();
                break;
            case R.id.btnStartGame:

                int playersQuant = listPlayers.size();
                if (playersQuant < MIN_PLAYER_QUANTITY) {
                    Toast.makeText(this, "Please, add more players for game", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putParcelableArrayListExtra(PLAYER_KEY, listPlayers);
                    startActivity(intent);
                }

                break;
        }
    }

    /**
     * **************  This function used by adapter ***************
     */
    public void onItemClick(int mPosition) {
        Player player = listPlayers.get(mPosition);

        // SHOW ALERT

        Toast.makeText(this,
                "Name: " + player.getName() +
                        " deleted",
                Toast.LENGTH_SHORT
        ).show();

        listPlayers.remove(mPosition);
        customListAdapter.notifyDataSetChanged();
    }
}
