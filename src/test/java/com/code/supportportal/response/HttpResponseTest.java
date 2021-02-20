package com.code.supportportal.response;

import com.code.supportportal.response.HttpResponse.HttpResponseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    @Test
    void testCreateResponseStatusOk() {
        HttpResponse response = HttpResponseBuilder.anResponse()
                .withHttpStatus(HttpStatus.OK)
                .withStatusCode("200")
                .withReason("For get All elements")
                .withMessage("Get all elements")
                .withTimeAt(LocalDateTime.now(ZoneOffset.UTC))
                .buildResponse();

        assertNotNull(response);
        assertEquals("200", response.getStatusCode());
    } // end test

    @Test
    void testCreateResponseStatus() {
        HttpResponse response = HttpResponseBuilder.anResponse()
                .withHttpStatus(HttpStatus.OK)
                .withStatusCode("500")
                .withReason("Bad Server error")
                .withMessage("An has error occurred")
                .withTimeAt(LocalDateTime.now(ZoneOffset.UTC))
                .buildResponse();

        System.out.println(response.getTimeAt());
        assertNotNull(response);
        assertEquals("500", response.getStatusCode());
        assertEquals("Bad Server error", response.getReason());

    } // end test

} // end test