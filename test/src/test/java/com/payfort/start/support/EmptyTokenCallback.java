package com.payfort.start.support;

import com.payfort.start.Token;
import com.payfort.start.TokenCallback;
import com.payfort.start.error.StartApiException;

/**
 * {@link TokenCallback} that does nothing.
 */
public final class EmptyTokenCallback implements TokenCallback {

    @Override
    public void onSuccess(Token token) {
        // do nothing
    }

    @Override
    public void onError(StartApiException error) {
        // do nothing
    }

    @Override
    public void onCancel() {
        // do nothing
    }
}
