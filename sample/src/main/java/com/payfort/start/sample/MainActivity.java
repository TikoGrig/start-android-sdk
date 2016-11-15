package com.payfort.start.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.payfort.start.Card;
import com.payfort.start.Start;
import com.payfort.start.Token;
import com.payfort.start.TokenCallback;
import com.payfort.start.error.CardVerificationException;
import com.payfort.start.error.StartApiException;

import java.util.EnumSet;

public class MainActivity extends AppCompatActivity implements TokenCallback {

    // TODO: put your open Payfort key here
    private static final String API_OPEN_KEY = "live_open_k_55e06cde7fe8d3141a7e";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText numberEditText;
    private EditText monthEditText;
    private EditText yearEditText;
    private EditText cvcEditText;
    private EditText ownerEditText;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private Button payButton;
    Start start = new Start(API_OPEN_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        numberEditText = (EditText) findViewById(R.id.numberEditText);
        monthEditText = (EditText) findViewById(R.id.monthEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        cvcEditText = (EditText) findViewById(R.id.cvcEditText);
        ownerEditText = (EditText) findViewById(R.id.ownerEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        payButton = (Button) findViewById(R.id.payButton);
    }

    public void pay(View view) {
        try {
            Card card = unbindCard();

            errorTextView.setText(null);
            hideKeyboard();
            showProgress(true);

            start.createToken(this, card, this, 10 * 100, "USD");
        } catch (CardVerificationException e) {
            setErrors(e.getErrorFields());
        }
    }

    private Card unbindCard() throws CardVerificationException {
        clearErrors();
        String number = unbindString(numberEditText);
        int year = unbindInteger(yearEditText);
        int month = unbindInteger(monthEditText);
        String cvc = unbindString(cvcEditText);
        String owner = unbindString(ownerEditText);
        return new Card(number, cvc, month, year, owner);
    }

    private void clearErrors() {
        numberEditText.setError(null);
        monthEditText.setError(null);
        yearEditText.setError(null);
        cvcEditText.setError(null);
        ownerEditText.setError(null);
    }

    private void setErrors(EnumSet<Card.Field> errors) {
        String error = getString(R.string.edit_text_invalid);

        if (errors.contains(Card.Field.NUMBER)) {
            numberEditText.setError(error);
        }
        if (errors.contains(Card.Field.EXPIRATION_YEAR)) {
            yearEditText.setError(error);
        }
        if (errors.contains(Card.Field.EXPIRATION_MONTH)) {
            monthEditText.setError(error);
        }
        if (errors.contains(Card.Field.CVC)) {
            cvcEditText.setError(error);
        }
        if (errors.contains(Card.Field.OWNER)) {
            ownerEditText.setError(error);
        }
    }

    private String unbindString(EditText editText) {
        return editText.getText().toString().trim();
    }

    private int unbindInteger(EditText editText) {
        try {
            String text = unbindString(editText);
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showProgress(boolean progressVisible) {
        payButton.setEnabled(!progressVisible);
        progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSuccess(Token token) {
        Log.d(LOG_TAG, "Token is received: " + token);
        Toast.makeText(this, getString(R.string.congrats, token.getId()), Toast.LENGTH_LONG).show();
        showProgress(false);
    }

    @Override
    public void onError(StartApiException e) {
        Log.e(LOG_TAG, "Error getting token", e);
        errorTextView.setText(R.string.error);
        showProgress(false);
    }

    @Override
    public void onCancel() {
        Log.e(LOG_TAG, "Getting token is canceled by user");
        showProgress(false);
    }
}
