package com.zerock.ajaxconnectorweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zerock.ajaxconnectorweb.dto.TodoDTO;
import com.zerock.ajaxconnectorweb.service.TodoService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 할 일 관리 API 컨트롤러 (Servlet)
 * 브라우저(React)의 요청을 받아 JSON 형태로 응답하며,
 * CRUD 및 일괄 삭제 기능을 처리하는 관문 역할을 수행합니다.
 */
@WebServlet(name = "todoListController", urlPatterns = "/api/todos")
public class TodoListController extends HttpServlet {

    /**
     * ObjectMapper: Java 객체(DTO)와 JSON 문자열 간의 상호 변환을 담당하는 번역기
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        /**
         * JavaTimeModule: 자바의 LocalDate 등 날짜 라이브러리를 JSON이 인식할 수 있도록
         * 전용 번역 모듈을 등록하는 초기화 설정입니다.
         */
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * [GET] 데이터 조회 요청 처리
     * 리액트의 검색어(keyword)와 정렬 기준(sort)에 맞춰 목록을 반환합니다.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String sortType = req.getParameter("sort");
        String keyword = req.getParameter("keyword");

        try {
            // 서비스 계층을 통해 데이터 리스트 확보
            List<TodoDTO> todolist = TodoService.INSTANCE.getList(sortType, keyword);

            // 자바 리스트 객체를 JSON 문자열로 변환하여 응답 본문에 작성
            String jsonStr = objectMapper.writeValueAsString(todolist);
            resp.getWriter().print(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "조회 중 서버 내부 오류 발생");
        }
    }

    /**
     * [POST] 데이터 변경 요청 처리 (등록, 수정, 삭제)
     * 파라미터 'mode' 값에 따라 각기 다른 비즈니스 로직을 실행합니다.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // mode 파라미터를 통해 어떤 행위(등록/수정/삭제 등)를 할지 결정
        String mode = req.getParameter("mode");

        try {
            // 1. 단일 삭제 모드
            if ("delete".equals(mode)) {
                try {
                    Long tno = Long.parseLong(req.getParameter("tno"));
                    TodoService.INSTANCE.remove(tno);
                    resp.getWriter().write("{\"result\":\"deleted\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"result\":\"fail\", \"message\":\"" + e.getMessage() + "\"}");
                }
                return;
            }

            // 2. 선택 항목 및 완료 항목 일괄 삭제 모드
            if ("deleteSelected".equals(mode)) {
                String nos = req.getParameter("nos"); // 쉼표로 구분된 tno 문자열
                if (nos != null && !nos.isEmpty()) {
                    TodoService.INSTANCE.removeSelected(nos);
                }
                resp.getWriter().write("{\"result\":\"success\"}");
                return;
            }

            // 3. 전체 삭제 모드
            if ("deleteAll".equals(mode)) {
                TodoService.INSTANCE.removeAll();
                resp.getWriter().write("{\"result\":\"success\"}");
                return;
            }

            // 4. 상세 내용 수정 모드 (JSON 데이터 수신)
            if ("modify".equals(mode)) {
                try {
                    // 요청 본문(Reader)의 JSON을 자바 객체(DTO)로 변환
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

            // 5. 완료 체크 상태 토글 모드
            if ("updateFinished".equals(mode)) {
                Long tno = Long.parseLong(req.getParameter("tno"));
                boolean finished = Boolean.parseBoolean(req.getParameter("finished"));
                TodoService.INSTANCE.updateFinished(tno, finished);
                resp.getWriter().write("{\"result\":\"updated\"}");
                return;
            }

            // 6. [기본 모드] 신규 등록 처리
            try {
                // 리액트에서 보낸 JSON 데이터를 TodoDTO 객체로 변환
                TodoDTO todoDTO = objectMapper.readValue(req.getReader(), TodoDTO.class);
                TodoService.INSTANCE.register(todoDTO);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"result\":\"success\"}");
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}