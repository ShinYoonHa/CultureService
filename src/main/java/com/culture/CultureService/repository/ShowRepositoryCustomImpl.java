package com.culture.CultureService.repository;

import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.QShowEntity;
import com.culture.CultureService.entity.ShowEntity;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class ShowRepositoryCustomImpl implements ShowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public ShowRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 검색 조건에 따른 BooleanExpression 메서드들
    // html-searchDto가 바인딩됨.
    // html select태그 내 th:field="*{필드명}" 의 필드명 = searchDto의 변수 이름,
    // 메소드들의 매개변수명 = searchDto 변수 이름

    private BooleanExpression searchGenreEq(String searchGenre) {
        if(StringUtils.isEmpty(searchGenre)) {
            return null;
        }
        switch (searchGenre) {
            case "theater":
                return QShowEntity.showEntity.genre.eq("연극");
            case "musical":
                return QShowEntity.showEntity.genre.eq("뮤지컬");
            case "classic":
                return QShowEntity.showEntity.genre.eq("서양음악(클래식)");
            case "kmusic":
                return QShowEntity.showEntity.genre.eq("한국음악(국악)");
            case "popularmusic":
                return QShowEntity.showEntity.genre.eq("대중음악");
            case "dance":
                return QShowEntity.showEntity.genre.eq("무용");
            case "populardance":
                return QShowEntity.showEntity.genre.eq("대중무용");
            case "circus/magic":
                return QShowEntity.showEntity.genre.eq("서커스/마술");
            case "complex":
                return QShowEntity.showEntity.genre.eq("복합");
            case "kid":
                return QShowEntity.showEntity.genre.eq("아동");
            default:
                return null;

        }
    }

    private BooleanExpression searchTicketPriceEq(String searchFee) {
        if(StringUtils.isEmpty(searchFee)) {
            return null;
        }
        if(searchFee.equals("free")) {
            //Entity 변수 중 티켓 가격이 "전석무료" 인 Entity 반환
            return QShowEntity.showEntity.ticketPrice.eq("전석무료 ");
        }  else {
            return QShowEntity.showEntity.ticketPrice.ne("전석무료 ");
        }
    }

    private BooleanExpression withinDateRange(String searchDateType) {
        if (searchDateType == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar now = Calendar.getInstance();
        Date startDate = now.getTime();
        Date endDate = now.getTime();

        try {
            switch (searchDateType) {
                case "1d":
                    now.add(Calendar.DAY_OF_MONTH, 1);
                    endDate = now.getTime();
                    break;
                case "1w":
                    now.add(Calendar.WEEK_OF_YEAR, 1);
                    endDate = now.getTime();
                    break;
                case "1m":
                    now.add(Calendar.MONTH, 1);
                    endDate = now.getTime();
                    break;
                case "6m":
                    now.add(Calendar.MONTH, 6);
                    endDate = now.getTime();
                    break;
                default:
                    return null;
            }

            String formattedEndDate = dateFormat.format(endDate);
            String formattedStartDate = dateFormat.format(startDate);
            return QShowEntity.showEntity.stDate.loe(formattedEndDate).and(QShowEntity.showEntity.edDate.goe(formattedStartDate));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchBy) || StringUtils.isEmpty(searchQuery)) {
            return null;
        }

        if ("title".equals(searchBy)) {
            return QShowEntity.showEntity.title.containsIgnoreCase(searchQuery);
        } else if ("placeName".equals(searchBy)) {
            return QShowEntity.showEntity.place.name.containsIgnoreCase(searchQuery);
        }

        return null;
    }

    @Override
    public Page<ShowEntity> getShowListPage(ShowSearchDto showSearchDto, Pageable pageable) {
        QueryResults<ShowEntity> results = queryFactory
                .selectFrom(QShowEntity.showEntity)
                .where(
                        searchGenreEq(showSearchDto.getSearchGenre()),
                        searchTicketPriceEq(showSearchDto.getSearchFee()),
                        withinDateRange(showSearchDto.getSearchPeriod()),
                        searchByLike(showSearchDto.getSearchBy(), showSearchDto.getSearchQuery())
                )
                .orderBy(QShowEntity.showEntity.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        //메소드들에서 null 반환하면 에러 아니냐?! QueryDSL은 결과값이 null 이면 그냥 Pass 해버림
        //fetchResults - 쿼리 결과 List와 개수를 반환
        List<ShowEntity> shows = results.getResults(); //쿼리 결과 리스트 반환
        long total = results.getTotal(); //전체 개수 반환

        return new PageImpl<>(shows, pageable, total);
    }
}
