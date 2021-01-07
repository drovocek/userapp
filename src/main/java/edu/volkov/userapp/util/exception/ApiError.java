package edu.volkov.userapp.util.exception;

import edu.volkov.userapp.to.PackageType;
import lombok.Getter;

@Getter
public class ApiError {

    private final String url;
    private final ErrorType type;
    private final String typeMessage;
    private final String[] details;
    private final String packageType;

    public ApiError(CharSequence url, ErrorType type, String typeMessage, PackageType packageType, String... details) {
        this.url = url.toString();
        this.type = type;
        this.typeMessage = typeMessage;
        this.details = details;
        this.packageType = packageType.name();
    }
}