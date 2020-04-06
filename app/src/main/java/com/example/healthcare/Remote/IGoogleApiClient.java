package com.example.healthcare.Remote;

import com.example.healthcare.Model.Root;

import java.lang.annotation.Target;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApiClient {
    @GET
    Call<Root> getDetailOfCurrentLocation(@Url String url);
}
