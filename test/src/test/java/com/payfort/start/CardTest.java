package com.payfort.start;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.EnumSet;

import static com.payfort.start.Card.Field.CVC;
import static com.payfort.start.Card.Field.EXPIRATION_MONTH;
import static com.payfort.start.Card.Field.EXPIRATION_YEAR;
import static com.payfort.start.Card.Field.NUMBER;
import static com.payfort.start.Card.Field.OWNER;

/**
 * Test for {@link Card} class.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardTest {

    private static final String VALID_NUMBER = "4111111111111111";

    @Test
    public void testNumberValidation() throws Exception {
        assertHasSingleError(NUMBER, "4111111111111112", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "1", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "a", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, null, "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "a111111111111111", "111", 1, 2020, "John Doe");

        // https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
        new Card("378 2822 4631 0005", "111", 1, 2020, "John Doe");
        new Card("3714-4963-5398-43-1", "111", 1, 2020, "John Doe");
        new Card("3787\t3449\t367 10-00", "111", 1, 2020, "John Doe");
        new Card("5610591081018250", "111", 1, 2020, "John Doe");
        new Card("30569309025904", "111", 1, 2020, "John Doe");
        new Card("38520000023237", "111", 1, 2020, "John Doe");
        new Card("6011111111111117", "111", 1, 2020, "John Doe");
        new Card("6011000990139424", "111", 1, 2020, "John Doe");
        new Card("3530111333300000", "111", 1, 2020, "John Doe");
        new Card("3566002020360505", "111", 1, 2020, "John Doe");
        new Card("5555555555554444", "111", 1, 2020, "John Doe");
        new Card("5105105105105100", "111", 1, 2020, "John Doe");
        new Card("4111111111111111", "111", 1, 2020, "John Doe");
        new Card("4012888888881881", "111", 1, 2020, "John Doe");
        new Card("4222222222222", "111", 1, 2020, "John Doe");
        new Card("5019717010103742", "111", 1, 2020, "John Doe");
        new Card("6331101999990016", "111", 1, 2020, "John Doe");
    }

    @Test
    public void testCvcValidation() throws Exception {
        assertHasSingleError(CVC, VALID_NUMBER, null, 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "11", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "12345", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "12q", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "z123 ", 1, 2020, "John Doe");

        new Card(VALID_NUMBER, "123", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "7890", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, " 123 ", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "2 3\t4", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "1\t2-3\n4", 1, 2020, "John Doe");
    }

    @Test
    public void testExpirationDateValidation() throws Exception {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        assertHasSingleError(EXPIRATION_YEAR, VALID_NUMBER, "111", 1, 2010, "John Doe");
        assertHasSingleError(EXPIRATION_YEAR, VALID_NUMBER, "111", 1, 2101, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", 0, currentYear, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", 13, currentYear, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", currentMonth - 1, currentYear, "John Doe");

        new Card(VALID_NUMBER, "123", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "123", currentMonth, currentYear, "John Doe");
        new Card(VALID_NUMBER, "123", 12, currentYear, "John Doe");
    }

    @Test
    public void testOwnerValidation() throws Exception {
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, null);
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\t");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "  ");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\n");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\r");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, " \t\n\r");

        new Card(VALID_NUMBER, "123", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "123", 1, 2020, "A");
        new Card(VALID_NUMBER, "123", 1, 2020, " A");
        new Card(VALID_NUMBER, "123", 1, 2020, "A\t");
    }

    @Test
    public void testMultipleInvalidFields() throws Exception {
        assertHasMultipleError(EnumSet.of(CVC, EXPIRATION_YEAR), VALID_NUMBER, "12345", 1, 2000, "John Doe");
        assertHasMultipleError(EnumSet.allOf(Card.Field.class), "321 345", "12", 13, 2000, "");
        assertHasMultipleError(EnumSet.of(NUMBER, EXPIRATION_MONTH, OWNER), "601111111111111", "999", 13, 2030, null);
    }

    @Test
    public void testDetectBrand() throws Exception {
        Assert.assertEquals(new Card("4000056655665556", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.VISA);
        Assert.assertEquals(new Card("4242424242424242", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.VISA);

        Assert.assertEquals(new Card("5105105105105100", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.MASTER_CARD);
        Assert.assertEquals(new Card("5200828282828210", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.MASTER_CARD);
        Assert.assertEquals(new Card("5555555555554444", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.MASTER_CARD);

        Assert.assertEquals(new Card("371449635398431", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("378282246310005", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("6011000990139424", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("30569309025904", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("38520000023237", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("3530111333300000", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("3566002020360505", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
        Assert.assertEquals(new Card("0000000000000000", "123", 1, 2020, "John Doe").getBrand(), Card.Brand.UNKNOWN);
    }

    @Test
    public void testGetLastDigits() throws Exception {
        Assert.assertEquals(new Card("371449635398431", "123", 1, 2020, "John Doe").getLastDigits(), "8431");
        Assert.assertEquals(new Card("30569309025904", "123", 1, 2020, "John Doe").getLastDigits(), "5904");
        Assert.assertEquals(new Card("0000000000000000", "123", 1, 2020, "John Doe").getLastDigits(), "0000");
    }

    @Test
    public void testGetBin() throws Exception {
        Assert.assertEquals(new Card("3714-4963-5398431", "123", 1, 2020, "John Doe").getBin(), "371449");
        Assert.assertEquals(new Card(" 3056 9309 0259 04", "123", 1, 2020, "John Doe").getBin(), "305693");
        Assert.assertEquals(new Card("0000000000000000", "123", 1, 2020, "John Doe").getBin(), "000000");
    }

    private void assertHasMultipleError(EnumSet<Card.Field> invalidFields, String number, String cvc, int month, int year, String owner) {
        try {
            new Card(number, cvc, month, year, owner);
            Assert.fail();
        } catch (CardVerificationException e) {
            Assert.assertEquals(invalidFields, e.getErrors());
        }
    }

    private void assertHasSingleError(Card.Field invalidField, String number, String cvc, int month, int year, String owner) {
        assertHasMultipleError(EnumSet.of(invalidField), number, cvc, month, year, owner);
    }
}
