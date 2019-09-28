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

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class BotsFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private ArrayList<Bot> mBotsList;
    private RecyclerView mRecyclerView;
    private SharedPreferences mPrefsBots;
    private ImageView mImageViewPlaceHolder;
    private BotsRecyclerAdapter mBotsRecyclerAdapter;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e("BotsFragment", String.format("onSharedPreferenceChanged() :: KEY = %s", key));

        Bot foundBot = null;

        for (Bot bot : mBotsList) {
            if (bot.getMAC().equals(key)) {
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

            JSONObject jsonObject = new JSONObject(jsonString);

            if (foundBot != null) {
                Log.e("BotsFragment", "onSharedPreferenceChanged() :: UPDATE EXISTING BOT");
                foundBot.setMAC(key);
                foundBot.setKey(
                    jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_KEY)
                );

                foundBot.setName(
                    jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_NAME)
                );

                foundBot.setIsEnabled(
                    jsonObject.getBoolean(Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED)
                );
            }
            else {
                mBotsList.add(
                    new Bot(
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_KEY),
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_MAC),
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_NAME),
                        jsonObject.getBoolean(Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED)
                    )
                );
                Log.e("BotsFragment", "onSharedPreferenceChanged() :: ADDED NEW BOT");
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

    public BotsFragment() { super(); Log.e("BotFragment","BotsFragment()");}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("BotFragment","onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        Log.e("BotFragment","onCreate()");
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
        mRecyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mImageViewPlaceHolder = view.findViewById(R.id.image_view_placeholder);
        mBotsRecyclerAdapter = new BotsRecyclerAdapter(getActivity(), getContext(), mBotsList);
        mPrefsBots =
            getContext().getApplicationContext().getSharedPreferences(
                Constants.PREFS_TAG_BOTS,
                getContext().MODE_PRIVATE
            );

        Map<String, ?> prefsBotsAll = mPrefsBots.getAll();
        mPrefsBots.registerOnSharedPreferenceChangeListener(this);

        mBotsList.clear();

        for (String key : prefsBotsAll.keySet()) {
            String value = prefsBotsAll.get(key).toString();

            try {
                JSONObject jsonObject = new JSONObject(value);

                mBotsList.add(
                    new Bot(
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_KEY),
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_MAC),
                        jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_NAME),
                        jsonObject.getBoolean(Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED)
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_attention)
                    .setIcon(R.drawable.ic_attention_black_24dp)
                    .setMessage(R.string.dialog_message_bot_remove_all)
                    .setPositiveButton(
                        R.string.dialog_positive_button,
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
                        R.string.dialog_negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        }
                    );

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
