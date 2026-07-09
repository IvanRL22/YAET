package com.ivanrl.yaet;

import com.ivanrl.yaet.auth.UserPO;
import com.ivanrl.yaet.auth.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserData userData;
    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String name = (String) oAuth2User.getAttributes().get("name");
        String email = (String) oAuth2User.getAttributes().get("email");

        UserPO userPO = userRepository.findByEmail(email)
                                      .orElseGet(() -> storeNewUser(name, email));
        loadUserIntoSession(userPO);

        response.sendRedirect("/");
    }

    private UserPO storeNewUser(String name, String email) {
        var po = this.userRepository.save(new UserPO(name, email));
        log.info("New user signed in: {} - {}", name, email);

        return po;
    }

    private void loadUserIntoSession(UserPO userPO) {
        userData.setDbId(userPO.getId());
        userData.setName(userPO.getName());
        userData.setEmail(userPO.getEmail());
    }
}