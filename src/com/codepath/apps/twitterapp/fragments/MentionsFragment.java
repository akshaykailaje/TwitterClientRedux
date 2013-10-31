package com.codepath.apps.twitterapp.fragments;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.TwitterClientApp;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateTimeline(new HashMap<TwitterClient.TimelineParams, String>(), true);
	}
	
	@Override
	public void updateTimeline(Map<TimelineParams, String> params, boolean append) {
		
		params.put(TimelineParams.COUNT, "20");
		TwitterClientApp.getRestClient().getMentions(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				// update tweets
				getTweets().addAll(Tweet.fromJson(jsonTweets));
				getTweetsAdapter().notifyDataSetChanged();
			}
			
			
			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				Log.e("ERROR", "error:"+t.getMessage()+", obj="+obj.toString());
				Toast.makeText(getActivity().getApplicationContext(), "Could not retrieve the mentions timeline", Toast.LENGTH_LONG).show();
			}
			
		}, 
		params);

	}

}
