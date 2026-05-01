package com.zerock.ajaxconnectorweb.dao;

import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import com.zerock.ajaxconnectorweb.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {

    private void setTodoParams(PreparedStatement pstmt, TodoDTO dto) throws SQLException {
        pstmt.setString(1, dto.getTitle());
        pstmt.setString(2, dto.getContent());
        pstmt.setDate(3, java.sql.Date.valueOf(dto.getDueDate()));
        pstmt.setBoolean(4, dto.isFinished());
        pstmt.setInt(5, dto.getPriority());
        pstmt.setString(6, dto.getCategory());
    }

    public List<TodoDTO> selectAll(String sortType, String keyword) throws SQLException {
        String orderBy = "order by tno desc";

        if ("dueDate".equals(sortType)) {
            orderBy = "order by dueDate ASC, tno DESC";
        } else if ("priority".equals(sortType)) {
            orderBy = "order by priority DESC, tno DESC";
        }

        StringBuilder sql = new StringBuilder("select * from tbl_todo ");

        // 검색어 존재 여부 확인
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
            // [수정 포인트 1] SQL에 '?'가 있다면 값을 채워줘야 합니다.
            if (hasKeyword) {
                pstmt.setString(1, "%" + keyword + "%");
            }

            // [수정 포인트 2] 파라미터 세팅 후에 executeQuery()를 호출합니다.
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

    // 등록
    public void insert(TodoDTO todoDTO) {
        String sql = "insert into tbl_todo(tno, title, content, dueDate, finished, priority, category) " +
                "values(todo_seq.nextval, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setTodoParams(pstmt, todoDTO);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 삭제
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

    public void update(TodoDTO dto) throws Exception {
        String sql = "update tbl_todo set title = ?, content = ?, dueDate = ?, finished = ?, " +
                "priority = ?, category = ? where tno = ?";
        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setTodoParams(pstmt, dto); 
            pstmt.setLong(7, dto.getTno()); // update에만 필요한 tno는 따로 세팅
            pstmt.executeUpdate();
        }
    }

    // 현재 상태 업데이트
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