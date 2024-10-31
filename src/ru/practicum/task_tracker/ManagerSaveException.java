package ru.practicum.task_tracker;


public class ManagerSaveException extends RuntimeException {

    private static final String MSG_SAVE = "Error occurred  while saving";
    private static final String MSG_LOAD = "Error occurred  while loading";

    private ManagerSaveException(String msg, Exception e) {
        super(msg, e);
    }

    public ManagerSaveException(String msg) {
        super(msg);
    }

    public static ManagerSaveException saveException(Exception e) {
        return new ManagerSaveException(MSG_SAVE, e);
    }

    public static Exception customExeption(String msg) {
        return new ManagerSaveException(msg);
    }

    public static ManagerSaveException loadException(Exception e) {
        return new ManagerSaveException(MSG_LOAD, e);
    }
}
