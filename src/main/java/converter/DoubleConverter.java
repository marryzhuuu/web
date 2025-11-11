package converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("doubleConverter")
public class DoubleConverter implements Converter<Double> {

    @Override
    public Double getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Заменяем запятую на точку для поддержки русского формата
            String normalizedValue = value.replace(',', '.');
            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException e) {
            String componentId = component.getId();
            String fieldName = getFieldName(componentId);

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка ввода",
                    fieldName + " должно быть числом"
            );
            throw new ConverterException(message);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Double value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    private String getFieldName(String componentId) {
        if ("y".equals(componentId)) {
            return "Координата Y";
        } else if ("r".equals(componentId)) {
            return "Радиус R";
        }
        return "Значение";
    }
}