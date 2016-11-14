package com.payfort.start;

/**
 * A representation of token received from API.
 */
public class Token {

    private String id;
    private boolean verificationRequired;
    private TokenVerification verification;

    public Token() {
    }

    public Token(Token token, TokenVerification verification) {
        this.id = token.id;
        this.verificationRequired = token.verificationRequired;
        this.verification = verification;
    }

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

    /**
     * Returns token's verification. Is {@code null} if {@link #isVerificationRequired()} returns {@code false}.
     *
     * @return a token's verification
     */
    public TokenVerification getVerification() {
        return verification;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Token{");
        sb.append("id='").append(id).append('\'');
        sb.append(", verificationRequired=").append(verificationRequired);
        sb.append(", verification=").append(verification);
        sb.append('}');
        return sb.toString();
    }
}
