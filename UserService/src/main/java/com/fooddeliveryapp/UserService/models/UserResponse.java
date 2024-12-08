package com.fooddeliveryapp.UserService.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a User model to which Okta user resonse is mapped to after retrieval.
 * This model holds user information such as email, first name, last name, and address.
 * It uses Lombok annotations to automatically generate getter/setter methods, a constructor with all arguments,
 * a no-argument constructor, and a builder pattern for object creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private String email;
	private String firstName;
	private String lastName;
	private String address;
}
