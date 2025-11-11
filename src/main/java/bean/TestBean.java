package bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("testBean")
@SessionScoped
public class TestBean implements Serializable {
  private String textValue="Fucking test string";
  private Integer numberValue;
  private String selectedOption;
  private String resultMessage;

  public void saveData() {
    resultMessage = String.format("Сохранено: текст=%s, число=%d, опция=%s",
      textValue, numberValue, selectedOption);
  }

  public void showData() {
    resultMessage = String.format("Текущие значения: текст=%s, число=%d, опция=%s",
      textValue, numberValue, selectedOption);
  }

  public void clearData() {
    textValue = null;
    numberValue = null;
    selectedOption = null;
    resultMessage = "Данные очищены";
  }

  // Геттеры и сеттеры
  public String getTextValue() { return textValue; }
  public void setTextValue(String textValue) { this.textValue = textValue; }

  public Integer getNumberValue() { return numberValue; }
  public void setNumberValue(Integer numberValue) { this.numberValue = numberValue; }

  public String getSelectedOption() { return selectedOption; }
  public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

  public String getResultMessage() { return resultMessage; }
  public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
}
