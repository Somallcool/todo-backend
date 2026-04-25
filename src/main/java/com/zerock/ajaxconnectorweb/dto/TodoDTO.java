package com.zerock.ajaxconnectorweb.dto;

import java.time.LocalDate;

public class TodoDTO {
    private Long tno;           // 할 일 번호
    private String title;       // 할 일 제목
    private LocalDate dueDate;  // 마감 기한
    private boolean finished;   // 완료 여부

    public TodoDTO() {}

    public TodoDTO(Long tno, String title, LocalDate dueDate, boolean finished) {
        this.tno = tno;
        this.title = title;
        this.dueDate = dueDate;
        this.finished = finished;
    }

    public Long getTno() {
        return tno;
    }
    public String getTitle() {
        return title;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public boolean isFinished() {
        return finished;
    }

    public void setTno(Long tno) {
        this.tno = tno;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "TodoDTO{" +
                "tno=" + tno +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", finished=" + finished +
                '}';
    }
}
