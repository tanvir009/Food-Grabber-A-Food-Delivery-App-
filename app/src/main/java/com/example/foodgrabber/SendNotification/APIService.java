package com.example.foodgrabber.SendNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAi6zDrZA:APA91bH2SGNY8DsdWJShHrVUJji6a4BN-9CB8cj24N8wrEiyUKoEGpuRqD9IcELU9V0FgrYGOGpMeW2p4-FM8fFpS4tFampg1SLwjIoW6AuaxgYkL9gFAIVYG2A8DBhf-qk5UT_FMymj"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
