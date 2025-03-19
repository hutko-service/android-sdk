package org.hutko.android.demo;

import android.os.Bundle;
import android.widget.EditText;

import org.hutko.android.Card;
import org.hutko.android.CardInputLayout;

public class FlexibleExampleActivity extends BaseExampleActivity {
    private EditText editCard;
    private EditText editExpYy;
    private EditText editExpMm;
    private EditText editCvv;
    private CardInputLayout cardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editCard = findViewById(R.id.edit_card_number);
        editExpYy = findViewById(R.id.edit_yy);
        editExpMm = findViewById(R.id.edit_mm);
        editCvv = findViewById(R.id.edit_cvv);
        // ^^^ these fields used only as example for Hutko.setStrictUiBlocking(false);
        cardLayout = findViewById(R.id.card_layout);
        cardLayout.setCardNumberFormatting(false);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_flexible_example;
    }

    @Override
    protected Card getCard() {
//        Hutko.setStrictUiBlocking(false);
//        Log.i("Hutko", "CardNumber: " + editCard.getText());
//        Log.i("Hutko", "ExpYy: " + editExpYy.getText());
//        Log.i("Hutko", "ExpMm: " + editExpMm.getText());
//        Log.i("Hutko", "Cvv: " + editCvv.getText());

        return cardLayout.confirm(new CardInputLayout.ConfirmationErrorHandler() {
            @Override
            public void onCardInputErrorClear(CardInputLayout view, EditText editText) {

            }

            @Override
            public void onCardInputErrorCatched(CardInputLayout view, EditText editText, String error) {

            }
        });
    }
}
