package hotelmanagementsystem.exception;

public class HotelManagementException extends RuntimeException {
    public HotelManagementException() {
        super();
    }

    public HotelManagementException(String message){
        super(message);
    }
}
