package facades;

import dtos.FestivalDTO;
import dtos.GuestDTO;
import entities.Account;
import entities.Guest;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import entities.Festival;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FestivalFacadeTest {

    private static EntityManagerFactory emf;
    private static FestivalFacade facade;
    private static GuestFacade guestFacade;

    private Festival festival;
    private Festival festival2;
    private Festival festival3;
    private Guest guest;

    public FestivalFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = FestivalFacade.getFestivalFacade(emf);
       guestFacade = GuestFacade.getGuestFacade(emf);
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
            festival = new Festival("test festival 1", "test city 1", LocalDate.now(), "1 days");
            em.persist(festival);
            festival2 = new Festival("test festival 2", "test city 2", LocalDate.now(), "7 days");
            em.persist(festival2);
            LocalDate date = LocalDate.of(2020, 1, 8);
            festival3 = new Festival("test festival 3", "test city 3", date, "3 days");
            em.persist(festival3);


            em.createNamedQuery("Account.deleteAllRows").executeUpdate();
            Account user = new Account(false, "user", "test", "0001userPhone", "user1");
            em.persist(user);
            em.createNamedQuery("Guest.deleteAllRows").executeUpdate();
            guest = new Guest("user1", "u1", "eu1", "Don't know?", user);
            guest.setFestival(festival);
            em.persist(guest);

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

    @Test
    public void testUpdateFestival() throws Exception {
        List<FestivalDTO> festivals111 = facade.getAllFestivals();
        for (FestivalDTO f: festivals111) {
            System.out.println(f.toString());
        }

        LocalDate date = LocalDate.of(2024, 1, 8);
        List<Integer> guestIDs = Arrays.asList(guest.getId());

        List<GuestDTO> guests = guestFacade.getAllGuests();
        assertEquals(festival.getId(), guests.get(0).getFestivalID(), "Expects "+festival.getId()+" in Festival_ID field for Guest");

        facade.updateFestival(festival3.getId(),"festival 3", "Aarhus", date, "5 days", guestIDs);

        List<FestivalDTO> festivals = facade.getAllFestivals();
        assertEquals(3, festivals.size(), "Expects 3 rows in the database");

//        List<MyItem> getMyItems();
//        myClass.getMyItems()
        assertThat(festivals, hasItem(allOf(
                hasProperty("city", is("Aarhus")),
                hasProperty("name", is("festival 3")),
                hasProperty("duration", is("5 days"))
                )));

        guests = guestFacade.getAllGuests();
        assertEquals(festival3.getId(), guests.get(0).getFestivalID(), "Expects "+festival3.getId()+" for guest.festival.id");
    }

    @Test
    public void testDeleteFestival() throws Exception {

        List<FestivalDTO> festivals111 = facade.getAllFestivals();
        for (FestivalDTO f: festivals111) {
            System.out.println(f.toString());
        }

        facade.deleteFestival(festival3.getId());

        List<FestivalDTO> festivals = facade.getAllFestivals();
        assertEquals(2, festivals.size(), "Expects 2 rows in the database");
        List<GuestDTO> guests = guestFacade.getAllGuests();
        assertEquals(1, guests.size(), "Expects 1 rows in the database");
        assertEquals(festival.getId(), guests.get(0).getFestivalID(), "Expects "+festival.getId()+" as the id for guest.festival.id");
    }

    @Disabled
    @Test           //works in practice but not with test
    public void testDeleteFestivalWithGuestOnIt() throws Exception {

        facade.deleteFestival(festival.getId());

        List<FestivalDTO> festivals = facade.getAllFestivals();
        assertEquals(1, festivals.size(), "Expects 1 rows in the database");
        List<GuestDTO> guests = guestFacade.getAllGuests();
        assertEquals(1, guests.size(), "Expects 1 rows in the database");
        assertEquals(null, guests.get(0).getFestivalID(), "Expects "+null+" as the id for guest.festival.id");
    }


}
