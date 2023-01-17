package dtos;

import entities.Guest;
import entities.ShowRegistration;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuestDTO {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String status;
    private Integer festivalID;
    private Integer accountID;
    private Set<Integer> showRegistrationIDs = new LinkedHashSet<>();

    public GuestDTO(Integer id, String name, String phone, String email, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }
    public GuestDTO(Guest guest) {
        this.id = guest.getId();
        this.name = guest.getName();
        this.phone = guest.getPhone();
        this.email = guest.getEmail();
        this.status = guest.getStatus();
        if(guest.getFestival() != null) {
            this.festivalID = guest.getFestival().getId();
        }
        if(guest.getAccount() != null) {
            this.accountID = guest.getAccount().getId();
        }
        if(guest.getShowRegistrations() != null) {
            this.showRegistrationIDs = guest.getShowRegistrations().stream().map(ShowRegistration::getId).collect(Collectors.toSet());
        }
    }

    public static List<GuestDTO> listToDTOs(List<Guest> guestList) {
        return guestList.stream().map(GuestDTO::new).collect(Collectors.toList());
    }


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFestivalID() {
        return festivalID;
    }
    public void setFestivalID(Integer festivalID) {
        this.festivalID = festivalID;
    }

    public Integer getAccountID() {
        return accountID;
    }
    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Set<Integer> getShowRegistrationIDs() {
        return showRegistrationIDs;
    }
    public void setShowRegistrationIDs(Set<Integer> showRegistrationIDs) {
        this.showRegistrationIDs = showRegistrationIDs;
    }
}
