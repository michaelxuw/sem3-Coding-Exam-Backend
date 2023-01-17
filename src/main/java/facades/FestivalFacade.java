package facades;

import dtos.FestivalDTO;
import entities.Festival;
import entities.Guest;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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


    public void updateFestival(Integer id, String name, String city, LocalDate startDate, String duration, List<Integer> guestIDs){
        Festival festival = executeWithClose((em) -> {
            return em.find(Festival.class, id);
        });
        if(festival == null) throw new EntityNotFoundException("Could not find the Festival!");

//        System.out.println("Festival is: "+festival);

        List<Guest> guests;
        if(guestIDs.size() != 0) {
            guests = executeWithClose((em) -> {
                TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.id IN :guestIDs", Guest.class);
                query.setParameter("guestIDs", guestIDs.stream().map(integer -> integer.toString()).collect(Collectors.toList()));
                return query.getResultList();
            });
        } else {
            guests = new ArrayList<>();
        }

        festival.setName(name);
        festival.setCity(city);
        festival.setStartDate(startDate);
        festival.setDuration(duration);

//        System.out.println("Festival is: "+festival);


        executeInsideTransaction((em) -> {
            em.merge(festival);

            if (!guests.isEmpty()) {
                festival.addGuests(guests);
                em.merge(festival);
                for (Guest guest : guests) {
                    guest.setFestival(festival);
                    em.merge(guest);
                }
            }
        });
    }
    public void deleteFestival(Integer id) throws EntityNotFoundException{
        executeInsideTransaction(em -> {
            Festival festival = em.find(Festival.class, id);

            TypedQuery<Guest> query = em.createQuery("SELECT n FROM Guest n WHERE n.festival.id = :festivalID", Guest.class);
            query.setParameter("festivalID", id);
            List<Guest> guests = query.getResultList();

            if(!guests.isEmpty()) {
                for (Guest guest : guests) {
                    guest.setFestival(null);
                    em.merge(guest);
                }
            }

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
