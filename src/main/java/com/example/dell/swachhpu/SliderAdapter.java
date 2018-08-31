package com.example.dell.swachhpu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context) {
        this.context = context;
    }

    //arrays
    public int[] slide_images =  {
            R.drawable.capture,
            R.drawable.locate,
            R.drawable.report
    };

    public String[] slide_headings = {
            "CAPTURE!",
            "LOCATE!",
            "REPORT!"
    };

    public String[] slide_descs = {
            "Capture images of Garbage, which you think went un-noticed by the authorities.",
            "Help us locate you better, add specifics of your location, viz., Floor of the building etc...",
            "Tap Report, and done! We'll make sure your complain reaches upto the concerned authorities."
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (RelativeLayout) o ;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
