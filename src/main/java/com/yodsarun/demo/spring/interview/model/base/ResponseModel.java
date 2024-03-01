package com.yodsarun.demo.spring.interview.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Locale;

@Data
@ControllerAdvice
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ResponseModel<T> {

    private String message;

    @JsonProperty("data")
    private T dataObject;

    public ResponseModel() {
    }

    public ResponseModel(String message) {
        this.message = message.toLowerCase(Locale.ROOT);
    }

    public ResponseModel<T> setDataObject(T dataObject) {
        this.dataObject = dataObject;
        return this;
    }
}
