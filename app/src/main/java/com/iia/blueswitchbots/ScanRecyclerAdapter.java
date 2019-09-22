package com.iia.blueswitchbots;

import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import android.bluetooth.BluetoothDevice;
import androidx.recyclerview.widget.RecyclerView;

public class ScanRecyclerAdapter
        extends RecyclerView.Adapter<ScanRecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<BluetoothDevice> mData;

    ScanRecyclerAdapter(Context context, ArrayList<BluetoothDevice> data) {
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        ImageButton addBot;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_view_name);
            addBot = itemView.findViewById(R.id.image_button_add);
            address = itemView.findViewById(R.id.text_view_address);

            addBot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("SRA :: ADD BOT CLICKED", address.getText().toString());
                    }
                }
            );
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ScanRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.scan_list_item, parent, false);

        return new ScanRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScanRecyclerAdapter.ViewHolder holder, int position) {
        BluetoothDevice device = mData.get(position);

        String[] _mac_octaves = device.getAddress().split(":");
        String[] mac_octaves = { _mac_octaves[3], _mac_octaves[4], _mac_octaves[5]};
        String mac_octaves_joined = TextUtils.join("", mac_octaves);

        holder.name.setText(TextUtils.concat("Bot-", mac_octaves_joined));
        holder.address.setText(device.getAddress());
    }
}
