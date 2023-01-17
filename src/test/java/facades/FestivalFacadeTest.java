package facades;

import dtos.FestivalDTO;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import entities.Festival;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FestivalFacadeTest {

    private static EntityManagerFactory emf;
    private static FestivalFacade facade;

    public FestivalFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = FestivalFacade.getFestivalFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Festival.deleteAllRows").executeUpdate();
            em.persist(new Festival("test festival 1", "test city 1", LocalDate.now(), "1 days"));
            em.persist(new Festival("test festival 2", "test city 2", LocalDate.now(), "7 days"));
            LocalDate date = LocalDate.of(2020, 1, 8);
            em.persist(new Festival("test festival 3", "test city 3", date, "3 days"));

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testGetAllFestivals() throws Exception {
        assertEquals(3, facade.getAllFestivals().size(), "Expects 3 rows in the database");
    }
    @Test
    public void testGetAllRelevantFestivals() throws Exception {
        assertEquals(2, facade.getAllRelevantFestivals().size(), "Expects 2 rows in the database");
    }

    @Test
    public void testCreateFestival() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 8);
        facade.createFestival("test festival 4", "test city 4", date, "4 days");

        List<FestivalDTO> festivals = facade.getAllFestivals();
        assertEquals(4, festivals.size(), "Expects 4 rows in the database");
        assertEquals("4 days", festivals.get(3).getDuration());
    }


}
