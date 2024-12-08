package com.fooddeliveryapp.UserService.services;

import com.fooddeliveryapp.UserService.models.UserResponse;

/**
 * Interface for the User service that defines the operations 
 * related to user management. This interface acts as a contract 
 * for the implementation of user-related business logic.
 */
public interface IUserService {

	UserResponse getUserProfileDetails();
}
