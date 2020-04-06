package com.example.healthcare.Remote;

import com.example.healthcare.Model.Results;

public class Common {

    public static Results curentResult;

    private static final String GOOGLE_API_URL= "https://maps.googleapis.com/";
    public static IGoogleApiClient getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleApiClient.class);
    }
}
