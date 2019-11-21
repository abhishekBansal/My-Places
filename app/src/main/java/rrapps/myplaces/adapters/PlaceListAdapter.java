package rrapps.myplaces.adapters;

/**
 * 
 * Created by abhishek on 30/06/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import rrapps.myplaces.R;
import rrapps.myplaces.model.MPLocation;

public class PlaceListAdapter extends ArrayAdapter<MPLocation> {

    private View.OnClickListener mOnClickListener;

    public PlaceListAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, 0);
        mOnClickListener = onClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MPLocationViewHolder viewHolder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_place, parent, false);

            viewHolder = new MPLocationViewHolder();
            viewHolder.locationNameTv = (TextView)convertView.findViewById(R.id.tv_location_name);
            viewHolder.locationInitialTv = (TextView)convertView.findViewById(R.id.tv_location_initial);
            viewHolder.navigateToBtn = (ImageButton)convertView.findViewById(R.id.button_navigate);
            viewHolder.shareBtn = (ImageButton)convertView.findViewById(R.id.button_share);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MPLocationViewHolder)convertView.getTag();
        }

        MPLocation location = getItem(position);
        viewHolder.locationNameTv.setText(location.getName());
        viewHolder.locationInitialTv.setText(location.getName().substring(0, 1).toUpperCase());
        viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(getItem(position));
                mOnClickListener.onClick(v);
            }
        });

        viewHolder.navigateToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(getItem(position));
                mOnClickListener.onClick(v);
            }
        });


        return convertView;
    }

    private static class MPLocationViewHolder {
        TextView locationNameTv;
        TextView locationInitialTv;
        ImageButton navigateToBtn;
        ImageButton shareBtn;
    }
}

