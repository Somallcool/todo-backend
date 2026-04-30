package com.zerock.ajaxconnectorweb.service;

import com.zerock.ajaxconnectorweb.dao.TodoDAO;
import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Log4j2
public enum TodoService {
    // DAO는 DB 심부름꾼이고, Service는 전체 상황을 관리하는 매니저

    INSTANCE;

    private TodoDAO dao;

    TodoService() {
        dao = new TodoDAO();
    }

    // 목록 불러오기
    public List<TodoDTO> getList(String sortType) throws SQLException {
        log.info("Service: getList 실행 중...");
        return dao.selectAll(sortType);
    }

    // 등록 (유효성 검사 로직)
    public void register(TodoDTO dto) throws Exception {
        log.info("Service: register 샐행 중...{}", dto);

        // 검사 로직
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new Exception("제목은 필수 입력 사항입니다.");
        }

        if (dto.getTitle().length() > 100) {
            throw new Exception("제목은 100자 이내로 입력해주세요.");
        }

        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())) {
            throw new Exception("마감 기한은 오늘 이후의 날짜여야 합니다.");
        }

        dao.insert(dto);
    }

    // 수정
    public void modify(TodoDTO dto) throws Exception {
        log.info("Service: modify 실행 중... {}", dto);
        dao.update(dto);
    }
    // 삭제
    public void remove(Long tno) throws Exception{
        log.info("Service: remove 샐행 중... tno{}", tno);
        dao.delete(tno);
    }

    // 완료 상태 업데이트
    public void updateFinished(Long tno, boolean finished) {
        log.info("Service: updateFinished 샐행 중...");
        dao.updateFinished(tno, finished);
    }


}
