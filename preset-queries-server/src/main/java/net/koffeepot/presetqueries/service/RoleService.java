package net.koffeepot.presetqueries.service;

public interface RoleService {

    enum RoleLevel { WRITE, READ, UNKNOWN }

    void checkTokenAtLeast(String token, RoleLevel minLevel);

    String getAuthToken(String user, String pwd);

    RoleLevel checkToken(String token);
}
