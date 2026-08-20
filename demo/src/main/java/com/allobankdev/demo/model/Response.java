package com.allobankdev.demo.model;


public record Response<T>(String code, String message, T data) {

    public static Response<Object> create(String serviceCode, String responseCode, String message, Object data) {
        return new Response<>(serviceCode + responseCode, message, data);
    }

    public static Response<Object> unauthenticated() {
        return new Response<>("0101", "unauthenticated", null);
    }

    public static Response<Object> unauthorized() {
        return new Response<>("0201", "unauthorized", null);
    }


    public static Response<Object> badRequest() {
        return new Response<>("0301", "bad request", null);
    }
    
    public static  final String RESPONSE_CODE_SUCCESS = "00";
    public static  final String RESPONSE_CODE_NOT_FOUND = "01";
    public static  final String RESPONSE_CODE_INVALID_INPUT = "02";
    public static  final String RESPONSE_CODE_NO_CONTENT = "03";
    public static final String RESPONSE_CODE_ALREADY_EXISTS = null; 

}