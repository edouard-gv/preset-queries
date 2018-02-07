package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
public class LDAPRoleRepositoryImpl implements RoleRepository {

    @Value("${ldap.provider-url:unset}")
    private String providerUrl;

    @Value("${ldap.domain:unset}")
    private String domain;

    @Override
    public RoleService.RoleLevel getRole(String login, String password) {
        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,  providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL,
                login+"@"+domain);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try  {
            DirContext ctx = new InitialDirContext(env);
        }
        catch (NamingException ne) {
            return RoleService.RoleLevel.UNKNOWN;
        }
        return RoleService.RoleLevel.WRITE;
    }
}
