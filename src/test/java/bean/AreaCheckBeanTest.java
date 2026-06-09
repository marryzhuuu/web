package bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AreaCheckBeanTest {

// первый квадрант: четверть окружности x²+y² ≤ (r/2)²

@Test

void firstQuadrant_insideCircle() {

assertTrue(AreaCheckBean.checkArea(0.5, 0.5, 2.0));

}

@Test

void firstQuadrant_outsideCircle() {

assertFalse(AreaCheckBean.checkArea(1.0, 1.0, 2.0));

}

@Test

void firstQuadrant_onCircleBoundary() {

assertTrue(AreaCheckBean.checkArea(1.0, 0.0, 2.0));

}

// третий квадрант: прямоугольник x ∈ [-r/2, 0], y ∈ [-r, 0]

@Test

void thirdQuadrant_insideRect() {

assertTrue(AreaCheckBean.checkArea(-0.5, -1.0, 2.0));

}

@Test

void thirdQuadrant_outsideByX() {

assertFalse(AreaCheckBean.checkArea(-1.5, -1.0, 2.0));

}

@Test

void thirdQuadrant_outsideByY() {

assertFalse(AreaCheckBean.checkArea(-0.5, -2.5, 2.0));

}

@Test

void thirdQuadrant_onCorner() {

assertTrue(AreaCheckBean.checkArea(-1.0, -2.0, 2.0));

}

// четвёртый квадрант: треугольник y ≥ x - r/2

@Test

void fourthQuadrant_insideTriangle() {

assertTrue(AreaCheckBean.checkArea(0.5, -0.3, 2.0));

}

@Test

void fourthQuadrant_outsideTriangle() {

assertFalse(AreaCheckBean.checkArea(1.0, -0.1, 2.0));

}

@Test

void fourthQuadrant_onHypotenuse() {

assertTrue(AreaCheckBean.checkArea(0.5, -0.5, 2.0));

}

// второй квадрант: всегда false

@Test

void secondQuadrant_alwaysFalse() {

assertFalse(AreaCheckBean.checkArea(-1.0, 1.0, 2.0));

}

// начало координат

@Test

void origin_isInside() {

assertTrue(AreaCheckBean.checkArea(0.0, 0.0, 2.0));

}

// зависимость от радиуса

@Test

void radiusScaling_smallRadius() {

assertTrue(AreaCheckBean.checkArea(0.4, 0.0, 1.0));

assertFalse(AreaCheckBean.checkArea(0.6, 0.0, 1.0));

}

// граничные по x

@Test

void boundary_x_equals_zero() {

assertTrue(AreaCheckBean.checkArea(0.0, 0.5, 2.0));

assertTrue(AreaCheckBean.checkArea(0.0, -1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(0.0, -0.5, 2.0));

}

@Test

void boundary_x_equals_R_div_2() {

// x = R/2 = 1 - правая граница

// Используем точки явно внутри и явно снаружи

assertTrue(AreaCheckBean.checkArea(0.5, 0.0, 2.0)); // внутри круга

assertTrue(AreaCheckBean.checkArea(0.5, -0.2, 2.0)); // внутри треугольника

assertFalse(AreaCheckBean.checkArea(1.5, 0.0, 2.0)); // явно вне круга

assertFalse(AreaCheckBean.checkArea(1.5, -0.5, 2.0)); // явно вне треугольника

}

@Test

void boundary_x_equals_minus_R_div_2() {

assertTrue(AreaCheckBean.checkArea(-1.0, -1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(-1.0, -2.0, 2.0));

assertFalse(AreaCheckBean.checkArea(-1.0, 0.5, 2.0));

assertFalse(AreaCheckBean.checkArea(-1.5, -1.0, 2.0));

}

@Test

void boundary_x_just_inside_outside() {

double epsilon = 0.001;

assertFalse(AreaCheckBean.checkArea(-1.0 - epsilon, -1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(-1.0 + epsilon, -1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(1.0 - epsilon, 0.0, 2.0));

assertFalse(AreaCheckBean.checkArea(1.0 + epsilon, 0.0, 2.0));

}

// граничные по У

@Test

void boundary_y_equals_zero() {

assertTrue(AreaCheckBean.checkArea(0.5, 0.0, 2.0));

assertTrue(AreaCheckBean.checkArea(0.0, 0.0, 2.0));

assertTrue(AreaCheckBean.checkArea(-0.5, 0.0, 2.0));

}

@Test

void boundary_y_equals_R_div_2() {

assertTrue(AreaCheckBean.checkArea(0.0, 1.0, 2.0));

assertFalse(AreaCheckBean.checkArea(0.5, 1.0, 2.0));

assertFalse(AreaCheckBean.checkArea(0.0, 1.5, 2.0));

}

@Test

void boundary_y_equals_minus_R_div_2() {

assertTrue(AreaCheckBean.checkArea(-0.5, -1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(-0.5, -0.5, 2.0));

assertTrue(AreaCheckBean.checkArea(-0.5, -1.5, 2.0));

}

@Test

void boundary_y_equals_minus_R() {

assertTrue(AreaCheckBean.checkArea(-0.5, -2.0, 2.0));

assertFalse(AreaCheckBean.checkArea(-0.5, -2.1, 2.0));

assertTrue(AreaCheckBean.checkArea(-0.5, -1.5, 2.0));

}

@Test

void boundary_y_just_inside_outside() {

double epsilon = 0.001;

assertTrue(AreaCheckBean.checkArea(-0.5, -2.0 + epsilon, 2.0));

assertFalse(AreaCheckBean.checkArea(-0.5, -2.0 - epsilon, 2.0));

}

// эквивалентные

@Test

void equivalenceClass_x_less_than_minus_R() {

assertFalse(AreaCheckBean.checkArea(-2.0, -1.0, 2.0));

assertFalse(AreaCheckBean.checkArea(-10.0, -1.0, 2.0));

assertFalse(AreaCheckBean.checkArea(-100.0, 0.0, 2.0));

}

@Test

void equivalenceClass_x_greater_than_R() {

assertFalse(AreaCheckBean.checkArea(2.0, 0.0, 2.0));

assertFalse(AreaCheckBean.checkArea(10.0, 0.0, 2.0));

}

@Test

void equivalenceClass_y_greater_than_R_div_2() {

assertFalse(AreaCheckBean.checkArea(0.0, 1.5, 2.0));

assertFalse(AreaCheckBean.checkArea(0.5, 2.0, 2.0));

assertFalse(AreaCheckBean.checkArea(0.0, 10.0, 2.0));

}

@Test

void equivalenceClass_y_less_than_minus_R() {

assertFalse(AreaCheckBean.checkArea(0.0, -2.5, 2.0));

assertFalse(AreaCheckBean.checkArea(-0.5, -10.0, 2.0));

}

@Test

void equivalenceClass_secondQuadrant() {

assertFalse(AreaCheckBean.checkArea(-0.1, 0.1, 2.0));

assertFalse(AreaCheckBean.checkArea(-0.9, 0.9, 2.0));

assertFalse(AreaCheckBean.checkArea(-0.5, 0.5, 2.0));

}

// углов точки

@Test

void cornerPoints_allCorners() {

assertTrue(AreaCheckBean.checkArea(-1.0, 0.0, 2.0));

assertTrue(AreaCheckBean.checkArea(-1.0, -2.0, 2.0));

assertTrue(AreaCheckBean.checkArea(0.0, -2.0, 2.0));

assertTrue(AreaCheckBean.checkArea(1.0, 0.0, 2.0));

assertTrue(AreaCheckBean.checkArea(0.0, 1.0, 2.0));

assertTrue(AreaCheckBean.checkArea(0.25, -0.25, 2.0));

assertTrue(AreaCheckBean.checkArea(0.1, -0.5, 2.0));

}

// границы радиуса

@Test

void boundary_radius_zero() {

assertTrue(AreaCheckBean.checkArea(0.0, 0.0, 0.0));

assertFalse(AreaCheckBean.checkArea(0.1, 0.0, 0.0));

assertFalse(AreaCheckBean.checkArea(0.0, -0.1, 0.0));

}

@Test

void boundary_radius_very_small() {

double smallR = 0.001;

assertTrue(AreaCheckBean.checkArea(0.0, 0.0, smallR));

assertFalse(AreaCheckBean.checkArea(0.001, 0.0, smallR));

}

@Test

void boundary_radius_very_large() {

double largeR = 1000.0;

assertTrue(AreaCheckBean.checkArea(100.0, 100.0, largeR));

assertTrue(AreaCheckBean.checkArea(-100.0, -500.0, largeR));

}

// на границах обл

@Test

void boundary_circle_arc() {

double r = 2.0;

double radius = r / 2;

assertTrue(AreaCheckBean.checkArea(radius, 0.0, r));

assertTrue(AreaCheckBean.checkArea(0.0, radius, r));

assertTrue(AreaCheckBean.checkArea(radius * Math.cos(Math.PI/4),

radius * Math.sin(Math.PI/4), r));

}

@Test

void boundary_triangle_hypotenuse() {

double r = 2.0;

assertTrue(AreaCheckBean.checkArea(0.0, -1.0, r));

assertTrue(AreaCheckBean.checkArea(0.5, -0.5, r));

assertTrue(AreaCheckBean.checkArea(1.0, 0.0, r));

}

@Test

void boundary_just_outside_circle() {

double r = 2.0;

double radius = r / 2;

double epsilon = 0.001;

assertFalse(AreaCheckBean.checkArea(radius + epsilon, 0.0, r));

assertFalse(AreaCheckBean.checkArea(0.0, radius + epsilon, r));

}

@Test

void boundary_just_outside_triangle() {

double r = 2.0;

double epsilon = 0.001;

assertFalse(AreaCheckBean.checkArea(0.5, -0.6, r));

assertFalse(AreaCheckBean.checkArea(1.0, -0.1, r));

}

// спец случ

@Test

void specialCases_pointsOnAxes() {

double r = 2.0;

assertTrue(AreaCheckBean.checkArea(0.5, 0.0, r));

assertTrue(AreaCheckBean.checkArea(0.0, 0.5, r));

assertTrue(AreaCheckBean.checkArea(-0.5, 0.0, r));

assertTrue(AreaCheckBean.checkArea(0.0, -0.5, r));

assertTrue(AreaCheckBean.checkArea(0.0, -1.5, r));

}

@Test

void specialCases_veryCloseToBoundary() {

double r = 2.0;

double epsilon = 1e-10;

assertTrue(AreaCheckBean.checkArea(1.0 - epsilon, 0.0, r));

assertTrue(AreaCheckBean.checkArea(0.0, 1.0 - epsilon, r));

assertTrue(AreaCheckBean.checkArea(-1.0 + epsilon, -2.0 + epsilon, r));

}

}