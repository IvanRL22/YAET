package com.ivanrl.yaet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;


@Getter
@Setter
@Component
@SessionScope
@NoArgsConstructor
public class UserData {

    private String name;
    private String email;

}
