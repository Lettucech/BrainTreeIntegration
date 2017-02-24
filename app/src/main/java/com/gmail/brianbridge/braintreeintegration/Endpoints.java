package com.gmail.brianbridge.braintreeintegration;

import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface Endpoints {
	@GET("payment/token")
	Observable<String> getClientToken();

	@POST("payment/method/add")
	Observable<String> addPaymentMethod(
			@Query("nonce") String nonce
	);

	@POST("payment/checkout")
	Observable<Boolean> checkout(
			@Query("nonce") String nonce,
			@Query("amount") String amount
	);
}
