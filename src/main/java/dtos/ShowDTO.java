package dtos;

import entities.Guest;
import entities.Show;
import entities.ShowRegistration;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShowDTO {
    private Integer id;
    private String name;
    private String duration;
    private String location;
    private LocalDate startDate;
    private String startTime;
    private Set<Integer> showRegistrationIDs = new LinkedHashSet<>();


    public ShowDTO(Integer id, String name, String duration, String location, LocalDate startDate, String startTime) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
    }
    public ShowDTO(Show show) {
        this.id = show.getId();
        this.name = show.getName();
        this.duration = show.getDuration();
        this.location = show.getLocation();
        this.startDate = show.getStartDate();
        this.startTime = show.getStartTime();
        if(show.getShowRegistrations() != null) {
            this.showRegistrationIDs = show.getShowRegistrations().stream().map(ShowRegistration::getId).collect(Collectors.toSet());
        }
    }

    public static List<ShowDTO> listToDTOs(List<Show> showList) {
        return showList.stream().map(ShowDTO::new).collect(Collectors.toList());
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

    public Set<Integer> getShowRegistrationIDs() {
        return showRegistrationIDs;
    }
    public void setShowRegistrationIDs(Set<Integer> showRegistrationIDs) {
        this.showRegistrationIDs = showRegistrationIDs;
    }
}
