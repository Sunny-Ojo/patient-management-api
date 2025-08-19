package com.sunshinecoder.common.dto.response;

public class ApiResponse<T> {

    private String message;
    private int status;
    private T data;

    public ApiResponse() {}

    public ApiResponse(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    // -----------------
    // Success helpers
    // -----------------
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("Success", 200, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, 200, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(message, 201, data);
    }

    // -----------------
    // Error helpers
    // -----------------
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(message, 400, null);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(message, 401, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(message, 404, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, 400, null);
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(message, 400, null);
    }

    // -----------------
    // Getters and setters
    // -----------------
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
