package com.lanxin.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyException extends RuntimeException{
    private Integer code;
    private String manage;
}
