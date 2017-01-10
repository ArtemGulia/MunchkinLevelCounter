package com.g_art.munchkinlevelcounter.listadapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.g_art.munchkinlevelcounter.R;
import com.g_art.munchkinlevelcounter.listadapter.helper.ItemTouchHelperAdapter;
import com.g_art.munchkinlevelcounter.listadapter.helper.ItemTouchHelperViewHolder;
import com.g_art.munchkinlevelcounter.listadapter.helper.OnStartDragListener;
import com.g_art.munchkinlevelcounter.model.Player;
import com.g_art.munchkinlevelcounter.model.Sex;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by agulia on 11/27/16.
 */

public class NewPlayersRecyclerAdapter extends RecyclerView.Adapter<NewPlayersRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {

	private final OnStartDragListener mDragStartListener;
	private ArrayList<Player> mPlayers;
	public static int color;

	public NewPlayersRecyclerAdapter(ArrayList<Player> mPlayers, OnStartDragListener dragStartListener) {
		this.mDragStartListener = dragStartListener;
		this.mPlayers = mPlayers;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.new_players_row, parent, false);
		color = parent.getContext().getResources().getColor(R.color.transparent_main_color);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		if (mPlayers.isEmpty()) {
			holder.text.setText(R.string.no_players);

			holder.imBtnPlayerSex.setVisibility(View.GONE);
		} else {

			final Player player = mPlayers.get(holder.getAdapterPosition());

			holder.text.setText(player.getName());
			holder.text.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if (!s.toString().isEmpty()) {
						mPlayers.get(holder.getAdapterPosition()).setName(s.toString());
					}
				}
			});

			holder.text.setFocusableInTouchMode(true);

			holder.imBtnPlayerSex.setVisibility(View.VISIBLE);
			holder.imBtnPlayerSex.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playerChangeSex(holder.getAdapterPosition());
				}
			});

			if (player.getSex() == Sex.MAN) {
				holder.imBtnPlayerSex.setImageResource(R.drawable.man);
			} else {
				holder.imBtnPlayerSex.setImageResource(R.drawable.woman);
			}
		}
	}

	private void playerChangeSex(int playerPosition) {
		final Player player = mPlayers.get(playerPosition);
		if (Sex.MAN == player.getSex()) {
			player.setSex(Sex.WOMAN);
		} else {
			player.setSex(Sex.MAN);
		}
		notifyItemChanged(playerPosition);
	}

	@Override
	public int getItemCount() {
		return mPlayers.size();
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		Collections.swap(mPlayers, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		return true;
	}

	@Override
	public void onItemDismiss(int position) {
		mPlayers.remove(position);
		notifyItemRemoved(position);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder  implements
			ItemTouchHelperViewHolder {

		private EditText text;
		private ImageButton imBtnPlayerSex;
		private ImageView imReorder;

		public ViewHolder(View itemView) {
			super(itemView);

			text = (EditText) itemView.findViewById(R.id.newPlayerName);
			imBtnPlayerSex = (ImageButton) itemView.findViewById(R.id.imvPlayerSex);
			imReorder = (ImageView) itemView.findViewById(R.id.reorder);
			final Drawable icon;
			if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				icon = VectorDrawableCompat.create(itemView.getContext().getResources(), R.drawable.ic_reorder_24dp, itemView.getContext().getTheme());
			} else {
				icon = itemView.getContext().getResources().getDrawable(R.drawable.ic_reorder_24dp, itemView.getContext().getTheme());
			}
			imReorder.setImageDrawable(icon);
		}

		@Override
		public void onItemSelected() {
			itemView.setBackgroundColor(color);
		}

		@Override
		public void onItemClear() {
			itemView.setBackgroundColor(0);
		}
	}
}
