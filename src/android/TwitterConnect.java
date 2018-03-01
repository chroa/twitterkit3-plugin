package com.manifestwebdesign.twitterconnect;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.*;
import com.twitter.sdk.android.core.services.*;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import android.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

import com.google.gson.Gson;

public class TwitterConnect extends CordovaPlugin {

	private static final String LOG_TAG = "Twitter Connect";
	private String action;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		TwitterConfig config = new TwitterConfig.Builder(cordova.getActivity().getApplicationContext())
    		.twitterAuthConfig(new TwitterAuthConfig(getTwitterKey(), getTwitterSecret()))
    		.build();
		Twitter.initialize(config);
	}

	private String getTwitterKey() {
		return preferences.getString("TwitterConsumerKey", "");
	}

	private String getTwitterSecret() {
		return preferences.getString("TwitterConsumerSecret", "");
	}

	public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		this.action = action;
		final Activity activity = this.cordova.getActivity();
		final Context context = activity.getApplicationContext();
		cordova.setActivityResultCallback(this);

		if (action.equals("login")) {
			login(activity, callbackContext);
			return true;
		}
		if (action.equals("logout")) {
			logout(callbackContext);
			return true;
		}
		if (action.equals("showUser")) {
			boolean includeEntities = false;
			String includeEntitiesStr = "";

			try {
				includeEntitiesStr = args.getJSONObject(0).getString("include_entities");
				includeEntities = Boolean.valueOf(includeEntitiesStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			showUser(includeEntities, callbackContext);
			return true;
		}
		if (action.equals("verifyCredentials")) {
			boolean includeEntities = false;
			boolean skipStatus = true;
			boolean includeEmail = true;
			String includeEntitiesStr = "";
			String skipStatusStr = "";
			String includeEmailStr = "";

			try {
				includeEntitiesStr = args.getJSONObject(0).getString("include_entities");
				includeEntities = Boolean.valueOf(includeEntitiesStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			try {
				skipStatusStr = args.getJSONObject(0).getString("skip_status");
				skipStatus = Boolean.valueOf(skipStatusStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			try {
				includeEmailStr = args.getJSONObject(0).getString("include_email");
				includeEmail = Boolean.valueOf(includeEmailStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			verifyCredentials(includeEntities, skipStatus, includeEmail, callbackContext);
			return true;
		}
		if (action.equals("sendTweet")) {
			String status = "";
			long inReplyToStatusId = 0;
			boolean possiblySensitive = false;
			double latitude = 0;
			double longitude = 0;
			String placeId = "";
			boolean displayCoordinates = false;
			boolean trimUser = false;
			String mediaIds = "";

			String inReplyToStatusIdStr = "";
			String possiblySensitiveStr = "";
			String latitudeStr = "";
			String longitudeStr = "";
			String displayCoordinatesStr = "";
			String trimUserStr = "";

			try {
				status = args.getJSONObject(0).getString("status");
			} catch(JSONException e) {
				callbackContext.error("A status should be provided as an input parameter of the function call: sendTweet");
			}

			try {
				inReplyToStatusIdStr = args.getJSONObject(0).getString("in_reply_to_status_id");
				inReplyToStatusId = Long.valueOf(inReplyToStatusIdStr);
			} catch(JSONException e) {
				//empty
			}

			try {
				possiblySensitiveStr = args.getJSONObject(0).getString("possibly_sensitive");
				possiblySensitive = Boolean.valueOf(possiblySensitiveStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			try {
				latitudeStr = args.getJSONObject(0).getString("lat");
				latitude = Double.valueOf(latitudeStr);
			} catch(JSONException e) {
				//empty
			}

			try {
				longitudeStr = args.getJSONObject(0).getString("long");
				longitude = Double.valueOf(longitudeStr);
			} catch(JSONException e) {
				//empty
			}

			try {
				placeId = args.getJSONObject(0).getString("place_id");
			} catch(JSONException e) {
				//empty
			}

			try {
				displayCoordinatesStr = args.getJSONObject(0).getString("display_coordinates");
				displayCoordinates = Boolean.valueOf(displayCoordinatesStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			try {
				trimUserStr = args.getJSONObject(0).getString("trim_user");
				trimUser = Boolean.valueOf(trimUserStr);
			} catch(JSONException e) {
				//empty since has default value if error occurs
			}

			try {
				mediaIds = args.getJSONObject(0).getString("media_ids");
			} catch(JSONException e) {
				//empty
			}


			sendTweet(status, inReplyToStatusId, possiblySensitive, latitude, longitude, placeId, displayCoordinates, trimUser, mediaIds, callbackContext);
			return true;
		}
		return false;
	}

	private void login(final Activity activity, final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
				twitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
					@Override
					public void success(final Result<TwitterSession> result) {
						Log.v(LOG_TAG, "Successful login session!");
						callbackContext.success(handleResult(result.data));
					}

					@Override
					public void failure(final TwitterException e) {
						Log.v(LOG_TAG, "Failed login session.");
						callbackContext.error("Failed login session.");
					}
				});
			}
		});
	}

	private void logout(final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Log.v(LOG_TAG, "Logged out");
				callbackContext.success();
			}
		});
	}

	private JSONObject handleResult(TwitterSession result) {
		JSONObject response = new JSONObject();
		try {
			response.put("userName", result.getUserName());
			response.put("userId", result.getUserId()); //does not match idStr
			response.put("secret", result.getAuthToken().secret);
			response.put("token", result.getAuthToken().token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	private void handleLoginResult(int requestCode, int resultCode, Intent intent) {
		TwitterLoginButton twitterLoginButton = new TwitterLoginButton(cordova.getActivity());
		twitterLoginButton.onActivityResult(requestCode, resultCode, intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (action.equals("login")) {
			handleLoginResult(requestCode, resultCode, intent);
		}
	}

	private void showUser(final boolean includeEntities, final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				UserShowServiceApi twitterApiClient = new UserShowServiceApi(TwitterCore.getInstance().getSessionManager().getActiveSession());
				UserShowService userService = twitterApiClient.getCustomService();
				Call<User> call = userService.show(TwitterCore.getInstance().getSessionManager().getActiveSession().getUserId(), includeEntities);

				call.enqueue(new Callback<User>() {
					@Override
					public void success(Result<User> result) {
						Log.v(LOG_TAG, "ShowUser API call successful!");
						JSONObject jsonUser = UserObjectToJSON(result.data);
						callbackContext.success(jsonUser);
					}
					@Override
					public void failure(TwitterException e) {
						Log.v(LOG_TAG, "ShowUser API call failed.");
						callbackContext.error(e.getLocalizedMessage());
					}
				});
			}
		});
	}

	private void verifyCredentials(final boolean includeEntities,
								   final boolean skipStatus,
								   final boolean includeEmail,
								   final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				VerifyCredentialsServiceApi twitterApiClient = new VerifyCredentialsServiceApi(TwitterCore.getInstance().getSessionManager().getActiveSession());
				VerifyCredentialsService credentialsService = twitterApiClient.getCustomService();
				Call<User> call = credentialsService.verify(includeEntities, skipStatus, includeEmail);

				call.enqueue(new Callback<User>() {
					@Override
					public void success(Result<User> result) {
						Log.v(LOG_TAG, "VerifyCredentials API call successful!");
						JSONObject jsonUser = UserObjectToJSON(result.data);
						callbackContext.success(jsonUser);
					}
					@Override
					public void failure(TwitterException e) {
						Log.v(LOG_TAG, "VerifyCredentials API call failed.");
						callbackContext.error(e.getLocalizedMessage());
					}
				});
			}
		});
	}

	private void sendTweet(final String status,
						   final long inReplyToStatusId,
						   final boolean possiblySensitive,
						   final double latitude,
						   final double longitude,
						   final String placeId,
						   final boolean displayCoordinates,
						   final boolean trimUser,
						   final String mediaIds,
						   final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
				StatusesService statusesService = twitterApiClient.getStatusesService();
				Call<Tweet> call = statusesService.update(status, inReplyToStatusId, possiblySensitive, latitude, longitude, placeId, displayCoordinates, trimUser, mediaIds);
				call.enqueue(new Callback<Tweet>() {
					@Override
					public void success(Result<Tweet> result) {
						Log.v(LOG_TAG, "VerifyCredentials API call successful!");
						JSONObject jsonUser = TweetObjectToJSON(result.data);
						callbackContext.success(jsonUser);
					}
					@Override
					public void failure(TwitterException exception) {
						Log.v(LOG_TAG, "VerifyCredentials API call failed.");
						callbackContext.error(exception.getLocalizedMessage());
					}
				});
			}
		});
	}

	private JSONObject UserObjectToJSON(User user) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(user);

		JSONObject jsonUser = new JSONObject();
		try {
			jsonUser = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonUser;
	}

	private JSONObject TweetObjectToJSON(Tweet tweet) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(tweet);

		JSONObject jsonTweet = new JSONObject();
		try {
			jsonTweet = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonTweet;
	}

}