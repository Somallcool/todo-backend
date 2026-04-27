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
    public List<TodoDTO> selectAll() throws SQLException {
        String sql = "select * from tbl_todo order by tno desc";
        List<TodoDTO> list = new ArrayList<>();

        try (
                Connection connection = ConnectionUtil.INSTANCE.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
        ) {

            while (rs.next()) {
                TodoDTO dto = new TodoDTO();
                dto.setTno(rs.getLong("tno"));
                dto.setTitle(rs.getString("title"));
                dto.setDueDate(rs.getDate("dueDate").toLocalDate());
                dto.setFinished(rs.getBoolean("finished"));
                list.add(dto);
            }

        }
        return list;
    }

    // 등록
    public void insert(TodoDTO todoDTO) {
        String sql = "insert into tbl_todo(tno, title, dueDate, finished) values(todo_seq.nextval,?,?,?)";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, todoDTO.getTitle());
            preparedStatement.setDate(2, java.sql.Date.valueOf(todoDTO.getDueDate()));
            preparedStatement.setBoolean(3, todoDTO.isFinished());

            preparedStatement.executeUpdate();
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

    public void update(TodoDTO dto) throws Exception{
        String sql = "update tbl_todo set title =?, dueDate = ?, finished = ? where tno =?";

        try (Connection connection = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, dto.getTitle());
            preparedStatement.setDate(2, java.sql.Date.valueOf(dto.getDueDate()));
            preparedStatement.setBoolean(3, dto.isFinished());
            preparedStatement.setLong(4, dto.getTno());

            preparedStatement.executeUpdate();
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