package com.ruoyi.sbk.dto;

import lombok.Data;

@Data
public class Result {
    private String statusCode;
    private String message;
    private Object data;
}