package com.gmail.brianbridge.braintreeintegration.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.OnCardFormValidListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.gmail.brianbridge.braintreeintegration.PaymentService;
import com.gmail.brianbridge.braintreeintegration.R;
import com.gmail.brianbridge.braintreeintegration.utils.PaymentMethodType;

import java.util.HashSet;
import java.util.Set;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddCardActivity extends AppCompatActivity implements
		CardEditText.OnCardTypeChangedListener,
		OnCardFormValidListener,
		OnCardFormSubmitListener,
		ConfigurationListener,
		BraintreeErrorListener,
		PaymentMethodNonceCreatedListener,
		View.OnClickListener {
	public static final String TAG = AddCardActivity.class.getSimpleName();
	public static final String BUNDLE_CLIENT_TOKEN = "client_token";

	private BraintreeFragment braintreeFragment;
	private CardForm cardForm;
	private SupportedCardTypesView typesView;
	private CardType[] mSupportedCardTypes;
	private Button addButton;
	private Button scanButton;
	private PaymentService paymentService = new PaymentService();
	private Dialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_card);

		cardForm = (CardForm) findViewById(R.id.card_form);
		typesView = (SupportedCardTypesView) findViewById(R.id.supportedCardTypesView);
		scanButton = (Button) findViewById(R.id.btn_scan);
		addButton = (Button) findViewById(R.id.btn_add);
		addButton.setEnabled(false);

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
		Log.d(TAG, "onCardFormValid");
		if (valid) {
			addButton.setEnabled(true);
		} else {
			addButton.setEnabled(false);
			cardForm.validate();
		}
	}

	@Override
	public void onCardFormSubmit() {
		Log.d(TAG, "onCardFormSubmit");
	}

	@Override
	public void onError(Exception error) {
		Log.d(TAG, "onError = " + error.toString());
		dialog.dismiss();
	}

	@Override
	public void onConfigurationFetched(Configuration configuration) {
		init(configuration);
	}

	@Override
	public void onPaymentMethodNonceCreated(final PaymentMethodNonce paymentMethodNonce) {
		Log.d(TAG, "nonce = " + paymentMethodNonce.getNonce().substring(0, 10) + "...");
		addButton.setEnabled(true);
		paymentService.addPaymentMethod(paymentMethodNonce.getNonce())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(String nonce) {
						if (nonce != null) {
							dialog.dismiss();
							Intent intent = new Intent();
							intent.putExtra("nonce", nonce);
							setResult(RESULT_OK, intent);
							finish();
						}
					}
				});
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "clicked");
		if (view.getId() == R.id.btn_scan) {
			cardForm.scanCard(this);
		} else {
			dialog = new ProgressDialog.Builder(this).show();

			CardBuilder builder = new CardBuilder()
					.cardNumber(cardForm.getCardNumber())
					.expirationDate(cardForm.getExpirationMonth() + "/" + cardForm.getExpirationYear())
					.cvv(cardForm.getCvv());

			Card.tokenize(braintreeFragment, builder);
		}
	}

	public void init(Configuration configuration) {
		addButton.setOnClickListener(this);
		scanButton.setOnClickListener(this);

		cardForm.getCardEditText().displayCardTypeIcon(false);
		cardForm.cardRequired(true)
				.expirationRequired(true)
				.cvvRequired(true)
				.setup(this);

		cardForm.setOnCardTypeChangedListener(this);
		cardForm.setOnCardFormValidListener(this);

		Set<String> cardTypes = new HashSet<>(configuration.getCardConfiguration().getSupportedCardTypes());
		cardTypes.remove(PaymentMethodType.UNIONPAY.getCanonicalName());
		mSupportedCardTypes = PaymentMethodType.getCardsTypes(cardTypes);
		typesView.setSupportedCardTypes(mSupportedCardTypes);
	}
}
