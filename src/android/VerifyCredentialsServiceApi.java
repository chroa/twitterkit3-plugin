package com.manifestwebdesign.twitterconnect;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class VerifyCredentialsServiceApi extends TwitterApiClient {
    public VerifyCredentialsServiceApi(TwitterSession session) {
        super(session);
    }

    public VerifyCredentialsService getCustomService() {
        return getService(VerifyCredentialsService.class);
    }
}

interface VerifyCredentialsService {
    @GET("/1.1/account/verify_credentials.json")
    Call<User> verify(@Query("include_entities") boolean includeEntities,
                    @Query("skip_status") boolean skipStatus,
                    @Query("include_email") boolean includeEmail);
}
