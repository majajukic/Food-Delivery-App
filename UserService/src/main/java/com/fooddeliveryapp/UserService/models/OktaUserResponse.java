package com.fooddeliveryapp.UserService.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a response from the Okta /userinfo endpoint containing user details.
 * This model holds user information such as email, first name, last name, and zone info.
 * It uses Lombok annotations to automatically generate getter/setter methods, a constructor with all arguments,
 * a no-argument constructor, and a builder pattern for object creation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OktaUserResponse {
	private String email;
    private String given_name;
    private String family_name;
    private String zoneinfo;
}