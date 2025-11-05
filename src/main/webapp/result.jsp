<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Result" %>
<!DOCTYPE html>
<html>
<head>
    <title>Результат проверки</title>
    <meta charset="UTF-8">
    <link res="stylesheet" ref="styles/reset.css">
    <link rel="stylesheet" href="styles/result.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Результат проверки</h1>
        </div>

        <table>
            <tr>
                <th>Параметр</th>
                <th>Значение</th>
            </tr>
            <tr>
                <td>Координата X</td>
                <td>${x}</td>
            </tr>
            <tr>
                <td>Координата Y</td>
                <td>${y}</td>
            </tr>
            <tr>
                <td>Радиус R</td>
                <td>${r}</td>
            </tr>
            <tr>
                <td>Результат</td>
                <td class="${result.hit ? 'hit' : 'miss'}">
                    ${result.hit ? 'Попадание' : 'Промах'}
                </td>
            </tr>
            <tr>
                <td>Время проверки</td>
                <td>${result.timestamp}</td>
            </tr>
        </table>

        <a href="/?r=${r}" class="back-link">Вернуться к форме</a>
    </div>
</body>
</html>