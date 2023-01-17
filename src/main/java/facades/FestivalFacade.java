package facades;

import dtos.FestivalDTO;
import entities.Festival;
import entities.Guest;
import security.errorhandling.AuthenticationException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FestivalFacade {


    private static EntityManagerFactory emf;
    private static FestivalFacade instance;

    private FestivalFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static FestivalFacade getFestivalFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FestivalFacade();
        }
        return instance;
    }

    public List<FestivalDTO> getAllFestivals() {
        List<Festival> festivalList = executeWithClose(em -> {
            TypedQuery<Festival> query = em.createQuery("SELECT f FROM Festival f", Festival.class);
            return query.getResultList();
        });
        return FestivalDTO.listToDTOs(festivalList);
    }
    public List<FestivalDTO> getAllRelevantFestivals() {
        List<Festival> festivalList = executeWithClose(em -> {
            TypedQuery<Festival> query = em.createQuery("SELECT f FROM Festival f WHERE f.startDate >= :today", Festival.class);
            query.setParameter("today", LocalDate.now());
            return query.getResultList();
        });
        return FestivalDTO.listToDTOs(festivalList);
    }

    public FestivalDTO createFestival(String name, String city, LocalDate startDate, String duration) {

        Festival festival = new Festival(name, city, startDate, duration);

        executeInsideTransaction(em -> {
            em.persist(festival);
        });

        return new FestivalDTO(festival);
    }


    public void updateFestival(Integer id, String name, String city, LocalDate startDate, String duration, Integer guestID, List<Integer> guestIDs){
        List<Festival> festivals = executeWithClose((em) -> {
            TypedQuery<Festival> query = em.createQuery("SELECT n FROM Festival n WHERE n.id = :id", Festival.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
        if(festivals.isEmpty()) throw new EntityNotFoundException("Could not find the Festival!");
        List<Guest> guest = executeWithClose((em) -> {
            TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.id = :guestID", Guest.class);
            query.setParameter("guestID", guestID);
            return query.getResultList();
        });
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
        if(guest != null || !guest.isEmpty()) {
            festival.addGuest(guest.get(0));
        }
        if(guests != null || !guests.isEmpty()) {
            festival.addGuests(guests);
        }

        executeInsideTransaction((em) -> {
            em.merge(festival);
        });
    }
    public void deleteFestival(Integer id) throws EntityNotFoundException{
        List<Festival> festivals = executeWithClose((em) -> {
            TypedQuery<Festival> query = em.createQuery("SELECT n FROM Festival n WHERE n.id = :id", Festival.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
        if(festivals.isEmpty()) throw new EntityNotFoundException("Could not find the notification!");

        executeInsideTransaction(em -> {
            Festival festival = em.find(Festival.class, festivals.get(0));
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
