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
}