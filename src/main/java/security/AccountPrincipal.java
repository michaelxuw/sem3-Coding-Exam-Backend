package security;

import entities.Account;

import java.security.Principal;

public class AccountPrincipal implements Principal {

    private String email;

    private String name;
    private Permission permission;

    /* Create a AccountPrincipal, given the Entity class User*/
    public AccountPrincipal(Account accont, Permission permission) {
        this.email = accont.getEmail();
        this.permission = permission;
    }

    public AccountPrincipal(String email, String name, Permission permission) {
        super();
        this.email = email;
        this.name = name;
        this.permission = permission;

    }

    @Override
    public String getName() {
        return email;
    }

    public boolean hasPermission(Permission permission) {
        boolean isAllowed = permission == this.permission;
        if (permission == Permission.USER && this.permission == Permission.ADMIN) isAllowed = true;

        return isAllowed;
    }
}