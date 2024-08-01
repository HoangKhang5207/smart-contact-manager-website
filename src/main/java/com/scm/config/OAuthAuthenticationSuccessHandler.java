package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler");

        // identify the provider
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

        logger.info(authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + " => " + value);
        });

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("newbie");

        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            // google
            // google attributes
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getName());
            user.setAbout("This account is created by Google");
    
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            // github
            // github attributes
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email")
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            String providerUserId = oauthUser.getName();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProvider(Providers.GITHUB);
            user.setProviderUserId(providerUserId);
            user.setAbout("This account is created by GitHub");

        } else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
            // linkedin
            // linkedin attributes

        } else {
            logger.info("OAuthAuthenticationSuccessHandler: Unknown provider");
        }

        User user2 = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (user2 == null) {
            userRepository.save(user);
            logger.info("User saved successfully.");
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}
