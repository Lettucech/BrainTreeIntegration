package com.gmail.brianbridge.braintreeintegration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillingAddress {
	private String id;
	private String streetAddress;

	@JsonProperty("id")
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	@JsonProperty("street_address")
	public String getStreetAddress() { return streetAddress; }
	public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

	@Override
	public String toString() {
		return "BillingAddress [id=" + id + ", streetAddress=" + streetAddress + "]";
	}
}
