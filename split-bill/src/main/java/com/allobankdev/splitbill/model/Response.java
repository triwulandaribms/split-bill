package com.allobankdev.splitbill.model;


public record Response<T>(
            String code, 
            String message, 
            T data
){

    public static Response<Object> create(
            String serviceCode, 
            String responseCode, 
            String message, 
            Object data
    ) {

        return new Response<>(
            serviceCode + responseCode, 
            message, 
            data
        );
    }
    
    public static  final String RESPONSE_CODE_SUCCESS = "00";
    public static  final String RESPONSE_CODE_NOT_FOUND = "01";
    public static  final String RESPONSE_CODE_INVALID_INPUT = "02";

}