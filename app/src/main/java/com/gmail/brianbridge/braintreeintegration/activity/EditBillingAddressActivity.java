package com.gmail.brianbridge.braintreeintegration.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.brianbridge.braintreeintegration.BillingAddress;
import com.gmail.brianbridge.braintreeintegration.PaymentService;
import com.gmail.brianbridge.braintreeintegration.R;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class EditBillingAddressActivity extends AppCompatActivity {
	public static final String TAG = EditBillingAddressActivity.class.getSimpleName();

	private PaymentService paymentService = new PaymentService();
	private LinearLayout recentAddressesContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_billing_address);
//
//		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent();
//				intent.putExtra("first_name", ((EditText)findViewById(R.id.editText_firstName)).getText().toString());
//				intent.putExtra("last_name", ((EditText)findViewById(R.id.editText_lastName)).getText().toString());
//				intent.putExtra("", ((EditText)findViewById(R.id.editText_address)).getText().toString());
//				setResult(RESULT_OK, intent);
//				finish();
//			}
//		});

		recentAddressesContainer = (LinearLayout) findViewById(R.id.linearLayout_recentAddressesContainer);
	}
}
