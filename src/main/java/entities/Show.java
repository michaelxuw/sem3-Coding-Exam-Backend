package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
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
    private LocalDate startDate;

    @Size(max = 45)
    @NotNull
    @Column(name = "startTime", nullable = false, length = 45)
    private String startTime;

    @OneToMany(mappedBy = "show")
    private Set<ShowRegistration> showRegistrations = new LinkedHashSet<>();



    public Show() {
    }
    public Show(Integer id, String name, String duration, String location, LocalDate startDate, String startTime) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
    }
    public Show(String name, String duration, String location, LocalDate startDate, String startTime) {
        this.name = name;
        this.duration = duration;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
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