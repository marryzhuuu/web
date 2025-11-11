package bean;

import entity.Result;
import jakarta.annotation.PostConstruct;
import jakarta.el.ELResolver;
import jakarta.enterprise.context.ApplicationScoped;
import service.DatabaseService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;




@Named("areaCheckBean")
@ApplicationScoped
public class AreaCheckBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double x = 0.0;
    private Double y = 0.0;
    private Double r = 2.0;

    // Доступные значения для X
    private final List<Double> availableXValues = Arrays.asList(-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0);

    private DatabaseService databaseService;
    private ResultsBean resultsBean;


    // Получить существующий бин или создать новый
    public static <T> T getManagedBean(String beanName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ELResolver elResolver = elContext.getELResolver();

        return (T) elResolver.getValue(elContext, null, beanName);
    }

    @PostConstruct
    public void init() {
        try {
            // Инициализируем сервис вручную, так как @Inject не работает в Tomcat под Gretty
            databaseService = new DatabaseService();
            resultsBean = getManagedBean("resultsBean");
            resultsBean.setDatabaseService(databaseService);
        } catch (Exception e) {
            System.err.println("Error initializing ResultsBean: " + e.getMessage());
            e.printStackTrace();
            // Инициализируем пустой список в случае ошибки
            databaseService = new DatabaseService(); // Все равно создаем, но может быть нерабочим
            resultsBean = new ResultsBean();
            resultsBean.setDatabaseService(databaseService);
        }
    }

    public void checkPoint() {
        long startTime = System.nanoTime();

        boolean result = checkArea(x, y, r);
        long executionTime = (System.nanoTime() - startTime) / 1000; // microseconds

        Result pointResult = new Result(x, y, r, result, executionTime);
        databaseService.saveResult(pointResult);
        resultsBean.addResult(pointResult);
    }

    public void checkPointFromGraph(double x, double y) {
        this.x = x;
        this.y = y;
        checkPoint();
    }

    public static boolean checkArea(double x, double y, double r) {
        // Первый квадрант: окружность
        if (x >= 0 && y >= 0) {
            return (x * x + y * y) <= (r * r / 4);
        }
        // Третий квадрант: прямоугольник
        if (x <= 0 && y <= 0) {
            return x >= -r/2 && y >= -r;
        }
        // Четвертый квадрант: треугольник
        if (x >= 0 && y <= 0) {
            return y >= x - r/2;
        }
        return false;
    }

    // Геттер для доступных значений X
    public List<Double> getAvailableXValues() {
        return availableXValues;
    }

    // Getters and Setters
    public Double getX() { return x; }
//    public void setX(Double x) { this.x = x; }
    public void setX(Double xValue) {
        this.x = xValue;
//        logger.info("X value set to: " + x);
    }

    public Double getY() { return y; }
    public void setY(Double yValue) {
        this.y = yValue;
    }

    public Double getR() { return r; }
    public void setR(Double rValue) {
        this.r = rValue;
        // Trigger graph update when R changes
        resultsBean.updateResults();
    }


}