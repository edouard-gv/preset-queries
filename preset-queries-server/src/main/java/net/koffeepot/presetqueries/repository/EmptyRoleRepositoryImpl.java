package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.service.RoleService;
import org.springframework.stereotype.Component;

@Component
public class EmptyRoleRepositoryImpl implements RoleRepository {

    @Override
    public RoleService.RoleLevel getRole(String login, String password) {
        return RoleService.RoleLevel.WRITE;
    }
}
