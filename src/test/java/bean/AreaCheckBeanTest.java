package bean;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AreaCheckBeanTest {

    // === Первый квадрант: четверть окружности x²+y² ≤ (r/2)² ===

    @Test
    void firstQuadrant_insideCircle() {
        // r=2 → радиус четверти окружности = 1; (0.5, 0.5): 0.5 ≤ 1
        assertTrue(AreaCheckBean.checkArea(0.5, 0.5, 2.0));
    }

    @Test
    void firstQuadrant_outsideCircle() {
        // (1.0, 1.0): 1+1=2 > 1
        assertFalse(AreaCheckBean.checkArea(1.0, 1.0, 2.0));
    }

    @Test
    void firstQuadrant_onCircleBoundary() {
        // (1.0, 0.0): 1 ≤ 1 — на границе
        assertTrue(AreaCheckBean.checkArea(1.0, 0.0, 2.0));
    }

    // === Третий квадрант: прямоугольник x ∈ [-r/2, 0], y ∈ [-r, 0] ===

    @Test
    void thirdQuadrant_insideRect() {
        // r=2: x ≥ -1, y ≥ -2; (-0.5, -1.0) ✓
        assertTrue(AreaCheckBean.checkArea(-0.5, -1.0, 2.0));
    }

    @Test
    void thirdQuadrant_outsideByX() {
        // (-1.5, -1.0): x < -1 → false
        assertFalse(AreaCheckBean.checkArea(-1.5, -1.0, 2.0));
    }

    @Test
    void thirdQuadrant_outsideByY() {
        // (-0.5, -2.5): y < -2 → false
        assertFalse(AreaCheckBean.checkArea(-0.5, -2.5, 2.0));
    }

    @Test
    void thirdQuadrant_onCorner() {
        // (-1.0, -2.0): на углу прямоугольника
        assertTrue(AreaCheckBean.checkArea(-1.0, -2.0, 2.0));
    }

    // === Четвёртый квадрант: треугольник y ≥ x - r/2 ===

    @Test
    void fourthQuadrant_insideTriangle() {
        // r=2: y ≥ x - 1; (0.5, -0.3): -0.3 ≥ -0.5 ✓
        assertTrue(AreaCheckBean.checkArea(0.5, -0.3, 2.0));
    }

    @Test
    void fourthQuadrant_outsideTriangle() {
        // (1.0, -0.1): -0.1 < 0.0 → false
        assertFalse(AreaCheckBean.checkArea(1.0, -0.1, 2.0));
    }

    @Test
    void fourthQuadrant_onHypotenuse() {
        // (0.5, -0.5): -0.5 = 0.5 - 1 — на гипотенузе
        assertTrue(AreaCheckBean.checkArea(0.5, -0.5, 2.0));
    }

    // === Второй квадрант: всегда false ===

    @Test
    void secondQuadrant_alwaysFalse() {
        assertFalse(AreaCheckBean.checkArea(-1.0, 1.0, 2.0));
    }

    // === Начало координат: входит во все регионы ===

    @Test
    void origin_isInside() {
        assertTrue(AreaCheckBean.checkArea(0.0, 0.0, 2.0));
    }

    // === Зависимость от радиуса ===

    @Test
    void radiusScaling_smallRadius() {
        // r=1: четверть окружности радиуса 0.5; (0.4, 0.0) ≤ 0.5 ✓
        assertTrue(AreaCheckBean.checkArea(0.4, 0.0, 1.0));
        // (0.6, 0.0) > 0.5 → false
        assertFalse(AreaCheckBean.checkArea(0.6, 0.0, 1.0));
    }
}
