package entities.old;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BoatHasOwnerId implements Serializable {
    private static final long serialVersionUID = 2452397365331148640L;
    @NotNull
    @Column(name = "boat_ID", nullable = false)
    private Integer boatId;

    @NotNull
    @Column(name = "owner_ID", nullable = false)
    private Integer ownerId;

    public Integer getBoatId() {
        return boatId;
    }

    public void setBoatId(Integer boatId) {
        this.boatId = boatId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoatHasOwnerId entity = (BoatHasOwnerId) o;
        return Objects.equals(this.ownerId, entity.ownerId) && Objects.equals(this.boatId, entity.boatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, boatId);
    }

}