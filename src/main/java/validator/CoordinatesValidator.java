package validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("coordinatesValidator")
public class CoordinatesValidator implements Validator<Double> {

    @Override
    public void validate(FacesContext context, UIComponent component, Double value) throws ValidatorException {
        String componentId = component.getId();

        if ("x".equals(componentId)) {
            validateX(value);
        } else if ("y".equals(componentId)) {
            validateY(value);
        } else if ("r".equals(componentId)) {
            validateR(value);
        }
    }

    private void validateX(Double x) {
        if (x == null) {
            throw new ValidatorException(new FacesMessage("X coordinate is required"));
        }
        if (x < -3 || x > 5) {
            throw new ValidatorException(new FacesMessage("X must be between -3 and 5"));
        }
    }

    private void validateY(Double y) {
        if (y == null) {
            throw new ValidatorException(new FacesMessage("Y coordinate is required"));
        }
        if (y < -3 || y > 5) {
            throw new ValidatorException(new FacesMessage("Y must be between -3 and 5"));
        }
    }

    private void validateR(Double r) {
        if (r == null) {
            throw new ValidatorException(new FacesMessage("Radius is required"));
        }
        if (r < 1 || r > 4) {
            throw new ValidatorException(new FacesMessage("R must be between 1 and 4"));
        }
    }
}