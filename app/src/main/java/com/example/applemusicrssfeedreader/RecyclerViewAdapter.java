/*
 * Jack Murphy
 * jackmurphy569@gmail.com
 * 10-28-2019
 * */

package com.example.applemusicrssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//Most of this stuff came from stackoverflow and was just modified to fit the rest of the code
//Would probably be for the best to redo some of this to help eliminate spaghetti

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String tag = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mLinks = new ArrayList<>();
    private Context mContext;


    public RecyclerViewAdapter(ArrayList<String> ImageNames, ArrayList<String> Images, ArrayList<String> Links, Context context) {

        Log.d(tag, "constructor: called");

        mImageNames = ImageNames;
        mImages = Images;
        mLinks = Links;
        mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(tag, "onCreateViewHolder: called");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(tag, "onBindViewHolder: called.");

        Glide.with(mContext).asBitmap().load(mImages.get(position)).into(holder.image);

        holder.displayText.setText(mImageNames.get(position));

        //handles apple music redirect for more info when clicked
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(tag,"onClick: clicked on: " + mImageNames.get(position));

                //open internet browser to url specified by album
                Uri uri = Uri.parse(mLinks.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView displayText;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            displayText = itemView.findViewById(R.id.albumText);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
