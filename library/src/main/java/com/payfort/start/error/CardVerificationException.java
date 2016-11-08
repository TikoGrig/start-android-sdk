package com.payfort.start.error;

import com.payfort.start.Card;

import java.util.EnumSet;

/**
 * This exception is thrown during {@link Card} validation.
 */
public class CardVerificationException extends Exception {

    private final EnumSet<Card.Field> invalidFields;

    /**
     * Constructs an exception with list of invalid fields
     *
     * @param invalidFields a set with invalid fields
     */
    public CardVerificationException(EnumSet<Card.Field> invalidFields) {
        super("Invalid fields: " + invalidFields);
        this.invalidFields = invalidFields.clone();
    }

    /**
     * Constructs an exception with list of invalid fields and error message.
     *
     * @param invalidFields a set with invalid fields
     * @param message       an error message
     */
    public CardVerificationException(EnumSet<Card.Field> invalidFields, String message) {
        super(message);
        this.invalidFields = invalidFields.clone();
    }

    /**
     * Returns list of invalid card's fields.
     *
     * @return a set of invalid fields.
     */
    public EnumSet<Card.Field> getErrorFields() {
        return invalidFields.clone();
    }

}
