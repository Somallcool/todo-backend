package com.zerock.ajaxconnectorweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {
    private Long tno;

    private String title;

    private String content;   // 할 일 상세 내용 (새로 추가)

    private LocalDate dueDate;

    private boolean finished;

    private int priority;     // 우선순위: 1(낮음), 2(보통), 3(높음) (새로 추가)

    private String category;  // 카테고리: 업무, 운동, 공부 등 (새로 추가)
}
