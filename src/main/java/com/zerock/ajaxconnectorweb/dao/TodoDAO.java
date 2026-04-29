package com.zerock.ajaxconnectorweb.dao;

import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import com.zerock.ajaxconnectorweb.util.ConnectionUtil;
import oracle.jdbc.proxy.annotation.Pre;

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

    public List<TodoDTO> selectAll() throws SQLException {
        String sql = "select * from tbl_todo order by tno desc";
        List<TodoDTO> list = new ArrayList<>();

        try (
                Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
        ) {
            while (rs.next()) {
                // Lombok의 @Builder를 쓰거나 기본 생성자 사용
                TodoDTO dto = TodoDTO.builder()
                        .tno(rs.getLong("tno"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content")) // 추가
                        .dueDate(rs.getDate("dueDate").toLocalDate())
                        .finished(rs.getBoolean("finished"))
                        .priority(rs.getInt("priority"))   // 추가
                        .category(rs.getString("category")) // 추가
                        .build();
                list.add(dto);
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