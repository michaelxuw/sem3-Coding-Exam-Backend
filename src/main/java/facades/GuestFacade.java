package facades;

import dtos.GuestDTO;
import entities.Account;
import entities.Festival;
import entities.Guest;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
            account.setGuest(guest);
            em.merge(account);
        });
        return new GuestDTO(guest);
    }


    public void updateGuest(Integer id, String name, String city, LocalDate startDate, String duration, Integer guestID, List<Integer> guestIDs){
        List<Festival> festivals = executeWithClose((em) -> {
            TypedQuery<Festival> query = em.createQuery("SELECT n FROM Festival n WHERE n.id = :id", Festival.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
        if(festivals.isEmpty()) throw new EntityNotFoundException("Could not find the Festival!");

        Guest guest = executeWithClose((em) -> {
            return em.find(Guest.class, guestID);
        });
//        List<Guest> guest = executeWithClose((em) -> {
//            TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.id = :guestID", Guest.class);
//            query.setParameter("guestID", guestID);
//            return query.getResultList();
//        });
        List<Guest> guests = executeWithClose((em) -> {
            TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.id in :guestIDs", Guest.class);
            query.setParameter("guestIDs", guestIDs);
            return query.getResultList();
//            List<Guest> foundGuests = new LinkedList<Guest>();
//            for (Integer i : guestIDs) {
//                TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.id in :guestID", Guest.class);
//                query.setParameter("guestID", guestIDs);
//                foundGuests.add(query.getResultList().get(0));
//            }
//            return foundGuests;
        });

        Festival festival = festivals.get(0);
        festival.setName(name);
        festival.setCity(city);
        festival.setStartDate(startDate);
        festival.setDuration(duration);
        if(guest != null) {
            festival.addGuest(guest);
        }
        if(guests != null || !guests.isEmpty()) {
            festival.addGuests(guests);
        }

        executeInsideTransaction((em) -> {
            em.merge(festival);
        });
    }
    public void deleteGuest(Integer id) throws EntityNotFoundException{
        executeInsideTransaction(em -> {
            Festival festival = em.find(Festival.class, id);
            em.remove(festival);
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
