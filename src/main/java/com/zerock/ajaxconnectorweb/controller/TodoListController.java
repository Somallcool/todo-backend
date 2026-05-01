package com.zerock.ajaxconnectorweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerock.ajaxconnectorweb.dao.TodoDAO;
import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import com.zerock.ajaxconnectorweb.service.TodoService;

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

        String sortType = req.getParameter("sort");
        String keyword = req.getParameter("keyword");

        try {
//            TodoDAO dao = new TodoDAO();
            List<TodoDTO> todolist = TodoService.INSTANCE.getList(sortType, keyword);

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
//        init()에서 등록해서 불필요함.
//        objectMapper.registerModule(new JavaTimeModule());

        String mode = req.getParameter("mode");
        // 삭제
        if ("delete".equals(mode)) {
            try {
                Long tno = Long.parseLong(req.getParameter("tno"));
                TodoService.INSTANCE.remove(tno);
                resp.getWriter().write("{\"result\":\"deleted\"}");
            } catch (Exception e) {
                e.printStackTrace(); // 서버 콘솔에 에러 출력
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 에러 설정
                resp.getWriter().write("{\"result\":\"fail\", \"message\":\"" + e.getMessage() + "\"}");
            }
            return;
        }

        // 수정
        if ("modify".equals(mode)) {
            try {
                TodoDTO todoDTO = objectMapper.readValue(req.getReader(), TodoDTO.class);
                TodoService.INSTANCE.modify(todoDTO);
                resp.getWriter().write("{\"result\":\"modified\"}");
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500);
                resp.getWriter().write("{\"result\":\"fail\"}");
            }
            return;
        }

        // 완료 상태 업데이트
        if ("updateFinished".equals(mode)) {
            Long tno = Long.parseLong(req.getParameter("tno"));
            boolean finished = Boolean.parseBoolean(req.getParameter("finished"));
            TodoService.INSTANCE.updateFinished(tno, finished);
            resp.getWriter().write("{\"result\":\"updated\"}");
            return;
        }

        // 등록 로직 (기본)
        try {
            TodoDTO todoDTO = objectMapper.readValue(req.getReader(), TodoDTO.class);
            System.out.println("리액트에서 온 데이터: " + todoDTO);

            TodoService.INSTANCE.register(todoDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"result\":\"success\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
