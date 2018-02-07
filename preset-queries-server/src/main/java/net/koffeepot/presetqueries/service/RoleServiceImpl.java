package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.AuthException;
import net.koffeepot.presetqueries.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {

    private static final String WRITE_TOKEN = "XobZPTuH59";
    private static final String READ_TOKEN = "pjha00ippK";

    private static final String WRITE_prefix = "rw-";
    private static final String READ_prefix = "ro-";


    @Autowired
    RoleRepository roleRepository;

    @Override
    public String getAuthToken(String login, String password) {
        switch (roleRepository.getRole(login, password)) {
            case WRITE:
                return WRITE_prefix+WRITE_TOKEN;
            case READ:
                return READ_prefix+READ_TOKEN;
            case UNKNOWN:
            default:
                throw new AuthException("Cannot connect with login "+login+". Check your password");
        }
    }

    //FROM HERE SERVICE DOES NOT NEED SPRING
    //and thus can be tested in summer test

    @Override
    public RoleLevel checkToken(String token) {
        switch (token) {
            case WRITE_prefix+WRITE_TOKEN:
                return RoleLevel.WRITE;
            case READ_prefix+READ_TOKEN:
                return RoleLevel.READ;
            default:
                throw new AuthException("Error while checking token. Please log-in again");
        }
    }

    @Override
    public void checkTokenAtLeast(String token, RoleLevel minLevel) {
        if (checkToken(token) != RoleLevel.WRITE && minLevel == RoleLevel.WRITE)
            throw new AuthException("Unauthorized. You should have R/W permissions.");
    }
}
