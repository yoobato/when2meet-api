package kr.or.kftc.when2meet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
@Entity(name = "event")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private long id;

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", columnDefinition = "VARCHAR(255)", updatable = false, nullable = false)
    private UUID uuid;

    @NotEmpty(message = "title 은 반드시 입력해야 합니다.")
    @Column(name = "title")
    private String title;

    // TODO: ms로 변경하기?
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ElementCollection
    @NotEmpty(message = "members 는 1명 이상 입력해야 합니다.")
    @Column(name = "members")
    private List<String> members;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Column(name = "schedules")
    private List<Schedule> schedules;

    public Event(String title, Date startDate, Date endDate, List<String> members) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.members = members;
    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
