package com.code.supportportal.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class HttpResponse {
    private HttpStatus httpStatus;
    private int statusCode;
    private String reason;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss", timezone = "America/Mexico_City")
    private Date timeStamp;

    public static class HttpResponseBuilder {
        private HttpStatus httpStatus;
        private int statusCode;
        private String reason;
        private String message;
        private Date timeStamp;

        public static HttpResponseBuilder anResponse() {
            return new HttpResponseBuilder();
        }

        public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public HttpResponseBuilder withStatusCode(int statusCode) {
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

        public HttpResponseBuilder withTimeAt(Date timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public HttpResponse buildResponse() {
            HttpResponse response = new HttpResponse();
            response.setHttpStatus(this.httpStatus);
            response.setStatusCode(this.statusCode);
            response.setReason(this.reason);
            response.setMessage(this.message);
            response.setTimeStamp(this.timeStamp);

            return response;
        }

    } // end nest class

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
} // end response class
