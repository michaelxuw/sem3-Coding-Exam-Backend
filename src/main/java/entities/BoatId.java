package entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BoatId implements Serializable {
    private static final long serialVersionUID = 8988434185057464800L;
    @NotNull
    @Column(name = "boat_ID", nullable = false)
    private Integer boatId;

    @NotNull
    @Column(name = "harbour_ID", nullable = false)
    private Integer harbourId;

    public Integer getBoatId() {
        return boatId;
    }

    public void setBoatId(Integer boatId) {
        this.boatId = boatId;
    }

    public Integer getHarbourId() {
        return harbourId;
    }

    public void setHarbourId(Integer harbourId) {
        this.harbourId = harbourId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoatId entity = (BoatId) o;
        return Objects.equals(this.harbourId, entity.harbourId) && Objects.equals(this.boatId, entity.boatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(harbourId, boatId);
    }

}