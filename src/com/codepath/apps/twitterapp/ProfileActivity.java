package com.codepath.apps.twitterapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.fragments.UserTimelineFragment;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		String screenName = getIntent().getStringExtra("screenName");
		loadProfileInfo(screenName);
		loadProfileTimeline(screenName);
		
	}
	
	private void loadProfileInfo(String screenName) {
		
		Map<TwitterClient.TimelineParams, String> callParams = new HashMap<TwitterClient.TimelineParams, String>();
		callParams.put(TimelineParams.SCREENNAME, screenName);
		TwitterClientApp.getRestClient().getUserInfo(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject userJsonObject) {
				User user = new User(userJsonObject);
				getActionBar().setTitle("@" + user.getScreenName());
				populateProfileHeader(user);
			}
			
			@Override
			public void onFailure(Throwable t, JSONObject responseObject) {

			}
		},
		callParams);
	}
	
	private void loadProfileTimeline(String screenName) {
		
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction fst = manager.beginTransaction();
		fst.add(R.id.fragmentContainer, UserTimelineFragment.newInstance(screenName));
		fst.commit();
	}
	
	private void populateProfileHeader(User user) {
		TextView tvName = (TextView) findViewById(R.id.tvName);
		TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
		TextView tvFollowersValue = (TextView) findViewById(R.id.tvFollowersValue);
		TextView tvFollowingValue = (TextView) findViewById(R.id.tvFollowingValue);
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		
		tvName.setText(user.getName());
		tvTagline.setText(user.getTagline());
		tvFollowersValue.setText(String.valueOf(user.getFollowersCount()));
		tvFollowingValue.setText(String.valueOf(user.getFriendsCount()));
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}
