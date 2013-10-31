package com.codepath.apps.twitterapp.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterapp.EndlessScrollListener;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TweetsAdapter;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.models.Tweet;

import eu.erikw.PullToRefreshListView;

public abstract class TweetsListFragment extends Fragment {

	private List<Tweet> tweets;
	private TweetsAdapter tweetsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
		return inf.inflate(R.layout.fragments_tweets_list, parent, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		tweetsAdapter = new TweetsAdapter(getActivity(), tweets);
		final PullToRefreshListView lvTweets = (PullToRefreshListView) getActivity().findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdapter);
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("DEBUG", "tweets size="+tweets.size()+", page="+page+", totalItemsCount="+totalItemsCount);
				// get the last tweet to get the max id
				if (tweets.size() > 0) {
					Tweet lastTweet = tweets.get(tweets.size() - 1);
					// subtract one to not include the last tweet
					Map<TwitterClient.TimelineParams, String> params = new HashMap<TwitterClient.TimelineParams, String>();
					params.put(TwitterClient.TimelineParams.MAXID, String.valueOf(lastTweet.getTweetId() - 1));
					updateTimeline(params, true);
				}
			}
		});
		
		lvTweets.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

			@Override
			public void onRefresh() {

				Map<TwitterClient.TimelineParams, String> params = new HashMap<TwitterClient.TimelineParams, String>();
				// get the latest tweet to get the since id
				if (tweets.size() > 0) {
					Tweet latestTweet = tweets.get(0);
					params.put(TwitterClient.TimelineParams.SINCEID, String.valueOf(latestTweet.getTweetId()));
				}

				updateTimeline(params, false);
				lvTweets.onRefreshComplete();
			}
		});
	}
	
	/**
	 * Update the timeline with new tweets 
	 * @param params parameters for the timeline call
	 * @param append boolean flag indicating whether to append or prepend the retrieved tweets. true to append
	 */
	public abstract void updateTimeline(final Map<TwitterClient.TimelineParams, String> params, final boolean append);
	
	public List<Tweet> getTweets() {
		return tweets;
	}
	
	public TweetsAdapter getTweetsAdapter() {
		return tweetsAdapter;
	}
	
}
