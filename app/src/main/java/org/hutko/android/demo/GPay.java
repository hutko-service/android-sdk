package org.hutko.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.hutko.android.Hutko;
import org.hutko.android.HutkoWebView;
import org.hutko.android.GooglePayCall;
import org.hutko.android.Order;
import org.hutko.android.Receipt;


public class GPay extends AppCompatActivity implements
        View.OnClickListener, // Implementing OnClickListener for handling button clicks
        Hutko.PayCallback, // Implementing Hutko.PayCallback for payment callbacks
        Hutko.GooglePayCallback { // Implementing Hutko.GooglePayCallback for Google Pay callbacks

    private static final int RC_GOOGLE_PAY = 100500;
    private static final String K_GOOGLE_PAY_CALL = "google_pay_call";
    private Hutko hutko;
    private GooglePayCall googlePayCall; // <- this should be serialized on saving instance state
    private HutkoWebView webView;
    private Button googlePayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexible_example); // Set the layout for this activity

        // Initialize UI elements
        webView = findViewById(R.id.web_view); // Initialize HutkoWebView from layout
        googlePayButton = findViewById(R.id.btn_pay_google); // Initialize Button from layout
        googlePayButton.setOnClickListener(this); // Set click listener for Google Pay button

        // Check if Google Pay is supported and set button visibility accordingly
        if (Hutko.supportsGooglePay(this)) {
            googlePayButton.setVisibility(View.VISIBLE); // Show Google Pay button
        } else {
            googlePayButton.setVisibility(View.GONE); // Hide Google Pay button if unsupported
            Toast.makeText(this, R.string.e_google_pay_unsupported, Toast.LENGTH_LONG).show(); // Show unsupported message
        }

        if (savedInstanceState != null) {
            googlePayCall = savedInstanceState.getParcelable(K_GOOGLE_PAY_CALL);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.waitingForConfirm()) {
            webView.skipConfirm(); // Skip confirmation in WebView if waiting
        } else {
            super.onBackPressed(); // Otherwise, perform default back button behavior
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pay_google) {
            processGooglePay(); // Handle click on Google Pay button
//            processGooglePayWithToken(); // Handle click on Google Pay button
        }
    }

    private void processGooglePay() {
        // Initialize Hutko with merchant ID and WebView
        hutko = new Hutko(0, webView); // Initialize the payment process with the merchant ID
        final Order googlePayOrder = createOrder(); // Create order for Google Pay payment
        if (googlePayOrder != null) {
            hutko.googlePayInitialize(googlePayOrder, this, RC_GOOGLE_PAY, this); // Initialize Google Pay payment
        }
    }

    private void processGooglePayWithToken() {
        // Initialize Hutko with merchant ID and WebView
        hutko = new Hutko(0, webView); // Initialize the payment process with the merchant ID
        hutko.googlePayInitialize("321d7ebe83c2b34ce38fee59c4c845e9fef67a0b", this, RC_GOOGLE_PAY, this); // Initialize Google Pay payment
    }



    private Order createOrder() {
        final int amount = 100;
        final String email = "test@gmail.com";
        final String description = "test payment";
        final String currency = "GEL";
        return new Order(amount, currency, "vb_" + System.currentTimeMillis(), description, email); // Create and return new payment order
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(K_GOOGLE_PAY_CALL, googlePayCall);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_GOOGLE_PAY:
                if (!hutko.googlePayComplete(resultCode, data, googlePayCall, this)) {
                    Toast.makeText(this, R.string.e_google_pay_canceled, Toast.LENGTH_LONG).show(); // Show payment canceled message
                }
                break;
        }
    }

    @Override
    public void onPaidProcessed(Receipt receipt) {
        Toast.makeText(this, "Paid " + receipt.status.name() + "\nPaymentId:" + receipt.paymentId, Toast.LENGTH_LONG).show(); // Show payment success message
        Log.d("PaymentStatus", "Paid " + receipt.status.name() + " PaymentId: " + receipt.paymentId);

    }

    @Override
    public void onPaidFailure(Hutko.Exception e) {
        if (e instanceof Hutko.Exception.Failure) {
            Hutko.Exception.Failure f = (Hutko.Exception.Failure) e;
            Toast.makeText(this, "Failure\nErrorCode: " +
                    f.errorCode + "\nMessage: " + f.getMessage() + "\nRequestId: " + f.requestId, Toast.LENGTH_LONG).show(); // Show specific failure details
        } else if (e instanceof Hutko.Exception.NetworkSecurity) {
            Toast.makeText(this, "Network security error: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Show network security error
        } else if (e instanceof Hutko.Exception.ServerInternalError) {
            Toast.makeText(this, "Internal server error: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Show internal server error
        } else if (e instanceof Hutko.Exception.NetworkAccess) {
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show(); // Show network access error
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show(); // Show generic payment failure
        }
        e.printStackTrace(); // Print stack trace for debugging
    }

    @Override
    public void onGooglePayInitialized(GooglePayCall result) {
        // Handle Google Pay initialization if needed
        Toast.makeText(this, "Google Pay initialized", Toast.LENGTH_LONG).show(); // Show Google Pay initialization message
        this.googlePayCall = result; // Store Google Pay call result
    }
}
