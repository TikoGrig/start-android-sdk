package com.payfort.start;

import android.text.TextUtils;

import com.payfort.start.error.CardVerificationException;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.regex.Pattern;

/**
 * A representation of plastic card. It let developer validate card details and show errors on payment form.
 */
public class Card {

    private static final Pattern CVC_PATTERN = Pattern.compile("[0-9]{3,4}");

    public final String number;
    public final String cvc;
    public final int expirationMonth;
    public final int expirationYear;
    public final String owner;

    /**
     * Constructs new instance and throw {@link CardVerificationException} if any field is invalid.
     *
     * @param number          a number of card
     * @param cvc             a cvc of card
     * @param expirationMonth an expiration month of card
     * @param expirationYear  an expiration year of card
     * @param owner           an owner of card
     * @throws CardVerificationException if any field is invalid
     */
    public Card(String number, String cvc, int expirationMonth, int expirationYear, String owner) throws CardVerificationException {
        this.number = normalize(number);
        this.cvc = normalize(cvc);
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.owner = owner;

        validate();
    }

    /**
     * Detects and return card's issuer.
     *
     * @return a brand of card
     */
    public Brand getBrand() {
        return Brand.detect(number);
    }

    /**
     * Returns last 4 number digits.
     *
     * @return last digits
     */
    public String getLastDigits() {
        return number.substring(number.length() - 4);
    }

    /**
     * Returns bin of card (first 6 number's digits).
     *
     * @return first 6 digits
     */
    public String getBin() {
        return number.substring(0, 6);
    }

    private String normalize(String text) {
        return text == null ? null : text.replaceAll("[\\s\\.-]", "");
    }

    private void validate() throws CardVerificationException {
        EnumSet<Field> invalidFields = EnumSet.noneOf(Field.class);

        if (!isNumberValid()) {
            invalidFields.add(Field.NUMBER);
        }
        if (!isCvcValid()) {
            invalidFields.add(Field.CVC);
        }
        if (!isExpirationMonthValid()) {
            invalidFields.add(Field.EXPIRATION_MONTH);
        }
        if (!isExpirationYearValid()) {
            invalidFields.add(Field.EXPIRATION_YEAR);
        }
        if (!isOwnerValid()) {
            invalidFields.add(Field.OWNER);
        }

        if (!invalidFields.isEmpty()) {
            throw new CardVerificationException(invalidFields);
        }
    }

    private boolean isNumberValid() {
        return number != null &&
                TextUtils.isDigitsOnly(number) &&
                number.length() >= 12 && number.length() <= 19 &&   // https://en.wikipedia.org/wiki/Payment_card_number#Issuer_identification_number_.28IIN.29
                checkLuhn(getNumberDigits());
    }

    private int[] getNumberDigits() {
        int[] digits = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            digits[i] = number.charAt(i) - '0';
        }
        return digits;
    }

    // https://de.wikipedia.org/wiki/Luhn-Algorithmus#Java
    private boolean checkLuhn(int[] digits) {
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {

            // get digits in reverse order
            int digit = digits[length - i - 1];

            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }

    private boolean isCvcValid() {
        return cvc != null && CVC_PATTERN.matcher(cvc).matches();
    }

    private boolean isExpirationMonthValid() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        return expirationMonth >= 1 && expirationMonth <= 12 &&
                (expirationYear != currentYear || expirationMonth >= currentMonth);
    }

    private boolean isExpirationYearValid() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return expirationYear >= currentYear && expirationYear <= 2100;
    }

    private boolean isOwnerValid() {
        return owner != null && !owner.trim().isEmpty();
    }

    /**
     * A list of {@link Card} attributes.
     */
    public enum Field {
        NUMBER, CVC, EXPIRATION_MONTH, EXPIRATION_YEAR, OWNER
    }

    /**
     * A list of card issuers.
     */
    public enum Brand {

        VISA(new int[]{13, 16}, "4"),
        MASTER_CARD(new int[]{16}, "50", "51", "52", "53", "54", "55", "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "271", "2720"),
        UNKNOWN(new int[0]);

        private final String[] prefixes;
        private final int[] length;

        Brand(int[] length, String... prefixes) {
            this.length = length;
            this.prefixes = prefixes;
        }

        public static Brand detect(String number) {
            for (Brand brand : values()) {
                if (hasPrefix(number, brand.prefixes) && hasLength(number, brand.length)) {
                    return brand;
                }
            }
            return UNKNOWN;
        }

        private static boolean hasPrefix(String number, String[] prefixes) {
            for (String prefix : prefixes) {
                if (number.startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean hasLength(String number, int[] lengthList) {
            for (int length : lengthList) {
                if (number.length() == length) {
                    return true;
                }
            }
            return false;
        }
    }
}