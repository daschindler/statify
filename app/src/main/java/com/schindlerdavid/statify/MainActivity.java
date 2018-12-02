package com.schindlerdavid.statify;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.schindlerdavid.statify.adapter.TrackAdapter;
import com.schindlerdavid.statify.adapter.TrackRecycleAdapter;
import com.schindlerdavid.statify.entity.Track;
import com.schindlerdavid.statify.interfaces.VolleyCallBack;
import com.schindlerdavid.statify.singleton.StatifyRepo;
import com.schindlerdavid.statify.singleton.StatifyWebRepo;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llArtists;
    private ListView lvTracks;
    private TrackRecycleAdapter trackAdapter;
    private RecyclerView rvTracks;

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    StatifyWebRepo.setAccessToken(response.getAccessToken());
                    requestTracks();
                    break;

                case ERROR:
                    Toast.makeText(MainActivity.this, R.string.error_msg_no_connection, Toast.LENGTH_LONG).show();
                    break;

                default:
                    Toast.makeText(MainActivity.this, R.string.error_msg_no_connection, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        beginAuthentication();
    }

    private void beginAuthentication() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(StatifyRepo.getClientId(), AuthenticationResponse.Type.TOKEN, StatifyRepo.getRedirectUri());

        builder.setScopes(new String[]{"streaming","user-read-private","user-modify-playback-state",
                "user-read-currently-playing", "user-read-playback-state", "user-library-modify",
                "user-library-read", "app-remote-control", "user-read-email", "user-read-birthdate",
                "user-follow-read", "user-follow-modify", "playlist-read-private", "playlist-read-collaborative",
                "playlist-modify-public", "playlist-modify-private", "user-read-recently-played", "user-top-read"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
    }


    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_tracks:
                    return true;
                case R.id.navigation_artists:
                    return true;
                case R.id.navigation_playlists:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.custom_titlebar);
        centerTitle();
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#0e0e0e"));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //llArtists = findViewById(R.id.ll_artists);

    }

    private void requestTracks() {
        //lvTracks = findViewById(R.id.lv_track_list);
        rvTracks = findViewById(R.id.rv_track_list);
        StatifyWebRepo.getHttpQuery("https://api.spotify.com/v1/me/top/tracks?limit=20", MainActivity.this, new VolleyCallBack() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                JSONArray favoriteArtistJSON = null;
                try {
                    ArrayList<Track> trackList = new ArrayList<Track>();
                    favoriteArtistJSON = (JSONArray) jsonObject.get("items");
                    trackAdapter = new TrackRecycleAdapter(trackList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvTracks.setLayoutManager(mLayoutManager);
                    rvTracks.setItemAnimator(new DefaultItemAnimator());
                    rvTracks.setAdapter(trackAdapter);

                    for (int i = 0; i < favoriteArtistJSON.length(); i++){
                        JSONObject currentJSON = favoriteArtistJSON.getJSONObject(i);
                        JSONObject album = currentJSON.getJSONObject("album");
                        JSONArray images = album.getJSONArray("images");

                        JSONObject image = images.getJSONObject(1);

                        String url = image.get("url").toString();
                        //Bitmap cover = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        //Bitmap cover = imageLoader.loadImageSync(image.get("url").toString());
                        //TextView tvArtist = new TextView(MainActivity.this);
                        //tvArtist.setTextColor(getResources().getColor(R.color.white));
                        //vArtist.setText(currentJSON.get("name").toString());

                        //llArtists.addView(tvArtist);

                        Track track = new Track(currentJSON.get("name").toString(), url);
                        trackList.add(track);

                    }

                    trackAdapter.notifyDataSetChanged();

                    //trackAdapter.notifyDataSetChanged();
                    //lvTracks.setAdapter(trackAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

}
