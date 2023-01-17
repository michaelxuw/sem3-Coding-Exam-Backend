package rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.Account;
import entities.Festival;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

//@Disabled
public class FestivalResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private Festival festival;
    private Festival festival2;
    private Festival festival3;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createNamedQuery("Account.deleteAllRows").executeUpdate();

            Account user = new Account(false, "user", "test", "0001userPhone", "user1");
            Account admin = new Account(true, "admin", "test", "1000adminPhone", "admin1");
            em.persist(user);
            em.persist(admin);

            em.createNamedQuery("Festival.deleteAllRows").executeUpdate();
            em.createNamedQuery("Festival.deleteAllRows").executeUpdate();
            festival = new Festival("test festival 1", "test city 1", LocalDate.now(), "1 days");
            em.persist(festival);
            festival2 = new Festival("test festival 2", "test city 2", LocalDate.now(), "7 days");
            em.persist(festival2);
            LocalDate date = LocalDate.of(2020, 1, 8);
            festival3 = new Festival("test festival 3", "test city 3", date, "3 days");
            em.persist(festival3);
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{email: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }



    @Test
    public void testGetAllForAdmin() {
        login("admin", "test");
        System.out.println("logged in");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/festival/get").then()
                .statusCode(200);
    }

    @Test
    public void testGetAllRelevantForGuest() {
        login("user", "test");
        System.out.println("logged in");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/festival/getRelevant").then()
                .statusCode(200);
    }

    @Test
    public void testCreateFestivalAsAdmin() {
        login("admin", "test");
        System.out.println("logged in");

        JsonObject json = new JsonObject();
        json.addProperty("name", "test_festival");
        json.addProperty("city", "test_city");
        json.addProperty("startDate", "2022-01-01");
        json.addProperty("duration", "5");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(json.toString())
                .when()
                .post("/festival/new").then()
                .statusCode(200);
    }

    @Test
    public void testUpdateFestivalAsAdmin() {
        login("admin", "test");
        System.out.println("logged in");

        JsonObject json = new JsonObject();
        json.addProperty("name", "test_festival 3");
        json.addProperty("city", "test_city 3");
        json.addProperty("startDate", "2022-01-01");
        json.addProperty("duration", "5 days");
        JsonArray guestIDs = new JsonArray();
        json.add("guestIDs", guestIDs);
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(json.toString())
                .when()
                .put("/festival/"+festival3.getId()).then()
                .statusCode(200);
    }

    @Test
    public void testDeleteFestivalAsAdmin() {
        login("admin", "test");
        System.out.println("logged in");
        System.out.println(festival.getId());
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .delete("/festival/"+festival.getId()).then()
                .statusCode(200);
    }


    @Test
    public void testCreateFestivalAsGuest() {
        login("user", "test");
        System.out.println("logged in");

        JsonObject json = new JsonObject();
        json.addProperty("name", "test_festival");
        json.addProperty("city", "test_city");
        json.addProperty("startDate", "2022-01-01");
        json.addProperty("duration", "5");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(json.toString())
                .when()
                .post("/festival/new").then()
                .statusCode(401);
    }

    @Test
    public void testUpdateFestivalAsGuest() {
        login("user", "test");
        System.out.println("logged in");

        JsonObject json = new JsonObject();
        json.addProperty("name", "test_festival 3");
        json.addProperty("city", "test_city 3");
        json.addProperty("startDate", "2022-01-01");
        json.addProperty("duration", "5 days");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(json.toString())
                .when()
                .put("/festival/"+3).then()
                .statusCode(401);
    }

    @Test
    public void testDeleteFestivalAsGuest() {
        login("user", "test");
        System.out.println("logged in");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .delete("/festival/"+1).then()
                .statusCode(401);
    }

}
