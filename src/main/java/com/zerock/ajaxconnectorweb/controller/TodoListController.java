package com.zerock.ajaxconnectorweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerock.ajaxconnectorweb.dao.TodoDAO;
import com.zerock.ajaxconnectorweb.dto.TodoDTO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "todoListController", urlPatterns = "/api/todos")
public class TodoListController extends HttpServlet {

    // 오라클에서 가져온 자바 객체(DTO)는 자바만 이해가 가능함.
    // 이걸 브라우저가 이해하는 JSON 문자열로 왔다 갔다 하게 해주는 번역기
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        // JavaTimeModul & registerModule: 자바의 날짜(LocalDate)는 기본 번역기가 해석이 힘듦.
        // 날짜 전용 번역 모듈을 등록해서 날짜도 해석할 수 있게 하는 설정
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try {
            TodoDAO dao = new TodoDAO();
            List<TodoDTO> todolist = dao.selectAll();

            // writeValueAsString: 이 자바 객체를 JSON 문자열로 써줘
            String jsonStr = objectMapper.writeValueAsString(todolist);
            resp.getWriter().print(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "서버 내부 오류 발생");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());

        String mode = req.getParameter("mode");

        if ("delete".equals(mode)) {
            Long tno = Long.parseLong(req.getParameter("tno"));
            new TodoDAO().delete(tno);
            resp.getWriter().write("{\"result\":\"deleted\"}");
            return;
        }

        try {
            TodoDTO todoDTO = objectMapper.readValue(req.getReader(), TodoDTO.class);
            System.out.println("리액트에서 온 데이터: " + todoDTO);

            new TodoDAO().insert(todoDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"result\":\"success\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
