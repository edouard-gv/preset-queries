package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.AuthException;
import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test of RoleService that indeed does not need spring to boot
 */
public class RoleServiceSummerTest {
    RoleService roleService = new RoleServiceImpl();

    @Test
    public void authorizedROTokenTest() {
        assertThat(roleService.checkToken("ro-pjha00ippK")).isEqualTo(RoleService.RoleLevel.READ);
    }

    @Test
    public void authorizedRWTokenTest() {
        assertThat(roleService.checkToken("rw-XobZPTuH59")).isEqualTo(RoleService.RoleLevel.WRITE);
    }

    @Test
    public void unauthorizedROTokenTest() {
        assertThatThrownBy(() -> roleService.checkToken("ro-xxxxx"))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Please log-in again");
    }

    @Test
    public void unauthorizedRWTokenTest() {
        assertThatThrownBy(() -> roleService.checkToken("rw-xxxxx"))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Please log-in again");
    }

    @Test
    public void unknownTokenTest() {
        assertThatThrownBy(() -> roleService.checkToken("xxxxx"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Please log-in again");
    }

    @Test
    public void underRole() {
        assertThatThrownBy(() -> roleService.checkTokenAtLeast("ro-pjha00ippK", RoleService.RoleLevel.WRITE))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    public void overRole() {
        roleService.checkTokenAtLeast("rw-XobZPTuH59", RoleService.RoleLevel.READ);
        assertThat("Should not raise exception").isEqualTo("Should not raise exception");
    }

    @Test
    public void exactRORole() {
        roleService.checkTokenAtLeast("rw-XobZPTuH59", RoleService.RoleLevel.WRITE);
        assertThat("Should not raise exception").isEqualTo("Should not raise exception");
    }

    @Test
    public void exactRWRole() {
        roleService.checkTokenAtLeast("ro-pjha00ippK", RoleService.RoleLevel.READ);
        assertThat("Should not raise exception").isEqualTo("Should not raise exception");
    }

    @Test
    public void unknownRole() {
        assertThatThrownBy(() -> roleService.checkTokenAtLeast("rw-xxxxx", RoleService.RoleLevel.READ))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Please log-in again");
    }
}
