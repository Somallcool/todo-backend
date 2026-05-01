package com.zerock.ajaxconnectorweb.service;

import com.zerock.ajaxconnectorweb.dao.TodoDAO;
import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 할 일(Todo) 비즈니스 로직 처리 서비스
 * DAO(DB 심부름꾼)를 관리하며, 데이터 검증 및 서비스 흐름을 제어하는 매니저 역할을 수행합니다.
 * Enum을 사용한 싱글톤 패턴으로 구현되었습니다.
 */
@Log4j2
public enum TodoService {

    INSTANCE;

    private TodoDAO dao;

    TodoService() {
        dao = new TodoDAO();
    }

    /**
     * [조회] 할 일 목록 불러오기
     * @param sortType 정렬 기준 (최신순, 마감일순, 우선순위순)
     * @param keyword  검색어
     * @return 필터링 및 정렬된 할 일 목록
     */
    public List<TodoDTO> getList(String sortType, String keyword) throws SQLException {
        log.info("Service: getList 실행 중 (sort: {}, keyword: {})", sortType, keyword);
        return dao.selectAll(sortType, keyword);
    }

    /**
     * [등록] 새로운 할 일 등록 (데이터 유효성 검사 포함)
     * @param dto 등록할 할 일 정보
     * @throws Exception 제목 누락, 글자 수 초과, 과거 날짜 설정 시 예외 발생
     */
    public void register(TodoDTO dto) throws Exception {
        log.info("Service: register 실행 중... {}", dto);

        // 1. 필수 값 검증
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new Exception("제목은 필수 입력 사항입니다.");
        }

        // 2. 글자 수 제한 검증
        if (dto.getTitle().length() > 100) {
            throw new Exception("제목은 100자 이내로 입력해주세요.");
        }

        // 3. 날짜 유효성 검증 (과거 날짜 등록 불가)
        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())) {
            throw new Exception("마감 기한은 오늘 이후의 날짜여야 합니다.");
        }

        dao.insert(dto);
    }

    /**
     * [수정] 할 일 정보 전체 수정
     * @param dto 수정된 정보가 담긴 DTO
     */
    public void modify(TodoDTO dto) throws Exception {
        log.info("Service: modify 실행 중... {}", dto);
        dao.update(dto);
    }

    /**
     * [삭제] 단일 항목 삭제
     * @param tno 삭제할 할 일 번호
     */
    public void remove(Long tno) throws Exception {
        log.info("Service: remove 실행 중... tno: {}", tno);
        dao.delete(tno);
    }

    /**
     * [삭제] 선택된 항목들 일괄 삭제 (완료 항목 삭제 포함)
     * @param nos 삭제할 tno 번호들을 쉼표로 연결한 문자열 (예: "1,2,3")
     */
    public void removeSelected(String nos) throws SQLException {
        log.info("Service: removeSelected 실행 중... nos: {}", nos);
        dao.deleteSelected(nos);
    }

    /**
     * [삭제] 전체 목록 삭제 (초기화)
     */
    public void removeAll() throws SQLException {
        log.info("Service: removeAll 실행 중...");
        dao.deleteAll();
    }

    /**
     * [상태 업데이트] 할 일의 완료 여부(finished)만 변경
     * @param tno      대상 번호
     * @param finished 변경할 상태 값
     */
    public void updateFinished(Long tno, boolean finished) {
        log.info("Service: updateFinished 실행 중... tno: {}, finished: {}", tno, finished);
        dao.updateFinished(tno, finished);
    }
}