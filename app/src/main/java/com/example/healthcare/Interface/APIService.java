package com.example.healthcare.Interface;

import com.example.healthcare.Notification.MyResponse;
import com.example.healthcare.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
        {
             "Content-Type:application/json",
             "Authorization:key=AAAAMruvUIs:APA91bHf2-GBeVzUiFyhbP1rKFrK2ZqDufrJ346b4FbbOC9VyG0iyvxE9v4VF4m-P11Qxooyd51Sj7rdI39OHNUMDLd8u9QxJpM6fSfX37MEAlr1E91UrOPMmfrlYVfkFFVzdAGxRyDD"
        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
