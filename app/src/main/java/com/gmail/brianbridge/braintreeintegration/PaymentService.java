package com.gmail.brianbridge.braintreeintegration;

import android.os.Build;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;

public class PaymentService {
	private static String authToken = "RVVSd241UmZZWS9IOWJGMGR2V1RRZz09OmQ1L1N4d21WcDlEaTdxQVVpSHVPN0R5QTZEckZ4WFpORWppZzVkRUNKUUc5SnRwVU9iaVl1SzRvN3YvTm5kQzNhRkZVZTlRZDVTRVoyTEp2SHdqaUx3PT0";
	private Retrofit retrofit;
	private Endpoints endpoints;

	public PaymentService() {
		HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
		logger.setLevel(HttpLoggingInterceptor.Level.BODY);

		final String userAgent = "Build:" + BuildConfig.VERSION_CODE + " API" + Build.VERSION.SDK_INT + " " + Build.FINGERPRINT;

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new Interceptor() {
					@Override
					public okhttp3.Response intercept(Chain chain) throws IOException {
						Request request = chain.request();
						Request.Builder builder = request.newBuilder();

						builder.addHeader("carpool-auth", authToken);
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
	}

	public Observable<String> getClientToken() {
		return endpoints.getClientToken();
	}

	public Observable<List<BillingAddress>> getAddresses() {
		return endpoints.listAddresses();
	}

	public Observable<Boolean> checkout(String nonce) {
		return endpoints.checkout(nonce);
	}
}
