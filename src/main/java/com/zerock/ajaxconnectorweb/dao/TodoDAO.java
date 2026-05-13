package com.zerock.ajaxconnectorweb.dao;

import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import com.zerock.ajaxconnectorweb.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 할 일(Todo) 데이터베이스 접근 객체 (DAO)
 * 오라클 DB의 tbl_todo 테이블과 통신하여 CRUD 및 일괄 처리를 수행합니다.
 */
public class TodoDAO {

    /**
     * [공통 기능] PreparedStatement 파라미터 세팅
     * 등록과 수정 시 중복되는 파라미터 설정 로직을 분리한 헬퍼 메서드입니다.
     */
    private void setTodoParams(PreparedStatement pstmt, TodoDTO dto) throws SQLException {
        pstmt.setString(1, dto.getTitle());
        pstmt.setString(2, dto.getContent());
        pstmt.setDate(3, java.sql.Date.valueOf(dto.getDueDate()));
        pstmt.setBoolean(4, dto.isFinished());
        pstmt.setInt(5, dto.getPriority());
        pstmt.setString(6, dto.getCategory());
    }

    /**
     * [조회 기능] 전체 목록 조회 (검색 및 정렬 포함)
     * @param sortType 정렬 기준 (tno, dueDate, priority)
     * @param keyword  검색어 (제목 기준)
     */
    public List<TodoDTO> selectAll(String sortType, String keyword) throws SQLException {
        // 1. 기본 정렬 설정 (최신순)
        String orderBy = "order by tno desc";

        // 2. 정렬 조건 분기
        if ("dueDate".equals(sortType)) {
            orderBy = "order by dueDate ASC, tno DESC";
        } else if ("priority".equals(sortType)) {
            orderBy = "order by priority DESC, tno DESC";
        }

        StringBuilder sql = new StringBuilder("select * from tbl_todo ");

        // 3. 검색어 존재 여부에 따른 동적 SQL 생성
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        if (hasKeyword) {
            sql.append("where title like ? ");
        }
        sql.append(orderBy);

        List<TodoDTO> list = new ArrayList<>();

        try (
                Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql.toString());
        ) {
            // 검색어가 있다면 '?' 파라미터에 값을 채움
            if (hasKeyword) {
                pstmt.setString(1, "%" + keyword + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TodoDTO dto = TodoDTO.builder()
                            .tno(rs.getLong("tno"))
                            .title(rs.getString("title"))
                            .content(rs.getString("content"))
                            .dueDate(rs.getDate("dueDate").toLocalDate())
                            .finished(rs.getBoolean("finished"))
                            .priority(rs.getInt("priority"))
                            .category(rs.getString("category"))
                            .build();
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /**
     * [등록 기능] 신규 할 일 추가
     */
    public void insert(TodoDTO todoDTO) {
        String sql = "insert into tbl_todo(title, content, dueDate, finished, priority, category) " +
                "values(?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setTodoParams(pstmt, todoDTO);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [삭제 기능] 단일 항목 삭제
     * @param tno 삭제할 할 일 번호
     */
    public void delete(Long tno) {
        String sql = "delete from tbl_todo where tno = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, tno);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [삭제 기능] 선택 삭제 및 완료 항목 일괄 삭제
     * @param nos 삭제할 tno들을 쉼표로 연결한 문자열 (예: "1,2,3")
     */
    public void deleteSelected(String nos) throws SQLException {
        // SQL IN 연산자를 사용하여 여러 개의 데이터를 한 번에 삭제
        String sql = "delete from tbl_todo where tno in (" + nos + ")";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    /**
     * [삭제 기능] 모든 데이터 삭제 (초기화)
     */
    public void deleteAll() throws SQLException {
        String sql = "delete from tbl_todo";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.executeUpdate();
        }
    }

    /**
     * [수정 기능] 전체 데이터 수정 (모달 상세 수정용)
     */
    public void update(TodoDTO dto) throws Exception {
        String sql = "update tbl_todo set title = ?, content = ?, dueDate = ?, finished = ?, " +
                "priority = ?, category = ? where tno = ?";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setTodoParams(pstmt, dto);
            pstmt.setLong(7, dto.getTno()); // WHERE 절에 사용할 tno 추가 세팅
            pstmt.executeUpdate();
        }
    }

    /**
     * [수정 기능] 완료 여부(finished) 상태 토글 업데이트
     * @param tno      업데이트할 할 일 번호
     * @param finished 변경될 완료 상태 (true/false)
     */
    public void updateFinished(Long tno, boolean finished) {
        String sql = "update tbl_todo set finished = ? where tno = ?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBoolean(1, finished);
            preparedStatement.setLong(2, tno);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}