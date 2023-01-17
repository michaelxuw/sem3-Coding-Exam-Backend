package facades;

import dtos.GuestDTO;
import dtos.ShowDTO;
import entities.Account;
import entities.Guest;
import entities.Show;
import entities.ShowRegistration;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShowFacade {


    private static EntityManagerFactory emf;
    private static ShowFacade instance;

    private ShowFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static ShowFacade getShowFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ShowFacade();
        }
        return instance;
    }

    public List<ShowDTO> getAllShows() {
        List<Show> showList = executeWithClose(em -> {
            TypedQuery<Show> query = em.createQuery("SELECT g FROM Show g", Show.class);
            return query.getResultList();
        });
        return ShowDTO.listToDTOs(showList);
    }
    public List<ShowDTO> getAllShowsForGuestWithID(Integer id) {
        List<Show> showList = executeWithClose(em -> {
            TypedQuery<Show> query = em.createQuery("SELECT sr.show FROM ShowRegistration sr WHERE sr.guest.id = :id", Show.class);
//            TypedQuery<Show> query = em.createQuery("SELECT sr.show FROM ShowRegistration sr INNER JOIN sr.show WHERE sr.guest = :id", Show.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
        return ShowDTO.listToDTOs(showList);
    }
    public ShowDTO getShowByID(Integer id) {
        Show guest = executeWithClose((em) -> {
            return em.find(Show.class, id);
        });
        return new ShowDTO(guest);
    }

    public ShowDTO createShow(String name, String duration, String location, LocalDate startDate, String startTime) {
        Show show;
        show = new Show(name, duration, location, startDate, startTime);

        executeInsideTransaction(em -> {
            em.persist(show);
        });
        return new ShowDTO(show);
    }

    public void signUpToShow(Integer showID, Integer guestID){
        Show show = executeWithClose((em) -> {
            return em.find(Show.class, showID);
        });
        if(show == null) throw new EntityNotFoundException("Could not find the Show!");
        Guest guest = executeWithClose((em) -> {
            return em.find(Guest.class, guestID);
        });
        if(guest == null) throw new EntityNotFoundException("Could not find the Guest!");

        ShowRegistration showRegistration = new ShowRegistration(show, guest);
        show.addShowRegistration(showRegistration);
        guest.addShowRegistration(showRegistration);

        executeInsideTransaction((em) -> {
            em.persist(showRegistration);
            em.merge(show);
            em.merge(guest);
        });
    }
    public void updateShow(Integer id, String name, String duration, String location, LocalDate startDate, String startTime){
        Show show = executeWithClose((em) -> {
            return em.find(Show.class, id);
        });
        if(show == null) throw new EntityNotFoundException("Could not find the Show!");

        show.setName(name);
        show.setDuration(duration);
        show.setLocation(location);
        show.setStartDate(startDate);
        show.setStartTime(startTime);

        executeInsideTransaction((em) -> {
            em.merge(show);
        });
    }

    public void deleteShow(Integer id) throws EntityNotFoundException{
        executeInsideTransaction(em -> {
            Show show = em.find(Show.class, id);

                                // I think this should be fine, since in the database neither Shows nor Guests have
                                // info about each other or to the ShowRegistration
            Query query = em.createQuery("DELETE FROM ShowRegistration sr WHERE sr.show.id = :id");
            query.setParameter("id", id);
            query.executeUpdate();

            em.remove(show);
        });

        List<Show> showList = executeWithClose(em -> {
            TypedQuery<Show> query = em.createQuery("SELECT sr.show FROM ShowRegistration sr WHERE sr.guest.id = :id", Show.class);
            //            TypedQuery<Show> query = em.createQuery("SELECT sr.show FROM ShowRegistration sr INNER JOIN sr.show WHERE sr.guest = :id", Show.class);
            query.setParameter("id", id);
            return query.getResultList();
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
