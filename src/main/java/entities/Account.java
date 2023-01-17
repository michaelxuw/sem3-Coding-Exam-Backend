package entities;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQuery(name = "Account.deleteAllRows", query = "DELETE from Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "isAdmin", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isAdmin;

    @Size(max = 45)
    @NotNull
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 45)
    @NotNull
    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_ID")
    private Guest guest;



    public Account() {}

    //TODO Change when password is hashed
    public boolean verifyPassword(String pw){
        System.out.println("pw is: "+pw);
        System.out.println("password is: "+password);
        return BCrypt.checkpw(pw, password);
    }

    public Account(String email, String userPass) {
        this.email = email;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }
    public Account(boolean isAdmin, String email, String userPass, String phone, String name) {
        this.isAdmin = isAdmin;
        this.email = email;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
        this.phone = phone;
        this.name = name;
    }



    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }


    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }


}