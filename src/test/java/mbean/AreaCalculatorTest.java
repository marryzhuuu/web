package mbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AreaCalculatorTest {

    private static final double DELTA = 1e-9;

    private AreaCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new AreaCalculator(2.0);
    }

    // ── Формулы составных частей ────────────────────────────────────────────

    @Test
    void circleArea_r2_equalsQuarterCircleRadiusOne() {
        // π·(r/2)²/4 = π·1/4 ≈ 0.7854
        assertEquals(Math.PI / 4, calc.getCircleArea(), DELTA);
    }

    @Test
    void rectangleArea_r2_equalsTwoSquareUnits() {
        // r²/2 = 4/2 = 2.0
        assertEquals(2.0, calc.getRectangleArea(), DELTA);
    }

    @Test
    void triangleArea_r2_equalsHalfSquareUnit() {
        // r²/8 = 4/8 = 0.5
        assertEquals(0.5, calc.getTriangleArea(), DELTA);
    }

    @Test
    void totalArea_r2_isSumOfParts() {
        double expected = calc.getCircleArea() + calc.getRectangleArea() + calc.getTriangleArea();
        assertEquals(expected, calc.getArea(), DELTA);
    }

    @Test
    void totalArea_r2_matchesClosedFormula() {
        // Area = π·r²/16 + r²/2 + r²/8
        double r = 2.0;
        double expected = Math.PI * r * r / 16.0 + r * r / 2.0 + r * r / 8.0;
        assertEquals(expected, calc.getArea(), DELTA);
    }

    // ── r = 0 ────────────────────────────────────────────────────────────────

    @Test
    void area_r0_isZero() {
        calc.setR(0.0);
        assertEquals(0.0, calc.getArea(), DELTA);
        assertEquals(0.0, calc.getCircleArea(), DELTA);
        assertEquals(0.0, calc.getRectangleArea(), DELTA);
        assertEquals(0.0, calc.getTriangleArea(), DELTA);
    }

    // ── r = 1 ────────────────────────────────────────────────────────────────

    @Test
    void circleArea_r1_correctValue() {
        calc.setR(1.0);
        assertEquals(Math.PI / 16.0, calc.getCircleArea(), DELTA);
    }

    @Test
    void rectangleArea_r1_correctValue() {
        calc.setR(1.0);
        assertEquals(0.5, calc.getRectangleArea(), DELTA);
    }

    @Test
    void triangleArea_r1_correctValue() {
        calc.setR(1.0);
        assertEquals(0.125, calc.getTriangleArea(), DELTA);
    }

    @Test
    void totalArea_r1_isSumOfParts() {
        calc.setR(1.0);
        double expected = calc.getCircleArea() + calc.getRectangleArea() + calc.getTriangleArea();
        assertEquals(expected, calc.getArea(), DELTA);
    }

    // ── Масштабирование: Area ∝ r² ───────────────────────────────────────────

    @Test
    void area_scalesWithRSquared() {
        double area1 = calc.getArea();       // r = 2
        calc.setR(4.0);
        double area2 = calc.getArea();       // r = 4
        assertEquals(4.0 * area1, area2, DELTA);
    }

    // ── setR синхронизирует все атрибуты ─────────────────────────────────────

    @Test
    void setR_updatesAllAttributes() {
        calc.setR(3.0);
        double r = 3.0;
        assertEquals(Math.PI * r * r / 16.0, calc.getCircleArea(), DELTA);
        assertEquals(r * r / 2.0, calc.getRectangleArea(), DELTA);
        assertEquals(r * r / 8.0, calc.getTriangleArea(), DELTA);
        assertEquals(3.0, calc.getR(), DELTA);
    }

    // ── getR возвращает текущее значение ─────────────────────────────────────

    @Test
    void getR_returnsCurrentValue() {
        assertEquals(2.0, calc.getR(), DELTA);
        calc.setR(5.0);
        assertEquals(5.0, calc.getR(), DELTA);
    }
}
