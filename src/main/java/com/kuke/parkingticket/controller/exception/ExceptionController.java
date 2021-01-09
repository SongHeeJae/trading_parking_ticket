package com.kuke.parkingticket.controller.exception;

import com.kuke.parkingticket.advice.exception.AuthenticationEntryPointException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {
    @GetMapping(value = "/entrypoint")
    public void entrypointException() {
        throw new AuthenticationEntryPointException();
    }

    @GetMapping(value = "/accessdenied")
    public void accessdeniedException() {
        throw new AccessDeniedException("");
    }

}
