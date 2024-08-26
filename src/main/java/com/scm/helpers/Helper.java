package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {

            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oAuth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";

            if (clientId.equalsIgnoreCase("google")) {
                // sign in google account
                System.out.println("Getting email from Google");
                username = oAuth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {
                // sign in github account
                System.out.println("Getting email from Github");
                username = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email")
                        : oAuth2User.getAttribute("login").toString() + "@gmail.com";
            }
            return username;
        } else {
            // sign in local account
            System.out.println("Getting email from local account");
            return authentication.getName();
        }
    }

    public static String getLinkForEmailVerification(String emailToken) {
        return "http://localhost:8091/auth/verify-email?token=" + emailToken;
    }
}
