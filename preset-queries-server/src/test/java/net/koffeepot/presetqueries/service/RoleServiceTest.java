package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.PresetQueriesConfiguration;
import net.koffeepot.presetqueries.common.AuthException;
import net.koffeepot.presetqueries.repository.EmptyRoleRepositoryImpl;
import net.koffeepot.presetqueries.repository.RoleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Configuration
    static class TestConfiguration extends PresetQueriesConfiguration {
        @Bean
        //Will be spied, for I cannot use mock with multiple class available
        //See https://github.com/spring-projects/spring-boot/issues/10655
        public RoleRepository roleRepository() {
            RoleRepository roleRepository = spy(EmptyRoleRepositoryImpl.class);

            doReturn(RoleService.RoleLevel.WRITE).when(roleRepository).getRole("e.gomez-vaez", "goodpwd");
            doReturn(RoleService.RoleLevel.UNKNOWN).when(roleRepository).getRole("e.gomez-vaez", "badpwd");
            doReturn(RoleService.RoleLevel.READ).when(roleRepository).getRole("y.choinowsky", "yvonnepwd");

            return roleRepository;
        }
    }

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void authorizedRWUserTest() {
        String token = roleService.getAuthToken("e.gomez-vaez", "goodpwd");
        assertThat(token).startsWith("rw-");
    }

    @Test
    public void unauthorizedUserTest() {
        assertThatThrownBy(() -> roleService.getAuthToken("e.gomez-vaez", "badpwd"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Cannot connect with login e.gomez-vaez. Check your password");
    }

    @Test
    public void authorizedROUser() {
        String token = roleService.getAuthToken("y.choinowsky", "yvonnepwd");
        assertThat(token).startsWith("ro-");
    }
}
