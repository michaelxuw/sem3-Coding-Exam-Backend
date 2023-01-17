package entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ShowHasGuestId implements Serializable {
    private static final long serialVersionUID = -219952875406353474L;
    @NotNull
    @Column(name = "show_ID", nullable = false)
    private Integer showId;

    @NotNull
    @Column(name = "guest_ID", nullable = false)
    private Integer guestId;

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowHasGuestId entity = (ShowHasGuestId) o;
        return Objects.equals(this.showId, entity.showId) && Objects.equals(this.guestId, entity.guestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showId, guestId);
    }

}