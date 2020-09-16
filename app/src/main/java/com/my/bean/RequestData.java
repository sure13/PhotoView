package com.my.bean;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface RequestData {

    @GET("bimg/338/{fileName}") //{fileName}是动态码
    @Streaming
    Observable<ResponseBody> downloadImage(@Path("fileName") String fileName);

}
