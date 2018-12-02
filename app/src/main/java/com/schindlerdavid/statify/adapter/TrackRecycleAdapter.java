package com.schindlerdavid.statify.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.schindlerdavid.statify.R;
import com.schindlerdavid.statify.entity.Track;

import java.util.List;

public class TrackRecycleAdapter extends RecyclerView.Adapter<TrackRecycleAdapter.MyViewHolder> {

    private List<Track> trackList;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public NetworkImageView cover;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_list_item_track_name);
            cover = view.findViewById(R.id.iv_list_item_cover);
        }
    }


    public TrackRecycleAdapter(List<Track> trackList) {
        this.trackList = trackList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tracks, parent, false);

        mRequestQueue = Volley.newRequestQueue(parent.getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.name.setText(track.getName());
        holder.cover.setImageUrl(track.getCover_url(), mImageLoader);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }
}