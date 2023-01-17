package facades;

import dtos.FestivalDTO;
import entities.Festival;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
