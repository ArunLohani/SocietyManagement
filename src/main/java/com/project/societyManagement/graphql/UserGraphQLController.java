package com.project.societyManagement.graphql;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class UserGraphQLController {

    private final UserService userService;

    @QueryMapping
    public User userById(@Argument Long id){
        return userService.findUserById(id);
    }

}
