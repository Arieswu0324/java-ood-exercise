package coffeevendingmachine.exceptions;

public class IncompleteMachineSettingException extends CoffeeMachineException {

    public IncompleteMachineSettingException(String message) {
        super("Incomplete machine setting " + message + ", machine cannot start");
    }
}
