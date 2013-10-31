package com.codepath.apps.twitterapp.fragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.codepath.apps.twitterapp.DBManager;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.TwitterClientApp;
import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HomeTimelineFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		updateTimeline(new HashMap<TwitterClient.TimelineParams, String>(), true);
	}
	
	public static HomeTimelineFragment newInstance (Tweet composedTweet) {
		HomeTimelineFragment fragment = new HomeTimelineFragment();
		
		Bundle args = new Bundle();
		args.putSerializable("composedTweet", composedTweet);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/**
	 * Update the timeline with new tweets
	 * @param currentTweet tweet that was recently composed. It may not make it to the timeline yet. 
	 * @param params parameters for the timeline call
	 * @param append boolean flag indicating whether to append or prepend the retrieved tweets. true to append
	 */
	@Override
	public void updateTimeline(final Map<TwitterClient.TimelineParams, String> params, final boolean append) {
		
		params.put(TwitterClient.TimelineParams.COUNT, "25");
		TwitterClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				List<Tweet> latestTweets = Tweet.fromJson(jsonTweets);
				boolean saveTweets = false;
				
				if (getArguments() != null && getArguments().containsKey("composedTweet")) {
					Tweet composedTweet = (Tweet) getArguments().getSerializable("composedTweet");
					if (composedTweet != null && !latestTweets.contains(composedTweet)) {
						latestTweets.add(composedTweet);
					}
				}
				if (append == true) {
					if (getTweets().size() == 0) {
						saveTweets = true;
					}
					getTweets().addAll(latestTweets);
					
				} else {
					getTweets().addAll(0, latestTweets);
					saveTweets = true;
				}
				if (saveTweets && latestTweets != null && latestTweets.size() > 0) {
					Log.d("DEBUG", "Saving "+latestTweets.size()+" tweets to the db");
					DBManager.saveTweets(latestTweets);
				}
				getTweetsAdapter().notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(Throwable t, JSONArray response) {
				Log.d("DEBUG", "Error retrieving tweets. Response="+response.toString(), t);
				Toast.makeText(getActivity().getApplicationContext(), "Error retrieving tweets", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onFinish() {
				if (getTweets().size() == 0) {
					getTweets().addAll(DBManager.getStoredTweets());
					Log.d("DEBUG", "Retrieved "+getTweets().size()+" tweets from DB storage");
					getTweetsAdapter().notifyDataSetChanged();
				}
				super.onFinish();
			}
		}, params);
	}
}
