package com.fooddeliveryapp.UserService.services;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import com.fooddeliveryapp.UserService.models.OktaUserResponse;
import com.fooddeliveryapp.UserService.models.UserResponse;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserService userService;

    private static final String USERINFO_ENDPOINT = "https://dev-53200939.okta.com/oauth2/default/v1/userinfo";
    
    @Before
    public void setUp() {
        // setting up a mock Jwt token and authentication in the SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(jwt.getTokenValue()).thenReturn("mocked-jwt-token");
    }

    @Test
    public void test_When_Get_UserProfile_Details_Success() {
        OktaUserResponse mockOktaUserResponse = new OktaUserResponse();
        mockOktaUserResponse.setEmail("testuser@example.com");
        mockOktaUserResponse.setGiven_name("John");
        mockOktaUserResponse.setFamily_name("Doe");
        mockOktaUserResponse.setZoneinfo("Test Address");

        ResponseEntity<OktaUserResponse> mockResponse = new ResponseEntity<>(mockOktaUserResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(USERINFO_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(OktaUserResponse.class)))
            .thenReturn(mockResponse);

        UserResponse result = userService.getUserProfileDetails();

        assertNotNull(result);
        assertEquals("testuser@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Test Address", result.getAddress());

        // verify that the RestTemplate's exchange method was called once
        verify(restTemplate, times(1)).exchange(eq(USERINFO_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(OktaUserResponse.class));
    }
    
    @Test(expected = RuntimeException.class)
    public void testGetUserProfileDetails_UserNotFound() {
        // a mock response that returns null body (i.e., user not found)
        ResponseEntity<OktaUserResponse> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(eq(USERINFO_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(OktaUserResponse.class)))
            .thenReturn(mockResponse);

        userService.getUserProfileDetails();
    }

    @Test(expected = RuntimeException.class)
    public void test_When_GetUser_Profile_Details_Failure() {
        when(restTemplate.exchange(eq(USERINFO_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(OktaUserResponse.class)))
            .thenThrow(new RuntimeException("External API failure"));

        userService.getUserProfileDetails();
    }
}
