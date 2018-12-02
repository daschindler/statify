package com.schindlerdavid.statify.singleton;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.schindlerdavid.statify.MainActivity;
import com.schindlerdavid.statify.R;
import com.schindlerdavid.statify.entity.User;
import com.schindlerdavid.statify.interfaces.VolleyCallBack;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class StatifyWebRepo {
    private static String accessToken = null;
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "36b9546c22b44e31aea665bf90fa0f5d";
    private static final String REDIRECT_URI = "com.schindlerdavid.statify://callback";


    protected StatifyWebRepo() {
        // Exists only to defeat instantiation.
    }

    public static void setAccessToken(String accessToken) {
        StatifyWebRepo.accessToken = accessToken;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void getHttpQuery(String url, MainActivity thisMainactivity, final VolleyCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(thisMainactivity);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();
                            JSONObject jsonResponse = new JSONObject(response);
                            callBack.onSuccessResponse(jsonResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + accessToken);

                return params;
            }
        };
        queue.add(postRequest);
    }
}