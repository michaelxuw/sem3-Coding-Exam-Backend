package entities;

import javax.persistence.*;

@Entity
@Table(name = "Show_has_Guest")
public class ShowHasGuest {
    @EmbeddedId
    private ShowHasGuestId id;

    @MapsId("showId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_ID", nullable = false)
    private Show show;

    @MapsId("guestId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_ID", nullable = false)
    private Guest guest;

    public ShowHasGuestId getId() {
        return id;
    }

    public void setId(ShowHasGuestId id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

}