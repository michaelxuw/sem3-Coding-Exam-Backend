package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@NamedQuery(name = "Guest.deleteAllRows", query = "DELETE from Guest")
@Table(name = "GUEST")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_ID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Size(max = 45)
    @NotNull
    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    @Size(max = 45)
    @Column(name = "status", length = 45)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_ID")
    private Festival festival;

    @OneToOne(mappedBy = "guest")
    private Account account;

    @OneToMany(mappedBy = "guest")
    private Set<ShowRegistration> showRegistrations = new LinkedHashSet<>();


    public Guest() {
    }
    public Guest(String name, String phone, String email, String status) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }
    public Guest(String name, String phone, String email, String status, Account account) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.account = account;
    }

    public void addShowRegistration(ShowRegistration show) {
        showRegistrations.add(show);
    }
    public void addShowRegistrations(List<ShowRegistration> shows) {
        this.showRegistrations.addAll(shows);
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<ShowRegistration> getShowRegistrations() {
        return showRegistrations;
    }

    public void setShowRegistrations(Set<ShowRegistration> showRegistrations) {
        this.showRegistrations = showRegistrations;
    }

}