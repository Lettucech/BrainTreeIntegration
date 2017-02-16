package com.gmail.brianbridge.braintreeintegration.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.interfaces.PaymentMethodNoncesUpdatedListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.gmail.brianbridge.braintreeintegration.R;
import com.gmail.brianbridge.braintreeintegration.adapter.PaymentMethodListAdapter;

import java.util.List;

public class PaymentMethodActivity extends AppCompatActivity implements
		ConfigurationListener,
		BraintreeCancelListener,
		BraintreeErrorListener,
		PaymentMethodNoncesUpdatedListener,
		PaymentMethodNonceCreatedListener {
	public static final String TAG = PaymentMethodActivity.class.getSimpleName();
	public static final String BUNDLE_CLIENT_TOKEN = "client_token";
	private ListView methodListView;
	private Button newButton;
	private SimpleAdapter simpleAdapter;
	private BraintreeFragment braintreeFragment;
	private String clientToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_method);

		try {
			clientToken = getIntent().getStringExtra(BUNDLE_CLIENT_TOKEN);
			braintreeFragment = BraintreeFragment.newInstance(this, clientToken);
			braintreeFragment.addListener(this);
		} catch (InvalidArgumentException e) {
			Log.e(TAG, e.toString());
		}

		methodListView = (ListView) findViewById(R.id.listView_method);
		newButton = (Button) findViewById(R.id.btn_new);

		newButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(PaymentMethodActivity.this, AddCardActivity.class);
				intent.putExtra(AddCardActivity.BUNDLE_CLIENT_TOKEN, clientToken);
				startActivityForResult(intent, 0);
			}
		});
		afterViews();
	}

	private void afterViews() {
		PaymentMethod.getPaymentMethodNonces(braintreeFragment, true);
	}

	@Override
	public void onCancel(int requestCode) {
		Log.d(TAG, "onCancel");
	}

	@Override
	public void onError(Exception error) {
		Log.d(TAG, "onError");
	}

	@Override
	public void onConfigurationFetched(Configuration configuration) {
		Log.d(TAG, "onConfigurationFetched");
	}

	@Override
	public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
		Log.d(TAG, "onPaymentMethodNonceCreated");
	}

	@Override
	public void onPaymentMethodNoncesUpdated(List<PaymentMethodNonce> paymentMethodNonces) {
		methodListView.setAdapter(new PaymentMethodListAdapter(this, 0, paymentMethodNonces));
	}
}
