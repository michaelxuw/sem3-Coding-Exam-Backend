package facades;

import dtos.GuestDTO;
import entities.Account;
import entities.Festival;
import entities.Guest;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuestFacade {


    private static EntityManagerFactory emf;
    private static GuestFacade instance;

    private GuestFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static GuestFacade getGuestFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuestFacade();
        }
        return instance;
    }

    public List<GuestDTO> getAllGuests() {
        List<Guest> guestList = executeWithClose(em -> {
            TypedQuery<Guest> query = em.createQuery("SELECT g FROM Guest g", Guest.class);
            return query.getResultList();
        });
        return GuestDTO.listToDTOs(guestList);
    }
    public GuestDTO getGuestByID(Integer id) {
        Guest guest = executeWithClose((em) -> {
            return em.find(Guest.class, id);
        });
        return new GuestDTO(guest);
    }

    public GuestDTO createGuest(String name, String phone, String email, String status, Integer accountID) {
        Account account = executeWithClose((em) -> {
            return em.find(Account.class, accountID);
        });

        Guest guest;
        if (account != null) {
            guest = new Guest(name, phone, email, status, account);
        } else {
            guest = new Guest(name, phone, email, status);
        }
        executeInsideTransaction(em -> {
            em.persist(guest);
            if (account != null) {
                account.setGuest(guest);
                em.merge(account);
            }
        });
        return new GuestDTO(guest);
    }


    public void updateGuest(Integer id, String name, String phone, String email, String status, Integer accountID){
        Guest guest = executeWithClose((em) -> {
            return em.find(Guest.class, id);
        });
        if(guest == null) throw new EntityNotFoundException("Could not find the Guest!");

        Account account = executeWithClose((em) -> {
            return em.find(Account.class, accountID);
        });
        if(account == null) throw new EntityNotFoundException("Could not find the Account!");

        guest.setName(name);
        guest.setPhone(phone);
        guest.setEmail(email);
        guest.setStatus(status);
        if (account != null) {
            guest.setAccount(account);
        }

        executeInsideTransaction((em) -> {
            em.merge(guest);
            if (account != null) {
                account.setGuest(guest);
                em.merge(account);
            }
        });
    }

    public void deleteGuest(Integer id) throws EntityNotFoundException{
        executeInsideTransaction(em -> {
            Guest guest = em.find(Guest.class, id);

            TypedQuery<Account> query = em.createQuery("SELECT n FROM Account n WHERE n.guest.id = :id", Account.class);
            query.setParameter("id", id);
            Account account = query.getSingleResult();

            if(account != null) {
                account.setGuest(null);
                em.merge(account);
            }
            em.remove(guest);
        });
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
