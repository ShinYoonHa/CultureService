package com.culture.CultureService.dto;

import com.culture.CultureService.entity.ShowEntity;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
public class ShowDto {
    private Long id; //db 저장 시 아이디

    private String showId; //공연 아이디
    private String title; // 공연명
    private String stDate; // 공연 시작일
    private String edDate; // 공연 종료일

    private String cast; // 출연진
    private String staff; // 제작진
    private String runtime; // 런타임
    private String age; // 관람연령
    private String producer; // 제작사 (기획제작자)
    private String ticketPrice; // 티켓 가격
    private String posterUrl; // 포스터 이미지 경로

    private String genre; // 장르
    private String state; // 공연 상태
    private String storyUrl; //소개 이미지
    private String time; //공연 시간

    private PlaceDto placeDto; //공연시설 Dto

    public ShowDto(String showId, String title, String stDate, String edDate,
                   String genre, String posterUrl) {
        this.showId = showId;
        this.title = title;
        this.stDate = stDate;
        this.edDate = edDate;
        this.genre = genre;
        this.posterUrl = posterUrl;
    }
    private static ModelMapper modelMapper = new ModelMapper();

    public static ShowDto of(ShowEntity showEntity) { //ShowEntity 정보를 ShowDto에 띄워주는 메소드
       ShowDto showDto = modelMapper.map(showEntity, ShowDto.class);
       if(showEntity.getPlace() == null) {
           return null;
       }
       showDto.setPlaceDto(PlaceDto.of(showEntity.getPlace()));

       return showDto;
    }
}
