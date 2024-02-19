package com.asier.nemergenttest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asier.nemergenttest.R;
import com.asier.nemergenttest.models.Picture;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends ArrayAdapter<Picture> {
    private Context mContext;
    private List<Picture> pictureList;

    public PictureAdapter(@NonNull Context context, ArrayList<Picture> list) {
        super(context, 0 , list);
        mContext = context;
        pictureList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.pics_item, parent,false);

        Picture pic = pictureList.get(position);

        ImageView image = listItem.findViewById(R.id.picPreview);
        image.setImageDrawable(pic.getDrawable());

        TextView text = listItem.findViewById(R.id.picText);
        text.setText(pic.getLocation());

        return listItem;
    }
}
