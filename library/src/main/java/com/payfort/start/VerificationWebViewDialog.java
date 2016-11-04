package com.payfort.start;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * {@link Dialog} for token verification via {@link WebView}.
 */
class VerificationWebViewDialog extends Dialog implements DialogInterface.OnCancelListener {

    private final Callback callback;
    private final String url;

    VerificationWebViewDialog(Context context, String url, Callback callback) {
        super(context, R.style.FullScreenDialog);
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web_dialog);
        setOnCancelListener(this);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        callback.onDialogCanceled();
    }

    interface Callback {

        void onDialogCanceled();

    }
}
