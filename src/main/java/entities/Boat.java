package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Boat {
    @EmbeddedId
    private BoatId id;

    @MapsId("harbourId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "harbour_ID", nullable = false)
    private Harbour harbour;

    @Size(max = 45)
    @NotNull
    @Column(name = "brand", nullable = false, length = 45)
    private String brand;

    @Size(max = 45)
    @NotNull
    @Column(name = "make", nullable = false, length = 45)
    private String make;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Size(max = 100)
    @Column(name = "image_link", length = 100)
    private String imageLink;

    @ManyToMany
    @JoinTable(name = "Boat_has_Owner", joinColumns = @JoinColumn(name = "boat_ID"), inverseJoinColumns = @JoinColumn(name = "owner_ID"))
    private Set<Owner> owners = new LinkedHashSet<>();

    public BoatId getId() {
        return id;
    }

    public void setId(BoatId id) {
        this.id = id;
    }

    public Harbour getHarbour() {
        return harbour;
    }

    public void setHarbour(Harbour harbour) {
        this.harbour = harbour;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Set<Owner> getOwners() {
        return owners;
    }

    public void setOwners(Set<Owner> owners) {
        this.owners = owners;
    }

}