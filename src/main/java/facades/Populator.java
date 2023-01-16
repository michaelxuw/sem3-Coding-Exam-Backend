/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.RenameMeDTO;
import entities.RenameMe;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author tha
 */
public class Populator {
    public static void populate(){
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        AccountFacade accountFacade = AccountFacade.getAccountFacade(emf);
        accountFacade.createAccount(false, "user", "test", "u123", "test");
        accountFacade.createAccount(true, "admin", "test", "a123", "test");

    }
    
    public static void main(String[] args) {
        populate();
    }
}
