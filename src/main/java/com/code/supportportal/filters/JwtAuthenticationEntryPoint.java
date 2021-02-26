package com.code.supportportal.filters;

import com.code.supportportal.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static com.code.supportportal.constant.SecurityConstant.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException arg2) throws IOException {
        HttpResponse errorResponse = HttpResponse.HttpResponseBuilder.anResponse()
                .withHttpStatus(FORBIDDEN)
                .withStatusCode(FORBIDDEN.value())
                .withReason(FORBIDDEN.getReasonPhrase().toUpperCase())
                .withMessage(FORBIDDEN_MESSAGE)
                .withTimeAt(new Date())
                .buildResponse();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorResponse);
        outputStream.flush();
    }
} // end class intercept
