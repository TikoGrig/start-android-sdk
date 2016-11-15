package com.payfort.start.support;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * {@link Callback} that waits for results.
 */
public final class WaitForResultWebCallback<T> implements Callback<T> {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private Response<T> response;
    private Throwable error;

    public boolean waitForResult() throws InterruptedException {
        return waitForResult(10);
    }

    public boolean waitForResult(long timeoutInSeconds) throws InterruptedException {
        return countDownLatch.await(timeoutInSeconds, SECONDS);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        this.response = response;
        countDownLatch.countDown();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace(System.out);
        this.error = t;
        countDownLatch.countDown();
    }

    public Response<T> getResponse() {
        return response;
    }

    public Throwable getError() {
        return error;
    }
}
