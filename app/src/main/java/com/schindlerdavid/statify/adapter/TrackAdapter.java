package com.schindlerdavid.statify.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.schindlerdavid.statify.R;
import com.schindlerdavid.statify.entity.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends ArrayAdapter<Track> {

    private Context mContext;
    private List<Track> trackList = new ArrayList<Track>();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public TrackAdapter(@NonNull Context context, @LayoutRes ArrayList<Track> list) {
        super(context, 0 , list);
        mContext = context;
        trackList = list;

        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_tracks,parent,false);

        Track currentTrack = trackList.get(position);

        TextView trackName = listItem.findViewById(R.id.tv_list_item_track_name);
        trackName.setText(currentTrack.getName());

        NetworkImageView cover = listItem.findViewById(R.id.iv_list_item_cover);
        cover.setImageUrl(currentTrack.getCover_url(), mImageLoader);

        return listItem;
    }
}
