package com.payfort.start.web;

import android.os.Handler;
import android.os.Looper;

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
     * @param <T>         type of response
     */
    public static <T> void enqueueWithRetry(Call<T> call, Callback<T> callback, int maxAttempts) {
        checkAllNotNull(call, callback);
        checkArgument(maxAttempts > 0, "MaxAttempts must be positive!");

        call.enqueue(new CallbackWithRetryWrapper<>(call, callback, maxAttempts));
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

        call.enqueue(new CallbackWithConditionWrapper<>(call, callback, retryCondition, delayMillis));
    }

    private static class CallbackWithRetryWrapper<T> implements Callback<T> {

        private final int attempts;
        private final Call<T> call;
        private final Callback<T> callback;
        private int retryCount = 0;

        private CallbackWithRetryWrapper(Call<T> call, Callback<T> callback, int attempts) {
            this.call = checkNotNull(call);
            this.callback = checkNotNull(callback);
            checkArgument(attempts > 0);
            this.attempts = attempts;
        }

        @Override
        public void onResponse(Response<T> response) {
            callback.onResponse(response);
        }

        @Override
        public void onFailure(Throwable t) {
            if (retryCount++ < attempts) {
                call.clone().enqueue(this);
            } else {
                callback.onFailure(t);
            }
        }
    }

    private static class CallbackWithConditionWrapper<T> implements Callback<T> {

        private final Handler handler;
        private final long delayMillis;
        private final Call<T> call;
        private final Callback<T> callback;
        private final RetryCondition<T> retryCondition;

        private CallbackWithConditionWrapper(Call<T> call, Callback<T> callback, RetryCondition<T> retryCondition, long delayMillis) {
            this.call = call;
            this.callback = callback;
            this.retryCondition = retryCondition;
            this.delayMillis = delayMillis;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onResponse(Response<T> response) {
            if (response.isSuccess()) {
                T body = response.body();
                if (retryCondition.doRetry(body)) {
                    scheduleRetry();
                } else {
                    callback.onResponse(response);
                }
            } else {
                scheduleRetry();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            scheduleRetry();
        }

        private void retry() {
            if (!call.isCanceled()) {
                call.clone().enqueue(this);
            }
        }

        private void scheduleRetry() {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    retry();
                }
            }, delayMillis);
        }
    }

    public interface RetryCondition<T> {

        boolean doRetry(T t);

    }
}
