package service.exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, IOException cause) {
        super(message, cause);
    }
}
