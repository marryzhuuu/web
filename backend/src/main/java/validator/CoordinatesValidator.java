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

        // Проверка на null (если конвертер пропустил пустое значение)
        if (value == null) {
            String fieldName = getFieldName(componentId);
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации",
                    fieldName + " является обязательным полем"
            ));
        }

        if ("x".equals(componentId)) {
            validateX(value);
        } else if ("y".equals(componentId)) {
            validateY(value);
        } else if ("r".equals(componentId)) {
            validateR(value);
        }
    }

    private void validateX(Double x) {
        if (x < -3 || x > 5) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации X",
                    "Координата X должна быть в диапазоне от -3 до 5"
            ));
        }
    }

    private void validateY(Double y) {
        if (y < -3 || y > 5) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации Y",
                    "Координата Y должна быть в диапазоне от -3 до 5"
            ));
        }
    }

    private void validateR(Double r) {
        if (r < 1 || r > 4) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации R",
                    "Радиус R должен быть в диапазоне от 1 до 4"
            ));
        }
    }

    private String getFieldName(String componentId) {
        if ("x".equals(componentId)) {
            return "Координата X";
        } else if ("y".equals(componentId)) {
            return "Координата Y";
        } else if ("r".equals(componentId)) {
            return "Радиус R";
        }
        return "Поле";
    }
}