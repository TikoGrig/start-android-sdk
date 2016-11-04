package com.payfort.start;

/**
 * A representation of token received from API.
 */
public class Token {

    private String id;
    private boolean verificationRequired;

    /**
     * Returns id of token.
     *
     * @return a token's id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns does card require additional verification steps.
     *
     * @return {code true} if card requires verification
     */
    public boolean isVerificationRequired() {
        return verificationRequired;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Token{");
        sb.append("id='").append(id).append('\'');
        sb.append(", verificationRequired=").append(verificationRequired);
        sb.append('}');
        return sb.toString();
    }
}
