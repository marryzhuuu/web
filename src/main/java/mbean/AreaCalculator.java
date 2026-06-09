package mbean;

/**
 * Вычисляет площадь фигуры, заданной методом checkArea в AreaCheckBean.
 *
 * Фигура состоит из трёх частей:
 *   Q1 (x≥0, y≥0): четверть круга радиуса r/2 — площадь = π·r²/16
 *   Q3 (x≤0, y≤0): прямоугольник [−r/2;0]×[−r;0] — площадь = r²/2
 *   Q4 (x≥0, y≤0): прямоугольный треугольник с катетами r/2 — площадь = r²/8
 */
public class AreaCalculator implements AreaCalculatorMBean {

    private volatile double r;

    public AreaCalculator(double r) {
        this.r = r;
    }

    @Override
    public double getR() { return r; }

    @Override
    public void setR(double r) { this.r = r; }

    @Override
    public double getCircleArea() {
        return Math.PI * r * r / 16.0;
    }

    @Override
    public double getRectangleArea() {
        return r * r / 2.0;
    }

    @Override
    public double getTriangleArea() {
        return r * r / 8.0;
    }

    @Override
    public double getArea() {
        return getCircleArea() + getRectangleArea() + getTriangleArea();
    }
}
