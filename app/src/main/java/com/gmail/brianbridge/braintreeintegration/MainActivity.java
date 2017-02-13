package com.gmail.brianbridge.braintreeintegration;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();

	public interface Endpoints {
		@GET("payment/token")
		Observable<String> getToken(
				@Query("customer_id") String customerId
		);

		@GET("payment/checkout")
		Observable<Boolean> checkout(
				@Query("payment_method_nonce") String nonce
		);
	}

	private static String clientToken;
	private static String customerId = "71243647";
	private static String authToken = "Y081dVhxTXFZdUJabThCWmhvTkhSdz09OnhvcVFkUmNyTXN6aXg0MXcrc3M4emI1aEViY2IycVUzZnErZkxRR2V3dXdPTEJZN1pzQkpFZnl0SEl1a1pVK1E1M3pzVU5xWFN3MGFzcGxtVC85RkJBPT0";
	public static final String SERVICE_HEADER_AUTH_TOKEN = "carpool-auth";
	private Retrofit retrofit;
	private Endpoints endpoints;
	private BraintreeFragment mBraintreeFragment;
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initEnv();

		submitButton = (Button) findViewById(R.id.btn_submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBraintreeSubmit();
			}
		});
	}

	public void initEnv() {
		HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
		logger.setLevel(HttpLoggingInterceptor.Level.BODY);

		final String userAgent = "Build:" + BuildConfig.VERSION_CODE + " API" + Build.VERSION.SDK_INT + " " + Build.FINGERPRINT;

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new Interceptor() {
					@Override
					public okhttp3.Response intercept(Chain chain) throws IOException {
						Request request = chain.request();
						Request.Builder builder = request.newBuilder();

						builder.addHeader(SERVICE_HEADER_AUTH_TOKEN, authToken);
						builder.addHeader("Accept", "*/*");
						builder.addHeader("UserProfile-Agent", userAgent);
						builder.addHeader("Accept-Language", "ZH_HK");

						request = builder.build();
						return chain.proceed(request);
					}
				})
				.addInterceptor(logger)
				.build();

		retrofit = new Retrofit.Builder()
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.client(okHttpClient)
				.baseUrl("http://192.168.0.101:8080/carpool/")
				.build();
		endpoints = retrofit.create(Endpoints.class);

		endpoints.getToken(customerId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(String s) {
						clientToken = s;
					}
				});
	}

	public void onBraintreeSubmit() {
		DropInRequest dropInRequest = new DropInRequest().clientToken(clientToken);
		startActivityForResult(dropInRequest.getIntent(this), 100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "resultCode == Activity.RESULT_OK");
				DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
				// use the result to update your UI and send the payment method nonce to your server
				checkout(result.getPaymentMethodNonce().getNonce()).subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Boolean>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onNext(Boolean aBoolean) {
								Log.d(TAG, "isSuccess: " + aBoolean);
							}
						});
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// the user canceled
				Log.d(TAG, "resultCode == Activity.RESULT_CANCELED");
			} else {
				Log.d(TAG, "resultCode == ???");
				// handle errors here, an exception may be available in
				Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
				Log.e(TAG, error.toString());
			}
		}
	}

	public Observable<String> getToken(String customerId) {
		return endpoints.getToken(customerId);
	}

	public Observable<Boolean> checkout(String nonce) {
		return endpoints.checkout(nonce);
	}
}
