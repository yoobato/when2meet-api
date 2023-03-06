package kr.or.kftc.when2meet.repository;

import kr.or.kftc.when2meet.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByUuid(UUID uuid);
}
