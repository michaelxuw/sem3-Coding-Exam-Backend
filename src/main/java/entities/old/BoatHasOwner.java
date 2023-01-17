package entities.old;

import javax.persistence.*;

@Entity
@Table(name = "Boat_has_Owner")
public class BoatHasOwner {
    @EmbeddedId
    private BoatHasOwnerId id;

    @MapsId("boatId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boat_ID", nullable = false)
    private Boat boat;

    @MapsId("ownerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_ID", nullable = false)
    private Owner owner;

    public BoatHasOwnerId getId() {
        return id;
    }

    public void setId(BoatHasOwnerId id) {
        this.id = id;
    }

    public Boat getBoat() {
        return boat;
    }

    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

}