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

import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import org.json.JSONObject;
import android.widget.Toast;
import org.json.JSONException;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ScanRecyclerAdapter extends RecyclerView.Adapter<ScanRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private SharedPreferences mPrefsBots;
    private ArrayList<BluetoothDevice> mData;

    ScanRecyclerAdapter(Context context, ArrayList<BluetoothDevice> data) {
        mData = data;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPrefsBots =
            context.getApplicationContext().getSharedPreferences(
                Constants.PREFS_TAG_BOTS,
                context.MODE_PRIVATE
            );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mac;
        TextView name;
        ImageButton addBot;

        ViewHolder(View itemView) {
            super(itemView);

            mac = itemView.findViewById(R.id.text_view_mac);
            name = itemView.findViewById(R.id.edit_text_name);
            addBot = itemView.findViewById(R.id.image_button_add);

            addBot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mPrefsBots.contains(mac.getText().toString())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder
                                .setCancelable(false)
                                .setTitle(R.string.dialog_title_attention)
                                .setIcon(R.drawable.ic_attention_black_24dp)
                                .setMessage(R.string.dialog_message_bot_exists)
                                .setPositiveButton(
                                    R.string.dialog_positive_button,
                                    null
                                );

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else {
                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put(
                                    Constants.PREFS_TAG_BOTS_JSON_KEY_KEY,
                                    new String()
                                );

                                jsonObject.put(
                                        Constants.PREFS_TAG_BOTS_JSON_KEY_MAC,
                                        mac.getText().toString()
                                );

                                jsonObject.put(
                                    Constants.PREFS_TAG_BOTS_JSON_KEY_NAME,
                                    name.getText()
                                );

                                jsonObject.put(
                                    Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED,
                                    new Boolean(false)
                                );
                            }
                            catch (JSONException exception) {}

                            mPrefsBots.edit().putString(
                                mac.getText().toString(),
                                jsonObject.toString()
                            ).commit();

                            Log.e("ScanRecyclerAdapter", String.format("ADDING BOT = %s", jsonObject.toString()));

                            Toast.makeText(
                                mContext,
                                String.format("Added Bot: %s", name.getText()),
                                Toast.LENGTH_SHORT
                            ).show();
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
        View view = mInflater.inflate(R.layout.scan_list_item, parent, false);

        return new ScanRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice device = mData.get(position);

        holder.mac.setText(device.getAddress());
        holder.name.setText(
            TextUtils.concat(
                "Bot-",
                holder.mac.getText().toString().replace(":",""))
        );
    }
}
