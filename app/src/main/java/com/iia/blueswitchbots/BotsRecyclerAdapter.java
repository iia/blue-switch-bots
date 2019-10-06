/**
 * Blue Switch Bots
 * Copyright (C) 2019 Ishraq Ibne Ashraf <ishraq.i.ashraf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package com.iia.blueswitchbots;

import android.view.View;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BotsRecyclerAdapter extends RecyclerView.Adapter<BotsRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private Fragment mFragment;
    private ArrayList<Bot> mData;
    private LayoutInflater mInflater;
    private SharedPreferences mPrefsBots;
    private FragmentActivity mFragmentActivity;

    BotsRecyclerAdapter(
        Context context, Fragment fragment, FragmentActivity fragmentActivity, ArrayList<Bot> data
    )
    {
        mData = data;
        mContext = context;
        mFragment = fragment;
        mFragmentActivity = fragmentActivity;
        mInflater = LayoutInflater.from(context);
        mPrefsBots =
            fragmentActivity.getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES_TAG_BOTS,
                context.MODE_PRIVATE
            );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        String key;
        Boolean isEnabled;
        TextView textViewMac;
        TextView textViewName;
        ImageView imageViewIsEnabled;
        ImageButton imageButtonBotDelete;
        ImageButton imageButtonBotSettings;

        ViewHolder(View itemView) {
            super(itemView);

            textViewMac = itemView.findViewById(R.id.text_view_mac);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageViewIsEnabled = itemView.findViewById(R.id.image_view_is_enabled);
            imageButtonBotDelete = itemView.findViewById(R.id.image_button_delete);
            imageButtonBotSettings = itemView.findViewById(R.id.image_button_settings);

            imageButtonBotSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog =
                            ((BotsFragment) mFragment).getDialogBotSettings(
                                mContext,
                                mFragmentActivity,
                                textViewMac.getText().toString(),
                                ""
                            );

                        dialog.show();

                        ((BotsFragment) mFragment).setIsDialogOnScreenBotSettings(true);
                    }
                }
            );

            imageButtonBotDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPrefsBots.contains(textViewMac.getText().toString())) {
                            AlertDialog dialog =
                                ((BotsFragment) mFragment).getDialogBotRemove(
                                    mContext,
                                    textViewMac.getText().toString()
                                );

                            dialog.show();

                            ((BotsFragment) mFragment).setIsDialogOnScreenBotRemove(true);
                        }
                    }
                }
            );
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bots_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bot bot = mData.get(position);

        holder.key = bot.getKey();
        holder.isEnabled = bot.getIsEnabled();
        holder.textViewMac.setText(bot.getMac());
        holder.textViewName.setText(bot.getName());

        if (holder.isEnabled) {
            holder.imageViewIsEnabled.setImageResource(
                R.drawable.ic_blue_switch_bots_24dp
            );
        }
        else {
            holder.imageViewIsEnabled.setImageResource(
                R.drawable.ic_blue_switch_bots_disabled_24dp
            );
        }
    }
}
