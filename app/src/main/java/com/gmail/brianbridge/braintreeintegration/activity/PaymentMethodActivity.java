package com.gmail.brianbridge.braintreeintegration.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
import com.gmail.brianbridge.braintreeintegration.PaymentService;
import com.gmail.brianbridge.braintreeintegration.R;
import com.gmail.brianbridge.braintreeintegration.adapter.PaymentMethodListAdapter;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
	private PaymentMethodListAdapter adapter;
	private BraintreeFragment braintreeFragment;
	private String clientToken;
	private Dialog dialog;
	private PaymentService paymentService = new PaymentService();

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

		methodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				PaymentMethodNonce nonce = adapter.getItem(i);
				Intent intent = new Intent();
				intent.putExtra("nonce", nonce.getNonce());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
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
		dialog = new ProgressDialog.Builder(this).show();
		PaymentMethod.getPaymentMethodNonces(braintreeFragment, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			adapter.clear();
			adapter.notifyDataSetChanged();
			dialog = new ProgressDialog.Builder(this).show();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					dialog = new AlertDialog.Builder(PaymentMethodActivity.this)
							.setMessage("Payment?")
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									checkout(data.getStringExtra("nonce"));
								}
							})
							.show();
				}
			});
			PaymentMethod.getPaymentMethodNonces(braintreeFragment, true);
		}
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
		adapter = new PaymentMethodListAdapter(this, 0, paymentMethodNonces);
		methodListView.setAdapter(adapter);
		dialog.dismiss();
	}

	private void checkout(String nonce) {
		paymentService.checkout(nonce, "10")
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Boolean>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						dialog.dismiss();
					}

					@Override
					public void onNext(Boolean aBoolean) {
						dialog.dismiss();
						dialog = new AlertDialog.Builder(PaymentMethodActivity.this)
								.setMessage("Paid")
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										finish();
									}
								})
								.show();
					}
				});
	}
}
