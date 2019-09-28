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

import android.os.Bundle;
import android.view.View;
import android.app.Dialog;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class BotSettingsDialogFragment extends DialogFragment {
    private String mBotMAC;
    private String mBotKey;
    private String mBotName;
    private Boolean mIsEnabled;

    public BotSettingsDialogFragment(
        Boolean isEnabled,
        String botMAC,
        String botName,
        String botKey
    )
    {
        super();

        mBotMAC = botMAC;
        mBotKey = botKey;
        mBotName = botName;
        mIsEnabled = isEnabled;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogView =
            getActivity().getLayoutInflater().inflate(R.layout.fragment_bot_settings,null);

        final TextView textViewMAC = dialogView.findViewById(R.id.text_view_mac);
        final TextView textViewKey = dialogView.findViewById(R.id.edit_text_key);
        final TextView textViewName = dialogView.findViewById(R.id.edit_text_name);
        final CheckBox checkBoxIsEnabled = dialogView.findViewById(R.id.check_box_is_enabled);

        textViewMAC.setText(mBotMAC);
        textViewKey.setText(mBotKey);
        textViewName.setText(mBotName);
        checkBoxIsEnabled.setChecked(mIsEnabled);

        builder
            .setView(dialogView)
            .setCancelable(false)
            .setIcon(R.drawable.ic_settings_black_24dp)
            .setTitle(R.string.dialog_title_bot_settings)
            .setPositiveButton(
                R.string.dialog_positive_button_bot_settings,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        JSONObject jsonObject;
                        SharedPreferences mPrefsBots =
                            getContext().getApplicationContext().getSharedPreferences(
                                Constants.PREFS_TAG_BOTS,
                                getContext().MODE_PRIVATE
                            );

                        try {
                            jsonObject = new JSONObject(mPrefsBots.getString(mBotMAC, new String()));

                            jsonObject.put(
                                Constants.PREFS_TAG_BOTS_JSON_KEY_MAC,
                                textViewMAC.getText()
                            );

                            jsonObject.put(
                                Constants.PREFS_TAG_BOTS_JSON_KEY_NAME,
                                textViewName.getText()
                            );

                            jsonObject.put(
                                Constants.PREFS_TAG_BOTS_JSON_KEY_KEY,
                                textViewKey.getText()
                            );

                            jsonObject.put(
                                Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED,
                                checkBoxIsEnabled.isChecked()
                            );

                            mPrefsBots.edit().putString(mBotMAC, jsonObject.toString()).commit();
                        }
                        catch (JSONException exception) {}
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

        return builder.create();
    }
}
