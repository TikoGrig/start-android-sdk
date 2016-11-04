package com.payfort.start.web;

import com.payfort.start.Token;
import com.payfort.start.TokenVerification;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Definition of Start API.
 */
public interface StartApi {

    @FormUrlEncoded
    @POST("tokens/")
    Call<Token> createToken(
            @Field("number") String number,
            @Field("cvc") String cvc,
            @Field("exp_month") int expirationMonth,
            @Field("exp_year") int expirationYear,
            @Field("name") String owner
    );

    @FormUrlEncoded
    @POST("tokens/{token}/verification")
    Call<TokenVerification> createTokenVerification(
            @Path("token") String token,
            @Field("amount") int amount,
            @Field("currency") String currency
    );

    @GET("tokens/{token}/verification")
    Call<TokenVerification> getTokenVerification(@Path("token") String token);
}
