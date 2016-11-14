package com.payfort.start.web;

import android.os.Build;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payfort.start.BuildConfig;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                .addConverterFactory(GsonConverterFactory.create(newGson()))
                .build().create(StartApi.class);
    }

    private static Gson newGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static OkHttpClient newClient(String apiKey) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                //.addInterceptor(new HttpLoggingInterceptor().setLevel(BODY))
                .addInterceptor(new HeadersInterceptor(apiKey));
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
            clientBuilder.sslSocketFactory(new TLSSocketFactory(), getTrustManager());
        }
    }

    private static X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Error creating TrustManager", e);
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
            builder.header("User-Agent", "StartAndroid " + BuildConfig.VERSION_NAME);
            builder.header("Authorization", Credentials.basic(apiKey, ""));
            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
