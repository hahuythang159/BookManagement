package com.hutech.demo.controller;

public class ReplyForm {
    private Long supportId;
    private String replyMessage;

    // Constructors, getters, setters
    public ReplyForm() {
    }

    public ReplyForm(Long supportId, String replyMessage) {
        this.supportId = supportId;
        this.replyMessage = replyMessage;
    }

    public Long getSupportId() {
        return supportId;
    }

    public void setSupportId(Long supportId) {
        this.supportId = supportId;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }
}
