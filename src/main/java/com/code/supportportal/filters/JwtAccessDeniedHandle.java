package com.code.supportportal.filters;

import com.code.supportportal.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.code.supportportal.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Class that blocking if any user want access to any resource that not have permissions
 */
@Component
public class JwtAccessDeniedHandle implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        HttpResponse errorResponse = HttpResponse.HttpResponseBuilder.anResponse()
                .withHttpStatus(UNAUTHORIZED)
                .withStatusCode(UNAUTHORIZED.toString())
                .withReason(UNAUTHORIZED.getReasonPhrase().toUpperCase())
                .withMessage(ACCESS_DENIED_MESSAGE)
                .withTimeAt(LocalDateTime.now(ZoneOffset.UTC))
                .buildResponse();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorResponse);
        outputStream.flush();
    }
}
