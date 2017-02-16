package com.gmail.brianbridge.braintreeintegration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCustomerRequest {
	private String firstName;
	private String lastName;
	private String address;

	public CreateCustomerRequest(String firstName, String lastName, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
	}

	@JsonProperty("first_name")
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	@JsonProperty("last_name")
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	@JsonProperty("address")
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	@Override
	public String toString() {
		return "CreateCustomerRequest{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", address='" + address + '\'' +
				'}';
	}
}
