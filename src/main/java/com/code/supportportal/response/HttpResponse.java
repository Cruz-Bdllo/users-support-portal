package com.code.supportportal.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class HttpResponse {
    private HttpStatus httpStatus;
    private String statusCode;
    private String reason;
    private String message;
    private LocalDateTime timeAt;

    public static class HttpResponseBuilder {
        private HttpStatus httpStatus;
        private String statusCode;
        private String reason;
        private String message;
        private LocalDateTime timeAt;

        public static HttpResponseBuilder anResponse() {
            return new HttpResponseBuilder();
        }

        public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public HttpResponseBuilder withStatusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        public HttpResponseBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public HttpResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public HttpResponseBuilder withTimeAt(LocalDateTime timeAt) {
            this.timeAt = timeAt;
            return this;
        }

        public HttpResponse buildResponse() {
            HttpResponse response = new HttpResponse();
            response.setHttpStatus(this.httpStatus);
            response.setStatusCode(this.statusCode);
            response.setReason(this.reason);
            response.setMessage(this.message);
            response.setTimeAt(this.timeAt);

            return response;
        }

    } // end nest class

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeAt() {
        return timeAt;
    }

    public void setTimeAt(LocalDateTime timeAt) {
        this.timeAt = timeAt;
    }
} // end response class
