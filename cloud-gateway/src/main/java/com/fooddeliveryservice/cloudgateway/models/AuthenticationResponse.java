package com.fooddeliveryservice.cloudgateway.models;

import java.util.Collection;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {

	private String userId; // user's ID will be the email
	private String accessToken;
	private String refreshToken;
	private long expiresAt;
	private Collection<String> authorityList;
}
