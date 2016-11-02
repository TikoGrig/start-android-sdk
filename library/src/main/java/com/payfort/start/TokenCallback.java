package com.payfort.start;

/**
 * Callback for asynchronous work with {@link Start}.
 */
public interface TokenCallback {

    /**
     * To be called by {@link Start} after successful receiving {@link Token}.
     *
     * @param token a token received from API
     */
    void onSuccess(Token token);

    /**
     * To be called if error during receiving token occurs.
     *
     * @param error an error occurred during receiving token from API
     */
    void onError(Exception error);

    /**
     * To be called if user canceled token receiving process.
     */
    void onCancel();

}
