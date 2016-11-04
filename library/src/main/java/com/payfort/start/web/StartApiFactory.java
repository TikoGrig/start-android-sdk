package com.payfort.start.web;

import android.os.Build;

import java.io.IOException;
import java.util.Collections;

import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static com.payfort.start.web.HttpLoggingInterceptor.Level.BODY;

/**
 * Factory for creating {@link StartApi} instances.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class StartApiFactory {

    public static final String BASE_URL = "https://api.start.payfort.com/";

    public static StartApi newStartApi(String apiKey) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(newClient(apiKey))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(StartApi.class);
    }

    private static OkHttpClient newClient(String apiKey) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HeadersInterceptor(apiKey));
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(BODY));
        enableTls12(builder);
        return builder.build();
    }

    private static void enableTls12(OkHttpClient.Builder clientBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build();
            clientBuilder.connectionSpecs(Collections.singletonList(connectionSpec));
        } else {
            clientBuilder.sslSocketFactory(new TLSSocketFactory());
        }
    }

    private static final class HeadersInterceptor implements Interceptor {

        private final String apiKey;

        private HeadersInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();
            builder.header("Authorization", Credentials.basic(apiKey, ""));
            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
