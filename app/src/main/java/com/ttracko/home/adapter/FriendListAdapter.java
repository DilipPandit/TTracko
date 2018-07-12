package com.ttracko.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttracko.R;
import com.ttracko.home.models.Users;

import java.util.ArrayList;

/**
 * Created by root on 12/7/18.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListHolder> {

    ArrayList<Users> usersArrayList;
    Context context;

    public FriendListAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @Override
    public FriendListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_friendlist, parent, false);
        return new FriendListHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendListHolder holder, int position) {
        holder.tvName.setText(usersArrayList.get(position).getName());
        holder.tvMobileNumber.setText(usersArrayList.get(position).getMobile());
        if(usersArrayList.get(position).getGroupName()!=null)
        {
            if(!usersArrayList.get(position).getGroupName().isEmpty())
            {
                holder.ivProfile_image.setImageResource((R.drawable.ic_group_add));
            }else {
                holder.ivProfile_image.setImageResource((R.drawable.ic_user));
            }
        }else {
            holder.ivProfile_image.setImageResource((R.drawable.ic_user));
        }
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class FriendListHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvMobileNumber;
        ImageView ivProfile_image;

        public FriendListHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvMobileNumber = (TextView) itemView.findViewById(R.id.tvMobileNumber);
            ivProfile_image = (ImageView) itemView.findViewById(R.id.ivProfile_image);
        }
    }

}
