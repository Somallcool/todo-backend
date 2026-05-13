package com.zerock.ajaxconnectorweb.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS(Cross-Origin Resource Sharing) 및 인코딩 설정 필터
 * 브라우저의 보안 정책 때문에 발생하는 '교차 출처 리소스 공유' 문제를 해결하고,
 * 데이터 전송 시 한글 깨짐을 방지하는 역할을 합니다.
 */
@WebFilter(urlPatterns = "/*") // 모든 경로(/*)로 들어오는 요청에 대해 이 필터를 거치게 함
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * 필터의 핵심 로직: 요청이 서블릿에 도달하기 전/후에 실행됩니다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;

        // --- [1. 데이터 인코딩 설정: "말소리 정하기"] ---

        // 리액트에서 보낸 한글 데이터가 서버에서 깨지지 않도록 UTF-8로 해석합니다.
        request.setCharacterEncoding("UTF-8");

        // 서버에서 나가는 응답 데이터가 브라우저에서 깨지지 않도록 UTF-8로 설정합니다.
        response.setCharacterEncoding("UTF-8");

        // 이 응답은 JSON 형태이며 한글(UTF-8)을 포함하고 있음을 브라우저에 알립니다.
        response.setContentType("application/json; charset=UTF-8");


        // --- [2. CORS 헤더 설정: "통행 허가증 작성"] ---
        // 브라우저는 보안상 주소(Origin)가 다르면 응답을 차단합니다. 이를 허용해주는 설정입니다.

        // [Allow-Origin] "누구에게 허용할 것인가?"
        // 리액트 개발 서버(http://localhost:5173)에서 오는 요청만 내 데이터를 가져가게 허락합니다.
//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // [Allow-Methods] "어떤 동작을 허용할 것인가?"
        // 데이터 조회(GET), 등록(POST), 수정(PUT), 삭제(DELETE) 등 모든 방식을 허용합니다.
        // OPTIONS는 브라우저가 실제 요청 전 통신이 가능한지 미리 확인하는 '예비 요청'입니다.
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // [Allow-Headers] "어떤 편지 내용을 허용할 것인가?"
        // JSON 데이터를 주고받기 위한 'Content-Type'이나 로그인 인증을 위한 'Authorization' 헤더를 허용합니다.
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // [Allow-Credentials] "쿠키나 인증 정보를 주고받을 것인가?"
        // 나중에 로그인을 구현하여 세션이나 쿠키를 사용할 때 이 설정이 'true'여야 정보가 전달됩니다.
//        resp.setHeader("Access-Control-Allow-Credentials", "true");


        // --- [3. 다음 단계로 진행: "검문 통과"] ---

        // 설정을 마쳤으니 다음 필터나 실제 목적지인 서블릿(Controller)으로 요청을 넘겨줍니다.
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}