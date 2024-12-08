package com.fooddeliveryapp.UserService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddeliveryapp.UserService.models.UserResponse;
import com.fooddeliveryapp.UserService.services.IUserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
    private IUserService userService;

	/**
     * Retrieves currently logged in user's profile details from Okta
     * Both Admins and Customers can view their own profile info
     *
     * @param authentication The Authentication object which holds the details of the authenticated user.
     * @return A UserResponse containing user details.
     */
	@PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/my-profile")
    public ResponseEntity<UserResponse> getUserProfileInfo() {
        UserResponse user = userService.getUserProfileDetails();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}