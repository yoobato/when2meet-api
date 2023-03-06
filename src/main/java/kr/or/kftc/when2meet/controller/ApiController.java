package kr.or.kftc.when2meet.controller;

import jakarta.transaction.Transactional;
import kr.or.kftc.when2meet.dto.RequestScheduleDto;
import kr.or.kftc.when2meet.entity.Event;
import kr.or.kftc.when2meet.entity.Schedule;
import kr.or.kftc.when2meet.repository.EventRepository;
import kr.or.kftc.when2meet.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

// TODO: 배치 돌면서 endDate가 지난 Event & Member & Schedule 삭제
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {

    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/event/{uuid}")
    public ResponseEntity<Event> getEventByUUID(@PathVariable("uuid") UUID uuid) {
        Optional<Event> event = eventRepository.findEventByUuid(uuid);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping("/event")
    public ResponseEntity<String> createEvent(@RequestBody Event event) {
        try {
            Instant nowInstant = (new Date()).toInstant().truncatedTo(ChronoUnit.DAYS);
            Instant startInstant = event.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS);
            Instant endInstant = event.getEndDate().toInstant().truncatedTo(ChronoUnit.DAYS);

            // 시작일은 오늘이거나 오늘 이후여야 한다.
            // 종료일은 시작일보다 이후여야 한다.
            if ((!startInstant.equals(nowInstant) && !startInstant.isAfter(nowInstant))
                    || !endInstant.isAfter(startInstant)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("날짜 오류");
            }

            Event newEvent = eventRepository.save(
                    new Event(
                            event.getTitle(),
                            Date.from(startInstant),
                            Date.from(endInstant),
                            event.getMembers()
                    )
            );
            return ResponseEntity.ok(newEvent.getUuid().toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/event/{uuid}")
    public ResponseEntity<Event> modifyEvent(@PathVariable("uuid") UUID uuid, @RequestBody RequestScheduleDto requestScheduleDto) {
        try {
            Optional<Event> optionalEvent = eventRepository.findEventByUuid(uuid);
            if (!optionalEvent.isPresent()) {
                // UUID 에 해당하는 Event 없음.
                return ResponseEntity.unprocessableEntity().build();
            }

            Event event = optionalEvent.get();
            if (!event.getMembers().contains(requestScheduleDto.getMember())) {
                // 유효하지 않은 Member
                return ResponseEntity.unprocessableEntity().build();
            }

            List<Schedule> newSchedules = new ArrayList<>();
            for (Date date : requestScheduleDto.getDateList()) {
                Instant currentInstant = date.toInstant().truncatedTo(ChronoUnit.DAYS);
                Instant startInstant = event.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS);
                Instant endInstant = event.getEndDate().toInstant().truncatedTo(ChronoUnit.DAYS);

                if ((currentInstant.isAfter(startInstant) && currentInstant.isBefore(endInstant))
                        || currentInstant.equals(startInstant)
                        || currentInstant.equals(endInstant)) {
                    Schedule schedule = new Schedule(requestScheduleDto.getMember(), Date.from(currentInstant));
                    schedule.setEvent(event);
                    newSchedules.add(schedule);
                } else {
                    // 유효하지 않은 날짜가 있음.
                    return ResponseEntity.unprocessableEntity().build();
                }
            }

            // 기존 일정 삭제
            scheduleRepository.deleteByEventAndMember(event, requestScheduleDto.getMember());
            // 새 일정 추가
            event.setSchedules(newSchedules);

            return ResponseEntity.ok(eventRepository.saveAndFlush(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
