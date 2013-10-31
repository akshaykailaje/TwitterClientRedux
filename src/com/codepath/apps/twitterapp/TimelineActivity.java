package com.codepath.apps.twitterapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterapp.fragments.MentionsFragment;
import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TimelineActivity extends FragmentActivity implements TabListener {

	private User currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		updateCurrentUser();
		setupNavigationTabs();
	}
	
	private void setupNavigationTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		
		Tab tabHome = actionBar.newTab()
						.setText("Home")
						.setTag("HomeTimelineFragment")
						.setIcon(R.drawable.ic_home)
						.setTabListener(this);
		
		Tab tabMentions = actionBar.newTab()
							.setText("Mentions")
							.setTag("MentionsTimelineFragment")
							.setIcon(R.drawable.ic_mentions)
							.setTabListener(this);
		
		actionBar.addTab(tabHome);
		actionBar.addTab(tabMentions);
		
		actionBar.selectTab(tabHome);
	}
	
	public void onComposeAction(MenuItem mi) {
		
		// new intent to compose activity
		Intent i = new Intent(this, ComposeActivity.class);
		i.putExtra("currentUser", this.currentUser);
		startActivityForResult(i, 0);
	}
	
	public void onProfileAction(MenuItem mi) {
		if (this.currentUser == null) {
			Toast.makeText(getApplicationContext(), "View profile not available currently. Try again after sometime", Toast.LENGTH_LONG).show();
			return;
		}
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("screenName", this.currentUser.getScreenName());
		startActivity(i);
	}
	
	private void updateCurrentUser() {
		
		TwitterClientApp.getRestClient().verifyCredentials(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonCredentials) {
				currentUser = new User(jsonCredentials);
				setTitle(currentUser.getName().split(" ")[0] + "'s Timeline");
			}
			
			@Override
			public void onFailure(Throwable t, JSONArray response) {
				Log.e("DEBUG", "Error getting current user data. Response="+response.toString(), t);
			}
		});
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// compose tweet request code
    	if (requestCode == 0) {
    		if (data == null) {
    			return;
    		}
    		if (resultCode == RESULT_OK) {
    			Tweet composedTweet = (Tweet) data.getSerializableExtra("composedTweet");
    			Log.d("DEBUG", "composed tweet data = " + composedTweet);
    			
    			FragmentManager manager = getSupportFragmentManager();
    			android.support.v4.app.FragmentTransaction fst = manager.beginTransaction();
    			Fragment homeTimelineFragment = manager.findFragmentById(R.id.flContainer);
    			if (homeTimelineFragment != null) {
    				fst.remove(homeTimelineFragment);
    			}
    			
    			fst.replace(R.id.flContainer, HomeTimelineFragment.newInstance(composedTweet));
    			fst.addToBackStack(null);
    			fst.commitAllowingStateLoss();
    		}
    		
    		if (resultCode == RESULT_CANCELED) {
    			Log.d("DEBUG", "Result cancelled");
    			String errorMessage = data.getStringExtra("errorMessage");
    			if (errorMessage != null) {
    				Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    			}
    		}
    	}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		FragmentManager manager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fst = manager.beginTransaction();
		
		if (tab.getTag().equals("HomeTimelineFragment")) {
			fst.replace(R.id.flContainer, new HomeTimelineFragment());
		} else {
			fst.replace(R.id.flContainer, new MentionsFragment());
		}
		
		fst.commit();
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

}
