package com.example.myjourneyfriend;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class ListAdapter extends ArrayAdapter<JSONObject> {
    int resource;

    ArrayList<JSONObject> objects;

    Context context;

    public ListAdapter(Context context, int resource, int textViewResourceId, ArrayList<JSONObject> objects) {

        super(context, resource, textViewResourceId, objects);
        this.resource = resource;
        this.context = context;
        this.objects = objects;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(resource, parent, false);

        TextView txtTitle = itemView.findViewById(R.id.txttitle);


        try {

            txtTitle.setText(objects.get(position).getString("alarm_name"));


        } catch (JSONException e) {

            e.printStackTrace();

        }

        return itemView;

    }
}

