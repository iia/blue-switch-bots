package com.iia.blueswitchbots;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BotsFragment extends Fragment {
    public String name= "Bots";

    public BotsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bots, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        inflater.inflate(R.menu.menu_fragment_bots, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Bot> bl = new ArrayList<>();
        bl.add(new Bot("Bot-1","This is Bot-1"));
        bl.add(new Bot("Bot-2","This is Bot-2"));
        bl.add(new Bot("Bot-3","This is Bot-3"));
        bl.add(new Bot("Bot-4","This is Bot-4"));

        BotsRecyclerAdapter ba = new BotsRecyclerAdapter(getContext(), bl);

        RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.addItemDecoration(
                new DividerItemDecoration(
                        getContext(),
                        DividerItemDecoration.VERTICAL
                )
        );
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(ba);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
