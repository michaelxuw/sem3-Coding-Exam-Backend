package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NamedQuery(name = "Show.deleteAllRows", query = "DELETE from Show")
@Table(name = "`Show`")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_ID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Size(max = 45)
    @NotNull
    @Column(name = "duration", nullable = false, length = 45)
    private String duration;

    @Size(max = 45)
    @NotNull
    @Column(name = "location", nullable = false, length = 45)
    private String location;

    @NotNull
    @Column(name = "startDate", nullable = false)
    private Instant startDate;

    @Size(max = 45)
    @NotNull
    @Column(name = "startTime", nullable = false, length = 45)
    private String startTime;

    @OneToMany(mappedBy = "show")
    private Set<ShowRegistration> showRegistrations = new LinkedHashSet<>();

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Set<ShowRegistration> getShowRegistrations() {
        return showRegistrations;
    }

    public void setShowRegistrations(Set<ShowRegistration> showRegistrations) {
        this.showRegistrations = showRegistrations;
    }

}