
package com.example.abhilash.newsforyou.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Abhilash on 5/23/2016.
 */

public interface BingNewsAPI {

    @GET("bing/v5.0/news/")
    Call<News> getBingNews(
            @Query("Category") String Category,@Query("mkt") String Market
            , @Query("count") String count);

    @GET("bing/v5.0/news/search")
    Call<News> getBingSearch(
            @Query("q") String Category,@Query("mkt") String Market
            , @Query("count") String count);

}




