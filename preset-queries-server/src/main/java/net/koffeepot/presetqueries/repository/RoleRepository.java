package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.service.RoleService;

public interface RoleRepository {
    RoleService.RoleLevel getRole(String user, String password);
}
