package com.example.webapp_backend.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MessageDTO {

    private String sender;
    private String receiver;
    private String content;
    private Date sentAt;

    public MessageDTO(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }
}
