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

import java.util.Map;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import org.json.JSONObject;
import java.util.ArrayList;
import android.view.MenuItem;
import android.view.ViewGroup;
import org.json.JSONException;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.MenuInflater;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;

public class BotsFragment
    extends Fragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ArrayList<Bot> mBotsList;
    private RecyclerView mRecyclerView;
    private SharedPreferences mPrefsBots;
    private ImageView mImageViewPlaceHolder;
    private Boolean mIsDialogOnScreenBotRemove;
    private AlertDialog mCurrentSettingsDialog;
    private String mIsDialogOnScreenBotRemoveMac;
    private Boolean mIsDialogOnScreenBotSettings;
    private Boolean mIsDialogOnScreenBotRemoveAll;
    private String mIsDialogOnScreenBotSettingsMac;
    private BotsRecyclerAdapter mBotsRecyclerAdapter;
    private String mIsDialogOnScreenBotSettingsTempData;

    public void setIsDialogOnScreenBotRemove(Boolean isDialogOnScreenBotRemove) {
        mIsDialogOnScreenBotRemove = isDialogOnScreenBotRemove;
    }

    public void setIsDialogOnScreenBotSettings(Boolean isDialogOnScreenBotSettings) {
        mIsDialogOnScreenBotSettings = isDialogOnScreenBotSettings;
    }

    public AlertDialog getDialogBotSettings(
        Context context, FragmentActivity fragmentActivity, String mac, String tempData
    )
    {
        JSONObject jsonObject;
        mIsDialogOnScreenBotSettingsMac = mac;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView =
            fragmentActivity.getLayoutInflater().inflate(
                R.layout.fragment_bot_settings,
                null
            );

        final TextView textViewMAC = dialogView.findViewById(R.id.text_view_mac);
        final EditText editTextKey = dialogView.findViewById(R.id.edit_text_key);
        final EditText editTextName = dialogView.findViewById(R.id.edit_text_name);
        final CheckBox checkBoxIsEnabled = dialogView.findViewById(R.id.check_box_is_enabled);

        try {
            if (!tempData.isEmpty()) {
                mIsDialogOnScreenBotSettingsTempData = tempData;

                jsonObject = new JSONObject(mIsDialogOnScreenBotSettingsTempData);
            }
            else {
                mIsDialogOnScreenBotSettingsTempData = "";

                jsonObject =
                    new JSONObject(
                        mPrefsBots.getString(
                            mac,
                            mPrefsBots.getString(mac, new String())
                        )
                    );
            }

            textViewMAC.setText(
                jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC)
            );

            editTextKey.setText(
                jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY)
            );

            editTextName.setText(
                jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME)
            );

            checkBoxIsEnabled.setChecked(
                jsonObject.getBoolean(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED)
            );
        }
        catch (JSONException exception) {}

        builder
            .setView(dialogView)
            .setCancelable(false)
            .setIcon(R.drawable.ic_settings_black_24dp)
            .setTitle(R.string.title_dialog_bot_settings)
            .setPositiveButton(
                R.string.label_dialog_bot_settings_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        JSONObject jsonObject;

                        try {
                            jsonObject =
                                new JSONObject(
                                    mPrefsBots.getString(
                                        mIsDialogOnScreenBotSettingsMac,
                                        new String()
                                    )
                                );

                            jsonObject.put(
                                Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC,
                                textViewMAC.getText()
                            );

                            jsonObject.put(
                                Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME,
                                editTextName.getText()
                            );

                            jsonObject.put(
                                Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY,
                                editTextKey.getText()
                            );

                            jsonObject.put(
                                Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED,
                                checkBoxIsEnabled.isChecked()
                            );

                            mPrefsBots.edit().putString(
                                mIsDialogOnScreenBotSettingsMac,
                                jsonObject.toString()
                            ).commit();
                        }
                        catch (JSONException exception) {}
                    }
                }
            )
            .setNegativeButton(
                R.string.label_dialog_button_negative,
                null
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mCurrentSettingsDialog = null;
                        mIsDialogOnScreenBotSettings = false;
                        mIsDialogOnScreenBotSettingsMac = "";
                        mIsDialogOnScreenBotSettingsTempData = "";
                    }
                }
            );

        mCurrentSettingsDialog = builder.create();

        return mCurrentSettingsDialog;
    }

    public AlertDialog getDialogBotRemove(Context context, final String mac) {
        mIsDialogOnScreenBotRemoveMac = mac;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_bot_remove)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mPrefsBots.edit().remove(mIsDialogOnScreenBotRemoveMac).commit();
                    }
                }
            )
            .setNegativeButton(
                R.string.label_dialog_button_negative,
                null
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenBotRemove = false;
                        mIsDialogOnScreenBotRemoveMac = "";
                    }
                }
            );

        return builder.create();
    }

    public AlertDialog getDialogBotRemoveAll(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_bot_remove_all)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mPrefsBots.edit().clear().commit();

                        mBotsList.clear();
                        mBotsRecyclerAdapter.notifyDataSetChanged();

                        mRecyclerView.setVisibility(View.GONE);
                        mImageViewPlaceHolder.setVisibility(View.VISIBLE);
                    }
                }
            )
            .setNegativeButton(
                R.string.label_dialog_button_negative,
                null
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenBotRemoveAll = false;
                    }
                }
            );

        return builder.create();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Bot foundBot = null;

        for (Bot bot : mBotsList) {
            if (bot.getMac().equals(key)) {
                foundBot = bot;

                break;
            }
        }

        try {
            String jsonString = mPrefsBots.getString(key, new String());

            if ((foundBot != null) && jsonString.isEmpty()) {
                mBotsList.remove(foundBot);

                if (mBotsList.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mImageViewPlaceHolder.setVisibility(View.GONE);
                }
                else {
                    mRecyclerView.setVisibility(View.GONE);
                    mImageViewPlaceHolder.setVisibility(View.VISIBLE);
                }

                mBotsRecyclerAdapter.notifyDataSetChanged();

                return;
            }
            else {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (foundBot != null) {
                    foundBot.setMac(key);

                    foundBot.setKey(
                        jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY)
                    );

                    foundBot.setName(
                        jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME)
                    );

                    foundBot.setIsEnabled(
                        jsonObject.getBoolean(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED)
                    );
                }
                else {
                    mBotsList.add(
                        new Bot(
                            jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY),
                            jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC),
                            jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME),
                            jsonObject.getBoolean(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED)
                        )
                    );
                }
            }
        }
        catch (JSONException exception) {}

        if (mBotsList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mImageViewPlaceHolder.setVisibility(View.GONE);
        }
        else {
            mRecyclerView.setVisibility(View.GONE);
            mImageViewPlaceHolder.setVisibility(View.VISIBLE);
        }

        mBotsRecyclerAdapter.notifyDataSetChanged();
    }

    public BotsFragment() { super(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    )
    {
        return inflater.inflate(R.layout.fragment_bots, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_bots, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBotsList = new ArrayList<>();
        mIsDialogOnScreenBotRemove = false;
        mIsDialogOnScreenBotSettings = false;
        mIsDialogOnScreenBotSettingsMac = "";
        mIsDialogOnScreenBotRemoveAll = false;
        mIsDialogOnScreenBotSettingsTempData = "";
        mRecyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mImageViewPlaceHolder = view.findViewById(R.id.image_view_placeholder);

        mPrefsBots =
            getContext().getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFERENCES_TAG_BOTS,
                getContext().MODE_PRIVATE
            );

        Map<String, ?> prefsBotsAll = mPrefsBots.getAll();
        mPrefsBots.registerOnSharedPreferenceChangeListener(this);

        mBotsRecyclerAdapter =
            new BotsRecyclerAdapter(getContext(), this, getActivity(), mBotsList);

        mBotsList.clear();

        for (String key : prefsBotsAll.keySet()) {
            String value = prefsBotsAll.get(key).toString();

            try {
                JSONObject jsonObject = new JSONObject(value);

                mBotsList.add(
                    new Bot(
                        jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY),
                        jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC),
                        jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME),
                        jsonObject.getBoolean(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED)
                    )
                );
            }
            catch (JSONException exception) {}
        }

        recyclerView.addItemDecoration(
            new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL
            )
        );

        recyclerView.setAdapter(mBotsRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mBotsList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mImageViewPlaceHolder.setVisibility(View.GONE);
        }
        else {
            mRecyclerView.setVisibility(View.GONE);
            mImageViewPlaceHolder.setVisibility(View.VISIBLE);
        }

        mBotsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_remove_all_bots: {
                if (mPrefsBots.getAll().size() < 1) {
                    return true;
                }

                AlertDialog dialog = getDialogBotRemoveAll(getContext());

                dialog.show();
                mIsDialogOnScreenBotRemoveAll = true;

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE,
            mIsDialogOnScreenBotRemove
        );

        outState.putString(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_MAC,
            mIsDialogOnScreenBotRemoveMac
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS,
            mIsDialogOnScreenBotSettings
        );

        outState.putString(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_MAC,
            mIsDialogOnScreenBotSettingsMac
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_ALL,
            mIsDialogOnScreenBotRemoveAll
        );

        if (mCurrentSettingsDialog != null) {
            try {
                JSONObject jsonObject = new JSONObject();

                TextView textViewMAC =
                    mCurrentSettingsDialog.findViewById(R.id.text_view_mac);

                EditText editTextKey =
                    mCurrentSettingsDialog.findViewById(R.id.edit_text_key);

                EditText editTextName =
                    mCurrentSettingsDialog.findViewById(R.id.edit_text_name);

                CheckBox checkBoxIsEnabled =
                    mCurrentSettingsDialog.findViewById(R.id.check_box_is_enabled);

                jsonObject.put(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY, editTextKey.getText());
                jsonObject.put(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC, textViewMAC.getText());
                jsonObject.put(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME, editTextName.getText());

                jsonObject.put(
                    Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED, checkBoxIsEnabled.isChecked()
                );

                outState.putString(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_TEMP_DATA,
                    jsonObject.toString()
                );
            }
            catch (JSONException exception) {}
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AlertDialog dialog;

        if (savedInstanceState != null) {
            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_ALL
                )
            )
            {
                mIsDialogOnScreenBotRemoveAll =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_ALL
                    );

                if (mIsDialogOnScreenBotRemoveAll) {
                    dialog = getDialogBotRemoveAll(getContext());

                    dialog.show();
                }
            }

            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE
                ) &&
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_MAC
                )
            )
            {
                mIsDialogOnScreenBotRemove =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE
                    );

                mIsDialogOnScreenBotRemoveMac =
                    savedInstanceState.getString(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_MAC
                    );

                if (mIsDialogOnScreenBotRemove && !mIsDialogOnScreenBotRemoveMac.isEmpty()) {
                    dialog = getDialogBotRemove(getContext(), mIsDialogOnScreenBotRemoveMac);

                    dialog.show();
                }
            }

            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS
                ) &&
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_MAC
                )
            )
            {
                mIsDialogOnScreenBotSettings =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS
                    );

                mIsDialogOnScreenBotSettingsMac =
                    savedInstanceState.getString(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_MAC
                    );

                if (mIsDialogOnScreenBotSettings && !mIsDialogOnScreenBotSettingsMac.isEmpty()) {
                    if (
                        savedInstanceState.containsKey(
                            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_TEMP_DATA
                        )
                    )
                    {
                        dialog =
                            getDialogBotSettings(
                                getContext(),
                                getActivity(),
                                mIsDialogOnScreenBotSettingsMac,
                                savedInstanceState.getString(
                                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_TEMP_DATA
                                )
                            );
                    }
                    else {
                        dialog =
                            getDialogBotSettings(
                                getContext(),
                                getActivity(),
                                mIsDialogOnScreenBotSettingsMac,
                                null
                            );
                    }

                    dialog.show();
                }
            }
        }
    }
}
