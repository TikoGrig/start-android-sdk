package com.payfort.start.sample.support;

import com.payfort.start.Token;
import com.payfort.start.TokenCallback;
import com.payfort.start.error.StartApiException;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * {@link TokenCallback} that waits for results.
 */
public final class WaitForResultTokenCallback implements TokenCallback {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private Token token;
    private StartApiException error;
    private boolean canceled;

    @Override
    public void onSuccess(Token token) {
        this.token = token;
        countDownLatch.countDown();
    }

    @Override
    public void onError(StartApiException error) {
        error.printStackTrace(System.out);
        this.error = error;
        countDownLatch.countDown();
    }

    @Override
    public void onCancel() {
        this.canceled = true;
        countDownLatch.countDown();
    }

    public boolean waitForResult() throws InterruptedException {
        return waitForResult(10);
    }

    public boolean waitForResult(long timeoutInSeconds) throws InterruptedException {
        return countDownLatch.await(timeoutInSeconds, SECONDS);
    }

    public boolean hasResult() {
        return token != null || error != null || canceled;
    }

    public Token getToken() {
        return token;
    }

    public StartApiException getError() {
        return error;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
