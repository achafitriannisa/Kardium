package com.example.asuspc.wecg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.custom_list, data);
        this.dataSet = data;
        this.mContext = context;

    }

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView subtitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.title.setText(dataModel.getFileName());
        viewHolder.subtitle.setText(dataModel.getLastModified());
        // Return the completed view to render on screen
        return convertView;
    }



}
