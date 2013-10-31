package com.codepath.apps.twitterapp.fragments;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.os.Bundle;

import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.TwitterClientApp;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateTimeline(new HashMap<TwitterClient.TimelineParams, String>(), true);
	}
	
	public static UserTimelineFragment newInstance (String screenName) {
		UserTimelineFragment fragment = new UserTimelineFragment();
		
		Bundle args = new Bundle();
		args.putString("screenName", screenName);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void updateTimeline(Map<TimelineParams, String> params,
			boolean append) {
		
		if (getArguments() != null && getArguments().containsKey("screenName")) {
			params.put(TimelineParams.SCREENNAME, getArguments().getString("screenName"));
		}
		
		params.put(TimelineParams.COUNT, "25");
		TwitterClientApp.getRestClient().getUserTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray usersJsonTweets) {
				// update tweets
				getTweets().addAll(Tweet.fromJson(usersJsonTweets));
				getTweetsAdapter().notifyDataSetChanged();
			}
		},
		params);
	}

}
