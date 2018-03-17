package com.homeless.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.homeless.R;

import java.util.ArrayList;

/**
 * Created by vladi on 3/16/2018.
 */

public class ReviewListAdapter extends ArrayAdapter <Review> {
    private static final String TAG = "ReviewListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView address;
        TextView details;
        TextView priceAndScore;
    }
    public ReviewListAdapter(@NonNull Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String address = getItem(position).getAddress();
        String addDetails = "Rooms: " + getItem(position).getNumOfRooms() + " Floor: " + getItem(position).getFloor()
                + " Size: " + getItem(position).getSize();
        String priceAndScore = "Price: " + getItem(position).getPrice() + " Score: " + getItem(position).getScore();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.address = (TextView) convertView.findViewById(R.id.addressSum);
            holder.details = (TextView) convertView.findViewById(R.id.details);
            holder.priceAndScore = (TextView) convertView.findViewById(R.id.priceAndScore);


            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        /*Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);*/
        lastPosition = position;

        holder.address.setText(address);
        holder.details.setText(addDetails);
        holder.priceAndScore.setText(priceAndScore);
        return convertView;
    }

}
