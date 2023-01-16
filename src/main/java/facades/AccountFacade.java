package facades;

import entities.Account;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lam@cphbusiness.dk
 */
public class AccountFacade {

    private static EntityManagerFactory emf;
    private static AccountFacade instance;

    private AccountFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static AccountFacade getAccountFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AccountFacade();
        }
        return instance;
    }

    public Account getVeryfiedAccount(String email, String password) throws AuthenticationException {
        List<Account> response = executeWithClose(em -> {
            TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a WHERE a.email = :email", Account.class);
            query.setParameter("email", email);
            return query.getResultList();
        });
        if (response.isEmpty() || !response.get(0).verifyPassword(password)) {
            throw new AuthenticationException("Invalid email or password");
        }
        Account account = response.get(0);
        account.setPassword("");
        return account;
    }

    public Account createAccount(boolean isAdmin, String email, String userPass, String phone, String name) {

        Account account = new Account(isAdmin, email, userPass, phone, name);

        executeInsideTransaction(em -> {
            em.persist(account);
        });

        return account;
    }




    private <R> R executeWithClose(Function<EntityManager, R> action) {
        EntityManager em = emf.createEntityManager();
        R result = action.apply(em);
        em.close();
        return result;
    }
    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

}
