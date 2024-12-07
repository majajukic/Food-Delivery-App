package com.example.fooddeliveryapp.OrderService.external.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class OAuthRequestInterceptor implements RequestInterceptor {

	 // Injects the OAuth2AuthorizedClientManager to handle OAuth2 client authentication
	@Autowired
	private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
	

    /**
     * This method is called when a request is about to be sent. It adds the OAuth2 Bearer token 
     * to the request headers, allowing the downstream service to authenticate using OAuth2.
     *
     * @param template The Feign RequestTemplate, used to configure the outgoing HTTP request.
     */
	@Override
    public void apply(RequestTemplate template) {
		try {
	        template.header("Authorization", "Bearer "
	                + oAuth2AuthorizedClientManager
	                        .authorize(OAuth2AuthorizeRequest
	                                .withClientRegistrationId("internal-client")
	                                .principal("internal")
	                                .build())
	                        .getAccessToken().getTokenValue());
		} catch (ClientAuthorizationException ex) {
		    System.err.println("Error authorizing client: " + ex.getMessage());
		    ex.printStackTrace();
		}
    }
}

