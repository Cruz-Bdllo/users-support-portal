package com.code.supportportal.exception.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.code.supportportal.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED =
            "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED =
            "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR =
            "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS =
            "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED =
            "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE =
            "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION =
            "you do not have enough permission for access this resource";


    /* METHODS HANDLING
    --------------------------------------------------- */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> disabledException() {
        return createResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    } // end handler

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    } // end handler

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        HttpMethod supportedMethod = Objects.requireNonNull(ex.getSupportedHttpMethods().iterator().next());
        return createResponse(HttpStatus.BAD_REQUEST, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    } // end handler

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerError(Exception ex) {
        LOGGER.error(ex.getMessage());
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
    } // end handler

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException ex) {
        LOGGER.error(ex.getMessage());
        return createResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    } // end handler

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> ioException(IOException ex) {
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    } // end handler

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    } // end handler

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(){
        return createResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException ex){
        return createResponse(HttpStatus.UNAUTHORIZED, ex.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException ex){
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage().toUpperCase());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException ex){
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException ex){
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage().toUpperCase());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException ex){
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage().toUpperCase());
    }


    /* GENERATE RESPONSE
    --------------------------------------------------- */
    private ResponseEntity<HttpResponse> createResponse(HttpStatus statusCode, String message) {
        HttpResponse response = HttpResponse.HttpResponseBuilder.anResponse()
                .withHttpStatus(statusCode)
                .withStatusCode(statusCode.value())
                .withReason(statusCode.getReasonPhrase())
                .withMessage(message)
                .withTimeAt(new Date())
                .buildResponse();
        return new ResponseEntity<HttpResponse>(response, statusCode);
    }
} // end advice