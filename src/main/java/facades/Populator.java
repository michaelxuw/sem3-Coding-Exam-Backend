/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

import java.time.LocalDate;

/**
 *
 * @author tha
 */
public class Populator {
    public static void populate(){
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        AccountFacade accountFacade = AccountFacade.getAccountFacade(emf);
//        accountFacade.createAccount(false, "user", "test", "u123", "test");
//        accountFacade.createAccount(true, "admin", "test", "a123", "test");
//        accountFacade.createAccount(false, "user2", "test", "u1232", "test2");
//        accountFacade.createAccount(false, "user3", "test", "u1233", "test3");
//        accountFacade.createAccount(false, "user4", "test", "u1234", "test4");

        FestivalFacade festivalFacade = FestivalFacade.getFestivalFacade(emf);
        festivalFacade.createFestival("Spot Festival", "Aarhus", LocalDate.of(2023, 5, 5), "48 Hours (2 Days)");
        festivalFacade.createFestival("Distortion", "Koebenhavn", LocalDate.of(2023, 5, 30), "168 Hours (7 Days)");
        festivalFacade.createFestival("Spot Festival", "Aarhus", LocalDate.of(2022, 5, 5), "48 Hours (2 Days)");

        GuestFacade guestFacade = GuestFacade.getGuestFacade(emf);
//        guestFacade.createGuest("guest2", "g2", "eg2", "temp", 3);
//        guestFacade.createGuest("guest3", "g3", "eg3", "temp", 4);


    }
    
    public static void main(String[] args) {
        populate();
    }
}
