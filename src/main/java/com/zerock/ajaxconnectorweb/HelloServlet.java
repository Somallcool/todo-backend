package com.zerock.ajaxconnectorweb;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/api/hello")
public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String jsonResponse = "{\"message\": \"서버에서 보낸 JSON 데이터\", \"status\": \"success\"}";

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();

    }
}