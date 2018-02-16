package net.koffeepot.presetqueries.controller;

import net.koffeepot.presetqueries.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoleController {

    public static class UserLogin {

        public UserLogin() {
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        String login;
        String password;
    }

    public static class TokenWrapper {
        public TokenWrapper(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        String token;

    }

    @Autowired
    private RoleService roleService;

    //TODO integrate in a integration test?
    @RequestMapping(path = "/api/login", method= RequestMethod.POST)
    public @ResponseBody
    TokenWrapper login(@RequestBody UserLogin body) {
        return new TokenWrapper(roleService.getAuthToken(body.getLogin(), body.getPassword()));
    }

}
