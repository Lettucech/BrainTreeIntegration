package com.gmail.brianbridge.braintreeintegration;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface Endpoints {
	@GET("payment/token")
	Observable<String> getClientToken();

	@GET("payment/address/list")
	Observable<List<BillingAddress>> listAddresses();

	@GET("payment/checkout")
	Observable<Boolean> checkout(
			@Query("payment_method_nonce") String nonce
	);
}
