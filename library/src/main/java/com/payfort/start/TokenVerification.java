package com.payfort.start;

/**
 * A results of token verification.
 */
public class TokenVerification {

    private String id;
    private boolean enrolled;
    private boolean finalized;

    public boolean isEnrolled() {
        return enrolled;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenVerification{");
        sb.append("id='").append(id).append('\'');
        sb.append(", enrolled=").append(enrolled);
        sb.append(", finalized=").append(finalized);
        sb.append('}');
        return sb.toString();
    }
}
