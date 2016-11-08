package com.payfort.start;

import com.payfort.start.error.CardVerificationException;
import com.payfort.start.test.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.EnumSet;

import static com.payfort.start.Card.Field.CVC;
import static com.payfort.start.Card.Field.EXPIRATION_MONTH;
import static com.payfort.start.Card.Field.EXPIRATION_YEAR;
import static com.payfort.start.Card.Field.NUMBER;
import static com.payfort.start.Card.Field.OWNER;
import static org.junit.Assert.assertEquals;

/**
 * Test for {@link Card} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class CardTest {

    private static final String VALID_NUMBER = "4111111111111111";

    @Test
    public void testInvalidNumber() throws Exception {
        assertHasSingleError(NUMBER, "4111111111111112", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "1", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "a", "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, null, "111", 1, 2020, "John Doe");
        assertHasSingleError(NUMBER, "a111111111111111", "111", 1, 2020, "John Doe");
    }

    @Test
    public void testValidNumber() throws Exception {
        // https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
        newCard("378 2822 4631 0005");
        newCard("3714-4963-5398-43-1");
        newCard("3787\t3449\t367 10-00");
        newCard("5610591081018250");
        newCard("30569309025904");
        newCard("38520000023237");
        newCard("6011111111111117");
        newCard("6011000990139424");
        newCard("3530111333300000");
        newCard("3566002020360505");
        newCard("5555555555554444");
        newCard("5105105105105100");
        newCard("4111111111111111");
        newCard("4012888888881881");
        newCard("4222222222222");
        newCard("5019717010103742");
        newCard("6331101999990016");
    }

    @Test
    public void testInvalidCvc() throws Exception {
        assertHasSingleError(CVC, VALID_NUMBER, null, 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "11", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "12345", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "12q", 1, 2020, "John Doe");
        assertHasSingleError(CVC, VALID_NUMBER, "z123 ", 1, 2020, "John Doe");
    }

    @Test
    public void testValidCvc() throws Exception {
        new Card(VALID_NUMBER, "123", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "7890", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, " 123 ", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "2 3\t4", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "1\t2-3\n4", 1, 2020, "John Doe");
    }

    @Test
    public void testInvalidExpirationDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        assertHasSingleError(EXPIRATION_YEAR, VALID_NUMBER, "111", 1, 2010, "John Doe");
        assertHasSingleError(EXPIRATION_YEAR, VALID_NUMBER, "111", 1, 2101, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", 0, currentYear, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", 13, currentYear, "John Doe");
        assertHasSingleError(EXPIRATION_MONTH, VALID_NUMBER, "111", currentMonth - 1, currentYear, "John Doe");
    }

    @Test
    public void testValidExpirationDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        new Card(VALID_NUMBER, "123", 1, 2020, "John Doe");
        new Card(VALID_NUMBER, "123", currentMonth, currentYear, "John Doe");
        new Card(VALID_NUMBER, "123", 12, currentYear, "John Doe");
    }

    @Test
    public void testInvalidOwner() throws Exception {
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, null);
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\t");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "  ");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\n");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, "\r");
        assertHasSingleError(OWNER, VALID_NUMBER, "111", 1, 2020, " \t\n\r");
    }

    @Test
    public void testValidOwner() throws Exception {
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
    public void testDetectVisa() throws Exception {
        assertEquals(newCard("4000056655665556").getBrand(), Card.Brand.VISA);
        assertEquals(newCard("4242424242424242").getBrand(), Card.Brand.VISA);
        assertEquals(newCard("4012 8888 8888 1881").getBrand(), Card.Brand.VISA);
        assertEquals(newCard("4111 1111 1111 1111").getBrand(), Card.Brand.VISA);
        assertEquals(newCard("4222 2222 2222 2").getBrand(), Card.Brand.VISA);
        assertEquals(newCard("4917 6100 0000 0000").getBrand(), Card.Brand.VISA);
    }

    @Test
    public void testDetectMasterCard() throws Exception {
        assertEquals(newCard("5200828282828210").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("5274 5763 9425 9961").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("5555 5555 5555 4444").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("5105 1051 0510 5100").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2720 1700 0000 0006").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2223 4800 0000 0001").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2223 0400 0000 0003").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2223 0700 0000 0000").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2223 2700 0000 0006").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2720 3500 0000 0004").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2223 1000 0000 0005").getBrand(), Card.Brand.MASTER_CARD);
        assertEquals(newCard("2720 0500 0000 0000").getBrand(), Card.Brand.MASTER_CARD);
    }

    @Test
    public void testDetectUnknownBrand() throws Exception {
        assertEquals(newCard("371449635398431").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("378282246310005").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("6011000990139424").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("30569309025904").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("38520000023237").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("3530111333300000").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("3566002020360505").getBrand(), Card.Brand.UNKNOWN);
        assertEquals(newCard("0000000000000000").getBrand(), Card.Brand.UNKNOWN);
    }

    @Test
    public void testGetLastDigits() throws Exception {
        assertEquals(newCard("371449635398431").getLastDigits(), "8431");
        assertEquals(newCard("30569309025904").getLastDigits(), "5904");
        assertEquals(newCard("0000000000000000").getLastDigits(), "0000");
    }

    @Test
    public void testGetBin() throws Exception {
        assertEquals(newCard("3714-4963-5398431").getBin(), "371449");
        assertEquals(newCard(" 3056 9309 0259 04").getBin(), "305693");
        assertEquals(newCard("0000000000000000").getBin(), "000000");
    }

    private Card newCard(String number) throws CardVerificationException {
        return new Card(number, "123", 1, 2020, "John Doe");
    }

    private void assertHasMultipleError(EnumSet<Card.Field> invalidFields, String number, String cvc, int month, int year, String owner) {
        try {
            new Card(number, cvc, month, year, owner);
            Assert.fail();
        } catch (CardVerificationException e) {
            assertEquals(invalidFields, e.getErrorFields());
        }
    }

    private void assertHasSingleError(Card.Field invalidField, String number, String cvc, int month, int year, String owner) {
        assertHasMultipleError(EnumSet.of(invalidField), number, cvc, month, year, owner);
    }
}
