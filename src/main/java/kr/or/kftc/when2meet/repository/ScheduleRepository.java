package kr.or.kftc.when2meet.repository;

import kr.or.kftc.when2meet.entity.Event;
import kr.or.kftc.when2meet.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Modifying
    void deleteByEventAndMember(Event event, String member);
}
