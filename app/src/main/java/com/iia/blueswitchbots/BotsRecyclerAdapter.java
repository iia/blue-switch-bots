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

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BotsRecyclerAdapter extends RecyclerView.Adapter<BotsRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Bot> mData;
    private LayoutInflater mInflater;
    private SharedPreferences mPrefsBots;
    private FragmentActivity mFragmentActivity;

    BotsRecyclerAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<Bot> data) {
        mData = data;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFragmentActivity = fragmentActivity;
        mPrefsBots =
            fragmentActivity.getApplicationContext().getSharedPreferences(
                Constants.PREFS_TAG_BOTS,
                context.MODE_PRIVATE
            );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        String key;
        TextView mac;
        TextView name;
        Boolean isEnabled;
        ImageButton botDelete;
        ImageButton botSettings;
        ImageView imageViewIsEnabled;

        ViewHolder(View itemView) {
            super(itemView);

            mac = itemView.findViewById(R.id.text_view_mac);
            name = itemView.findViewById(R.id.edit_text_name);
            botDelete = itemView.findViewById(R.id.image_button_delete);
            botSettings = itemView.findViewById(R.id.image_button_settings);
            imageViewIsEnabled = itemView.findViewById(R.id.imageView2);

            botSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment botSettingsDialogFragment =
                            new BotSettingsDialogFragment(
                                isEnabled,
                                mac.getText().toString(),
                                name.getText().toString(),
                                key
                            );

                        botSettingsDialogFragment.show(
                            mFragmentActivity.getSupportFragmentManager(),
                            Constants.BOTS_TAG_SETTINGS_DIALOG_FRAGMENT
                        );
                    }
                }
            );

            botDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPrefsBots.contains(mac.getText().toString())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder
                                .setCancelable(false)
                                .setTitle(R.string.dialog_title_attention)
                                .setIcon(R.drawable.ic_attention_black_24dp)
                                .setMessage(R.string.dialog_message_bot_remove)
                                .setPositiveButton(
                                    R.string.dialog_positive_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            mPrefsBots.edit().remove(mac.getText().toString()).commit();
                                        }
                                    }
                                )
                                .setNegativeButton(
                                    R.string.dialog_negative_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {}
                                    }
                                );

                            AlertDialog alert = builder.create();
                            alert.show();
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
        holder.mac.setText(bot.getMAC());
        holder.name.setText(bot.getName());
        holder.isEnabled = bot.getIsEnabled();

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
