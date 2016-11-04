package com.payfort.start;

/**
 * A results of token verification.
 */
public class TokenVerification {

    private boolean enrolled;
    private boolean finalized;

    public boolean isEnrolled() {
        return enrolled;
    }

    public boolean isFinalized() {
        return finalized;
    }
}