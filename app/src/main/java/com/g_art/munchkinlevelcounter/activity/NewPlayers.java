package com.g_art.munchkinlevelcounter.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.g_art.munchkinlevelcounter.R;
import com.g_art.munchkinlevelcounter.application.MyApplication;
import com.g_art.munchkinlevelcounter.fragments.dialog.PlayerNameDialog;
import com.g_art.munchkinlevelcounter.listadapter.NewPlayersRecyclerAdapter;
import com.g_art.munchkinlevelcounter.listadapter.helper.OnStartDragListener;
import com.g_art.munchkinlevelcounter.listadapter.helper.SimpleItemTouchHelperCallback;
import com.g_art.munchkinlevelcounter.model.Player;
import com.g_art.munchkinlevelcounter.model.Sex;
import com.g_art.munchkinlevelcounter.util.StoredPlayers;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by G_Art on 16/7/2014.
 */
public class NewPlayers extends AppCompatActivity implements View.OnClickListener, OnStartDragListener {
    private final static String PLAYER_KEY = "playersList";
    public final static String PLAYER_NAME = "playerName";
    public static final String PLAYER_SEX = "player_sex";
    private Tracker mTracker;
    private String PREFS_NO_DATA;
    private DialogFragment playerDialog;
    private StoredPlayers mStored;
	private ArrayList<Player> mPlayers;

	private RecyclerView mRecyclerView;
	private NewPlayersRecyclerAdapter mAdapter;
	private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_players);

        // Obtain the shared Tracker instance.
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setAction("Setting Players")
                .setCategory("Screen")
                .setLabel("NewPlayers")
                .build());

        PREFS_NO_DATA = getString(R.string.no_data);

        mStored = StoredPlayers.getInstance(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

		mRecyclerView = (RecyclerView)findViewById(R.id.listNewPlayers);
        ImageButton btnAddPlayers = (ImageButton) findViewById(R.id.imgBtnAddPlayer);

        btnAddPlayers.setOnClickListener(this);
        ImageButton btnClear = (ImageButton) findViewById(R.id.imgBtnClear);
        btnClear.setOnClickListener(this);
        ImageButton btnStartGame = (ImageButton) findViewById(R.id.imgBtnStart);
        btnStartGame.setOnClickListener(this);
		mPlayers = initPlayers(savedInstanceState);

		mAdapter = new NewPlayersRecyclerAdapter(mPlayers, this);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
		mItemTouchHelper = new ItemTouchHelper(callback);
		mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

	private ArrayList<Player> initPlayers(Bundle savedInstanceState) {
		final ArrayList<Player> playerList;
		if (savedInstanceState == null || !savedInstanceState.containsKey(PLAYER_KEY)) {
			if (mStored.isPlayersStored()) {
				playerList = getPlayersForNewGame();
			} else {
				playerList = new ArrayList<>();
				playerList.add(new Player(getString(R.string.player_1)));
				playerList.add(new Player(getString(R.string.player_2)));
			}
		} else {
			playerList = savedInstanceState.getParcelableArrayList(PLAYER_KEY);
		}

		return playerList;
	}

	private ArrayList<Player> getPlayersForNewGame() {
        final ArrayList<Player> tList = mStored.loadPlayers(PREFS_NO_DATA);
        if (!tList.isEmpty()) {
			return clearStats(tList);
        }
		return new ArrayList<>();
    }

	private ArrayList<Player> clearStats(ArrayList<Player> tList) {
		ArrayList<Player> pList = new ArrayList<>(tList.size());
		for (Player pl : tList) {
			pList.add(pl.cloneWithoutStats());
		}
		return pList;
	}

	private void showPlayerNameDialog(String name) {
        showPlayerNameDialog(name, Sex.MAN);
    }

	private void showPlayerNameDialog(String name, Sex sex) {
        playerDialog = new PlayerNameDialog();
        Bundle nBundle = new Bundle();
        nBundle.putString(PLAYER_NAME, name);
        nBundle.putSerializable(PLAYER_SEX, sex);
        playerDialog.setArguments(nBundle);
        playerDialog.show(getFragmentManager(), "dialog");
    }

	public void doPositiveClickPlayerNameDialog(String name, Sex sex) {
		Player newPlayer = new Player(name);
		newPlayer.setSex(sex);

		mPlayers.add(newPlayer);

		mAdapter.notifyItemInserted(mPlayers.size());
		playerDialog.dismiss();
		playerDialog = null;
	}

    public void doNegativeClickPlayerNameDialog() {
		playerDialog.dismiss();
		playerDialog = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PLAYER_KEY, mPlayers);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnAddPlayer:
                showPlayerNameDialog("");
                break;
            case R.id.imgBtnClear:
				removePlayers();
                break;
            case R.id.imgBtnStart:
				int MIN_PLAYER_QUANTITY = 1;
				if (mPlayers.size() < MIN_PLAYER_QUANTITY) {
                    Toast.makeText(this, getString(R.string.more_players), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putParcelableArrayListExtra(PLAYER_KEY, mPlayers);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setAction("Starting Game")
                            .setCategory("Action")
                            .setLabel("Number of players")
                            .setValue(mPlayers.size())
                            .build());

                    startActivity(intent);
                }
                break;
        }
    }

	private void removePlayers() {
		int playersCount = mPlayers.size();
		mPlayers.clear();

		mAdapter.notifyItemRangeRemoved(0, playersCount);
		mTracker.send(new HitBuilders.EventBuilder()
				.setAction("ClearPlayers")
				.setCategory("Action")
				.setLabel("MyActivity")
				.build());
	}

	@Override
    protected void onDestroy() {
        if (playerDialog != null) {
            playerDialog = null;
        }

        super.onDestroy();
    }

	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		mItemTouchHelper.startDrag(viewHolder);
	}
}
