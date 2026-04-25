package com.zerock.ajaxconnectorweb.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class CORSFilter implements Filter {

    // FilterChain & doFilter: 서블릿으로 가기 전에 거치는 검문소 통로.
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;

        // Header: 편지봉투에 적힌 부가 정보? 누가 보냈나 형식이 뭔가 읽어도 되나(CORS) 등 적는 곳
        // resp.setHeader: 편지봉투(응답)에 부가 정보를 적어 놓겠다 -> 리액트씨, 필터가 허락해줄게요?

        // 1. 들어오는 요청을 UTF-8로 해석하겠다! (리액트가 보낸 한글 안 깨지게)
        request.setCharacterEncoding("UTF-8");

        // 2. 나가는 응답도 UTF-8로 보내겠다! (브라우저에서 한글 안 깨지게)
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // 3. React 서버(5173)에서 오는 요청을 허용하겠다 선언
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        // 4. 어떤 방식의 요청을 허용할 것인가?
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // 5. 요청 시 어떤 헤더 정보를 허용할 것인가?
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 6. 쿠키 등 인증 정보를 포함할 것인가?
        resp.setHeader("Access-Control-Allow-Credentials", "true");

        // 검사가 끝났으니 다음 필터나 서블릿으로 요청을 넘겨준다
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
