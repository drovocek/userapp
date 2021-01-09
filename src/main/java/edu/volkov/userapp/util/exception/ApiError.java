package edu.volkov.userapp.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError implements Serializable {

    private ErrorType type;
    private String typeMessage;
    private String[] details;
}