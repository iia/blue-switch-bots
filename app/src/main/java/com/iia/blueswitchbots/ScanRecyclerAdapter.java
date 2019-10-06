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
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ScanRecyclerAdapter extends RecyclerView.Adapter<ScanRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private Fragment mFragment;
    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private SharedPreferences mPrefsBots;

    ScanRecyclerAdapter(Context context, Fragment fragment, ArrayList<String> data) {
        mData = data;
        mContext = context;
        mFragment = fragment;
        mInflater = LayoutInflater.from(context);

        mPrefsBots =
            context.getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES_TAG_BOTS,
                context.MODE_PRIVATE
            );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMac;
        TextView textViewName;
        ImageButton imageButtonAddBot;

        ViewHolder(View itemView) {
            super(itemView);

            textViewMac = itemView.findViewById(R.id.text_view_mac);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageButtonAddBot = itemView.findViewById(R.id.image_button_add);

            imageButtonAddBot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mPrefsBots.contains(textViewMac.getText().toString())) {
                            if (!((ScanFragment) mFragment).getIsDialogOnScreenBotExists()) {
                                AlertDialog dialog =
                                    ((ScanFragment) mFragment).getDialogBotExists(mContext);

                                dialog.show();

                                ((ScanFragment) mFragment).setIsDialogOnScreenBotExists(true);
                            }
                        }
                        else {
                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put(
                                    Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY,
                                    new String()
                                );

                                jsonObject.put(
                                        Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME,
                                        textViewName.getText()
                                );

                                jsonObject.put(
                                        Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC,
                                        textViewMac.getText().toString()
                                );

                                jsonObject.put(
                                    Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED,
                                    new Boolean(false)
                                );
                            }
                            catch (JSONException exception) {}

                            mPrefsBots.edit().putString(
                                textViewMac.getText().toString(),
                                jsonObject.toString()
                            ).commit();

                            Toast.makeText(
                                mContext,
                                String.format("Added Bot: %s", textViewName.getText()),
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
        String deviceMac = mData.get(position);

        holder.textViewMac.setText(deviceMac);
        holder.textViewName.setText(
            TextUtils.concat(
                "Bot-",
                holder.textViewMac.getText().toString().replace(":",""))
        );
    }
}
