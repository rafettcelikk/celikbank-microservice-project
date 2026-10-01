package com.rafetcelik.cards.excepiton;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CardAlreadyExistsException extends RuntimeException {
    public CardAlreadyExistsException(String s) {
        super(s);
    }
}
