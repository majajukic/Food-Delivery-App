package com.fooddeliveryapp.UserService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fooddeliveryapp.UserService.models.OktaUserResponse;
import com.fooddeliveryapp.UserService.models.UserResponse;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserService implements IUserService {
	
	@Autowired
	private RestTemplate restTemplate;

    private static final String USERINFO_ENDPOINT = "https://dev-53200939.okta.com/oauth2/default/v1/userinfo";

    /**
     * Retrieves user information from Okta based on the currently authenticated user's JWT token.
     * This method makes a GET request to the Okta /userinfo endpoint and maps the response 
     * to a UserResponse object.
     * 
     * @return A UserResponse object containing the user's email, first name, last name, and address.
     * @throws RuntimeException if there's an error fetching user info or if the response is empty.
     */
    @Override
    public UserResponse getUserProfileDetails() {
    	log.info("Retrieving profile details for the currently logged-in user...");
    	
        Jwt jwt = getJwtFromSecurityContext();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt.getTokenValue());

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<OktaUserResponse> response = restTemplate.exchange(
                USERINFO_ENDPOINT,
                HttpMethod.GET,
                request,
                OktaUserResponse.class
            );

            OktaUserResponse oktaUser = response.getBody();
            
            if (oktaUser != null) {
            	
               	log.info("Profile details of the currently logged-in user successfully retrieved.");
               	
                return UserResponse.builder()
                        .email(oktaUser.getEmail())
                        .firstName(oktaUser.getGiven_name())
                        .lastName(oktaUser.getFamily_name())
                        .address(oktaUser.getZoneinfo())
                        .build();
            } else {
            	log.error("User was not found.");
                throw new RuntimeException("User not found in the response.");
            }
        } catch (Exception ex) {
        	log.error("An error has occured while attempting to fetch user's profile info: {}", ex.getMessage());
            throw new RuntimeException("Error fetching user info", ex);
        }
    }
    
    // ========================== HELPER METHODS ==========================
    
    /**
     * Helper method to retrieve the JWT token from the Spring Security context.
     * This token is typically stored in the security context after successful authentication.
     * 
     * @return The JWT token representing the authenticated user.
     */
    private Jwt getJwtFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Jwt) authentication.getPrincipal();
    }
}
