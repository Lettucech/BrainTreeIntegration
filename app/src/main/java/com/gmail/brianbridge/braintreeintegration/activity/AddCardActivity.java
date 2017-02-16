package com.gmail.brianbridge.braintreeintegration.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.OnCardFormValidListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.gmail.brianbridge.braintreeintegration.R;
import com.gmail.brianbridge.braintreeintegration.utils.PaymentMethodType;

import java.util.HashSet;
import java.util.Set;

public class AddCardActivity extends AppCompatActivity implements
		CardEditText.OnCardTypeChangedListener,
		OnCardFormValidListener,
		OnCardFormSubmitListener,
		ConfigurationListener,
		BraintreeErrorListener,
		PaymentMethodNonceCreatedListener {
	public static final String TAG = AddCardActivity.class.getSimpleName();
	public static final String BUNDLE_CLIENT_TOKEN = "client_token";

	private BraintreeFragment braintreeFragment;
	private CardForm cardForm;
	private SupportedCardTypesView typesView;
	private CardType[] mSupportedCardTypes;
	private Button addButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_card);

		cardForm = (CardForm) findViewById(R.id.card_form);
		typesView = (SupportedCardTypesView) findViewById(R.id.supportedCardTypesView);
		addButton = (Button) findViewById(R.id.btn_add);

		try {
			braintreeFragment = BraintreeFragment.newInstance(this, getIntent().getStringExtra(BUNDLE_CLIENT_TOKEN));
			braintreeFragment.addListener(this);
		} catch (InvalidArgumentException e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onCardTypeChanged(CardType cardType) {
		if (cardType == CardType.EMPTY) {
			typesView.setSupportedCardTypes(mSupportedCardTypes);
		} else {
			typesView.setSelected(cardType);
		}
	}

	@Override
	public void onCardFormValid(boolean valid) {

	}

	@Override
	public void onCardFormSubmit() {

	}

	@Override
	public void onError(Exception error) {

	}

	@Override
	public void onConfigurationFetched(Configuration configuration) {
		init(configuration);
	}

	@Override
	public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

	}

	public void init(Configuration configuration) {
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, String.valueOf(cardForm.isValid()));
			}
		});

		cardForm.getCardEditText().displayCardTypeIcon(false);
		cardForm.cardRequired(true)
				.expirationRequired(true)
				.cvvRequired(true)
				.setup(this);

		cardForm.setOnCardTypeChangedListener(this);
		cardForm.setOnCardFormValidListener(this);
		cardForm.setOnCardFormSubmitListener(this);

		Set<String> cardTypes = new HashSet<>(configuration.getCardConfiguration().getSupportedCardTypes());
		cardTypes.remove(PaymentMethodType.UNIONPAY.getCanonicalName());
		mSupportedCardTypes = PaymentMethodType.getCardsTypes(cardTypes);
		typesView.setSupportedCardTypes(mSupportedCardTypes);
	}
}
