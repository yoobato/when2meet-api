package kr.or.kftc.when2meet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter @Setter
public class RequestScheduleDto {

    private List<Date> dateList;
    private String member;
}
