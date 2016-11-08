package com.payfort.start.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.payfort.start.util.Preconditions.checkAllNotNull;
import static com.payfort.start.util.Preconditions.checkArgument;
import static com.payfort.start.util.Preconditions.checkNotNull;

/**
 * Some helpful static methods for {@link retrofit2.Retrofit}
 */
public class RetrofitUtils {

    /**
     * Enqueue request call with retries.
     *
     * @param call        a call to be enqueue. Can't be null
     * @param callback    a callback for call. Can't be null
     * @param maxAttempts a max count of attempts. Must be positive
     * @param delayMillis a delay between retries in milliseconds. Must be zero or positive
     * @param <T>         type of response
     */
    public static <T> void enqueueWithRetry(Call<T> call, Callback<T> callback, int maxAttempts, long delayMillis) {
        checkAllNotNull(call, callback);
        checkArgument(maxAttempts > 0, "MaxAttempts must be positive!");
        checkArgument(delayMillis >= 0, "Delay must be zero or positive!");

        call.enqueue(new CallbackWithRetryWrapper<>(callback, maxAttempts, delayMillis));
    }

    /**
     * Enqueue request call until condition will be satisfied or original call is not canceled.
     *
     * @param call           a call to be enqueue. Can't be null
     * @param callback       a callback for call. Can't be null
     * @param retryCondition a condition. Can't be null
     * @param delayMillis    a delay between retries in milliseconds. Must be zero or positive
     * @param <T>            type of response
     */
    public static <T> void enqueueWithCondition(Call<T> call, Callback<T> callback, RetryCondition<T> retryCondition, long delayMillis) {
        checkAllNotNull(call, callback, retryCondition);
        checkArgument(delayMillis >= 0, "Delay must be zero or positive!");

        call.enqueue(new CallbackWithConditionWrapper<>(callback, retryCondition, delayMillis));
    }

    /**
     * Read raw server's error response.
     *
     * @param errorResponse retrofit response.
     * @return a raw error response or empty string if error reading response occurs
     */
    public static String getRawErrorBody(Response<?> errorResponse) {
        ResponseBody errorBody = errorResponse.errorBody();
        try {
            return errorBody.string();
        } catch (IOException e) {
            Log.e(RetrofitUtils.class.getSimpleName(), "Error reading error response", e);
            return "";
        } finally {
            errorBody.close();
        }
    }

    private static class CallbackWithRetryWrapper<T> implements Callback<T> {

        private final int attempts;
        private final Callback<T> callback;
        private final long delayMillis;
        private final Handler handler;
        private int retryCount = 0;

        private CallbackWithRetryWrapper(Callback<T> callback, int attempts, long delayMillis) {
            this.callback = checkNotNull(callback);
            checkArgument(attempts > 0);
            this.attempts = attempts;
            this.delayMillis = delayMillis;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            callback.onResponse(call, response);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (retryCount++ < attempts) {
                scheduleRetry(call);
            } else {
                callback.onFailure(call, t);
            }
        }

        private void retry(Call<T> call) {
            if (!call.isCanceled()) {
                call.clone().enqueue(this);
            }
        }

        protected void scheduleRetry(final Call<T> call) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    retry(call);
                }
            }, delayMillis);
        }
    }

    private static class CallbackWithConditionWrapper<T> extends CallbackWithRetryWrapper<T> {

        private final RetryCondition<T> retryCondition;

        private CallbackWithConditionWrapper(Callback<T> callback, RetryCondition<T> retryCondition, long delayMillis) {
            super(callback, Integer.MAX_VALUE, delayMillis);
            this.retryCondition = retryCondition;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                T body = response.body();
                if (retryCondition.doRetry(body)) {
                    scheduleRetry(call);
                } else {
                    super.onResponse(call, response);
                }
            } else {
                scheduleRetry(call);
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            scheduleRetry(call);
        }

    }

    public interface RetryCondition<T> {

        boolean doRetry(T t);

    }
}
