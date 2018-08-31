package com.example.dell.swachhpu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 6/22/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    String str;
    String name;
    //we are storing all the products in a list
    private List<Retrievedata> userlist;
    //private List<UserImage> userimage;

    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, List<Retrievedata> userlist) {
        this.mCtx = mCtx;
        this.userlist = userlist;
        //this.userimage = userimage;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
       /* LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_view, parent, false);
        return new ProductViewHolder(view);
        */
        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_view, parent, false);

        ProductViewHolder viewHolder = new ProductViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        //getting the product of the specified position
        final Retrievedata product = userlist.get(position);
        String status = product.getStatus();

        if (status.equals("0")) {

        } else if (status.equals("1")) {
            holder.compl_status.setText(" RESOLVING ");
            holder.compl_status.setTextColor(Color.parseColor("#00AAE4"));

        } else if (status.equals("2")) {
            holder.compl_status.setText(" RESOLVED ");
            holder.compl_status.setTextColor(Color.parseColor("#039d00"));

        } else if (status.equals("-1")) {
            holder.compl_status.setText(" REJECTED ");
            holder.compl_status.setTextColor(Color.RED);

        }

        Double lat = product.getLatitude();
        Double lon = product.getLongitude();
        Geocoder geocoder = new Geocoder(mCtx);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
            str = addressList.get(0).getSubLocality();
            // String str1 = addressList.get(0).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String date = product.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            String timeAgo = TimeAgo.getTimeAgo(timeInMilliseconds);
            holder.textView_time.setText(timeAgo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //binding the data with the viewholder views
        name = product.getName();
        holder.textViewname.setText(name);
        holder.textViewAddress.setText(product.getDescription());
        holder.textView_add.setText(str);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, RecyclerviewclickActivity.class);
                intent.putExtra("img_url",product.getImage());
                intent.putExtra("desc", product.getDescription());
                intent.putExtra("status",product.getStatus());
                intent.putExtra("address",str);
                intent.putExtra("floor",product.getFloor());
                intent.putExtra("dept",product.getDepartment());
                intent.putExtra("push_id",product.getpId());
                mCtx.startActivity(intent);
            }
        });
        // holder.imageView.setImageBitmap(decodedImage);

    }


    @Override
    public int getItemCount() {
        return userlist.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewAddress, compl_status, textView_add, textView_time ;
        LinearLayout parentLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewname = itemView.findViewById(R.id.username);
            textViewAddress = itemView.findViewById(R.id.text2);
            compl_status = itemView.findViewById(R.id.status);
            textView_time = itemView.findViewById(R.id.time);
            textView_add = itemView.findViewById(R.id.location_add);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
