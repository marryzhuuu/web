<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Result" %>
<!DOCTYPE html>
<html>
<head>
    <title>Area Checker</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles/reset.css">
    <link rel="stylesheet" href="styles/index.css">
    <script defer src="scripts/index.js"></script>
</head>
<body>
    <div class="header">
        <h1>Лабораторная работа #2</h1>
        <p>Студент: Жукова Мария</p>
        <p>Группа: P3232</p>
        <p>Вариант: 381731</p>
    </div>

    <div class="container">
        <div class="form-section">
            <h2>Ввод параметров</h2>
            <form id="pointForm" action="calculate" method="POST">
                <!-- Fieldset для координаты X -->
                <fieldset>
                    <legend>Координата X</legend>
                    <div class="x-buttons">
                        <button type="button" class="x-btn" data-value="-3">-3</button>
                        <button type="button" class="x-btn" data-value="-2">-2</button>
                        <button type="button" class="x-btn" data-value="-1">-1</button>
                        <button type="button" class="x-btn" data-value="0">0</button>
                        <button type="button" class="x-btn" data-value="1">1</button>
                        <button type="button" class="x-btn" data-value="2">2</button>
                        <button type="button" class="x-btn" data-value="3">3</button>
                        <button type="button" class="x-btn" data-value="4">4</button>
                        <button type="button" class="x-btn" data-value="5">5</button>
                    </div>
                    <input type="hidden" name="x" id="x" required>
                    <div id="x-error" class="error-message">Выберите значение X</div>
                </fieldset>

                <!-- Поле для координаты Y -->
                <fieldset>
                    <legend>Координата Y</legend>
                    <input type="text" name="y" id="y" class="y-input"
                           placeholder="Введите число от -3 до 5" required>
                    <div id="y-error" class="error-message">Введите число от -3 до 5</div>
                </fieldset>

                <!-- Fieldset для радиуса R -->
                <fieldset>
                    <legend>Радиус R</legend>
                    <div class="r-checkboxes">
                        <div class="r-checkbox-container">
                            <input type="checkbox" class="r-checkbox" id="r1" name="r" value="1">
                            <label for="r1" class="r-label">1</label>
                        </div>
                        <div class="r-checkbox-container">
                            <input type="checkbox" class="r-checkbox" id="r2" name="r" value="1.5">
                            <label for="r2" class="r-label">1.5</label>
                        </div>

                        <div class="r-checkbox-container">
                            <input type="checkbox" class="r-checkbox" id="r3" name="r" value="2">
                            <label for="r3" class="r-label">2</label>
                        </div>

                        <div class="r-checkbox-container">
                            <input type="checkbox" class="r-checkbox" id="r4" name="r" value="2.5">
                            <label for="r4" class="r-label">2.5</label>
                        </div>

                        <div class="r-checkbox-container">
                            <input type="checkbox" class="r-checkbox" id="r5" name="r" value="3">
                            <label for="r5" class="r-label">3</label>
                        </div>
                    </div>
                    <div id="r-error" class="error-message">Выберите значение R</div>
                </fieldset>

                <button type="submit">Проверить попадание</button>
            </form>
        </div>
        <div class="graph-section">
            <h2>График области</h2>
            <svg id="graph" width="400" height="400" viewBox="-200 -200 400 400">
                <!-- Координатные оси -->
                <line x1="-200" y1="0" x2="200" y2="0" stroke="black" stroke-width="2"/>
                <line x1="0" y1="-200" x2="0" y2="200" stroke="black" stroke-width="2"/>

                <!-- Подписи осей -->
                <text x="190" y="-10" font-size="12">X</text>
                <text x="5" y="-190" font-size="12">Y</text>

                <!-- Область (будет обновляться JavaScript) -->
                <path id="area" fill="lightblue" fill-opacity="0.5" stroke="blue"/>

                <!-- Сетка и разметка -->
                <g id="grid"></g>

                <!-- Точки результатов -->
                <g id="points"></g>
            </svg>
            <p id="graphMessage"></p>
        </div>
    </div>

    <div class="results-section">
        <h2>Результаты проверок</h2>
        <table>
            <thead>
                <tr>
                    <th>X</th>
                    <th>Y</th>
                    <th>R</th>
                    <th>Результат</th>
                    <th>Время</th>
                </tr>
            </thead>
            <tbody id="resultsTable">
                <%
                    ArrayList<Result> results = (ArrayList<Result>) session.getAttribute("results");
                    if (results != null) {
                        for (Result result : results) {
                %>
                <tr>
                    <td><%= result.getX() %></td>
                    <td><%= result.getY() %></td>
                    <td><%= result.getR() %></td>
                    <td class="<%= result.isHit() ? "hit" : "miss" %>">
                        <%= result.isHit() ? "Попадание" : "Промах" %>
                    </td>
                    <td><%= result.getTimestamp() %></td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>