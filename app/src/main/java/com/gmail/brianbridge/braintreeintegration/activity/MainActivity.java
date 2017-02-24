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
import android.widget.Button;

import com.gmail.brianbridge.braintreeintegration.PaymentService;
import com.gmail.brianbridge.braintreeintegration.R;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	public static final int REQUEST_SELECT_METHOD = 100;
	private String clientToken;
	private PaymentService paymentService = new PaymentService();
	private Dialog dialog;

	private Button payButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		payButton = (Button) findViewById(R.id.btn_pay);
		payButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getClientToken();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SELECT_METHOD && resultCode == RESULT_OK) {
			dialog = new AlertDialog.Builder(this)
					.setMessage("Use: " + data.getStringExtra("nonce").substring(0, 20) + " to pay?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialog.dismiss();
						}
					})
					.show();
		}
	}

	private void getClientToken() {
		paymentService.getClientToken().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<String>() {
					@Override
					public void onStart() {
						dialog = new ProgressDialog(MainActivity.this);
						dialog.setTitle("Getting Client Token");
						dialog.setCancelable(false);
						dialog.show();
					}

					@Override
					public void onCompleted() {
						dialog.dismiss();
						Intent intent = new Intent(MainActivity.this, PaymentMethodActivity.class);
						intent.putExtra(PaymentMethodActivity.BUNDLE_CLIENT_TOKEN, clientToken);
						startActivityForResult(intent, REQUEST_SELECT_METHOD);
					}

					@Override
					public void onError(Throwable e) {
						dialog.dismiss();
						Log.e(TAG, e.toString());
					}

					@Override
					public void onNext(String s) {
						clientToken = s;
						Log.d(TAG, "Client Token Got: " + clientToken.substring(0, 10) + "...");
					}
				});
	}
}
