package dtos;

import entities.Festival;
import entities.Guest;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FestivalDTO {
    private Integer id;
    private String name;
    private String city;
    private LocalDate startDate;
    private String duration;
    private Set<Integer> guestIDs = new LinkedHashSet<>();

    public FestivalDTO(Integer id, String name, String city, LocalDate startDate, String duration) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.duration = duration;
    }
    public FestivalDTO(Festival festival) {
        this.id = festival.getId();
        this.name = festival.getName();
        this.city = festival.getCity();
        this.startDate = festival.getStartDate();
        this.duration = festival.getDuration();
        this.guestIDs = festival.getGuests().stream().map(Guest::getId).collect(Collectors.toSet());
    }

    public static List<FestivalDTO> listToDTOs(List<Festival> festivalList) {
        return festivalList.stream().map(FestivalDTO::new).collect(Collectors.toList());
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

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Set<Integer> getGuests() {
        return guestIDs;
    }
    public void setGuests(Set<Integer> guestIDs) {
        this.guestIDs = guestIDs;
    }

    @Override
    public String toString() {
        return "FestivalDTO{" + "id=" + id + ", name='" + name + '\'' + ", city='" + city + '\'' + ", startDate=" + startDate + ", duration='" + duration + '\'' + ", guestIDs=" + guestIDs + '}';
    }
}
