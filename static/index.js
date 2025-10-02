"use strict";

const inputX = document.getElementById("x");
const inputY = document.getElementById("y");

// Инициализация полей формы
inputX.value = "";
inputY.value = "";
let selectedBtn = null;
const state = {
};

const table = document.getElementById("result-table");
const error = document.getElementById("error");

// Валидация вводимых данных
const validateState = (state) => {
    if (isNaN(state.x) || state.x < -3. || state.x > 5.) {
        error.hidden = false;
        error.innerText = "X должно быть в диапазоне (-3...5)";
        throw new Error("Invalid state");
    }

    if (isNaN(state.y) || state.y < -5. || state.y > 5.) {
        error.hidden = false;
        error.innerText = "Y должно быть в диапазоне (-5...5)";
        throw new Error("Invalid state");
    }

    if (isNaN(state.r)) {
        error.hidden = false;
        error.innerText = `Необходимо выбрать радиус R`;
        throw new Error("Invalid state");
    }


    error.hidden = true;
}


// Обработка ввода
inputX.addEventListener("change", (ev) => {
    state.x = parseFloat(ev.target.value);
});

inputY.addEventListener("change", (ev) => {
    state.y = parseFloat(ev.target.value);
});

Array.from(document.getElementById("rs").children)
    .filter(c => c.tagName === "INPUT")
    .forEach(btn => {
        btn.style.borderColor = "";
        btn.style.borderRadius = ""
        btn.addEventListener("click", function (ev) {
            selectedBtn = btn;

            Array.from(document.getElementById("rs").children)
                .filter(c => c.tagName === "INPUT")
                .forEach(btn => {
                    if (btn == selectedBtn) {
                        btn.style.borderColor = "#3399ff";
                        btn.style.borderRadius = "5px"
                    } else {
                        btn.style.borderColor = "";
                        btn.style.borderRadius = ""
                    }
                });

            state.r = parseFloat(ev.target.value);

        });
    });

document.getElementById("data-form").addEventListener("submit", async function (ev) {
    ev.preventDefault();

    validateState(state);

    const newRow = table.insertRow(-1);

    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowTime = newRow.insertCell(3);
    const rowExecTime = newRow.insertCell(4);
    const rowResult = newRow.insertCell(5);

    const params = new URLSearchParams(state);

    const response = await fetch("/fcgi-bin/web.jar?" + params.toString());

    const results = {
        x: state.x,
        y: state.y,
        r: state.r,
        execTime: "",
        time: "",
        result: false,
    };

    if (response.ok) {
        const result = await response.json();
        results.time = new Date(result.now).toLocaleString();
        results.execTime = `${result.time} ns`;
        results.result = result.result.toString();
    } else if (response.status === 400) {
        const result = await response.json();
        results.time = new Date(result.now).toLocaleString();
        results.execTime = "N/A";
        results.result = `error: ${result.reason}`;
    } else {
        results.time = "N/A";
        results.execTime = "N/A";
        results.result = "error"
    }

    const prevResults = JSON.parse(localStorage.getItem("results") || "[]");
    localStorage.setItem("results", JSON.stringify([...prevResults, results]));

    rowX.innerText = results.x.toString();
    rowY.innerText = results.y.toString();
    rowR.innerText = results.r.toString();
    rowTime.innerText = results.time;
    rowExecTime.innerText = results.execTime;
    rowResult.innerText = results.result;
});

const prevResults = JSON.parse(localStorage.getItem("results") || "[]");

prevResults.forEach(result => {
    const table = document.getElementById("result-table");

    const newRow = table.insertRow(-1);

    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowTime = newRow.insertCell(3);
    const rowExecTime = newRow.insertCell(4);
    const rowResult = newRow.insertCell(5);

    rowX.innerText = result.x.toString();
    rowY.innerText = result.y.toString();
    rowR.innerText = result.r.toString();
    rowTime.innerText = result.time;
    rowExecTime.innerText = result.execTime;
    rowResult.innerText = result.result;
});

// Рисование легенды на канвасе
const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');

// Размеры, пропорции, начало координат
const width = canvas.width;
const height = canvas.height;
const R = 100;
const centerX = width / 2;
const centerY = height / 2;

ctx.fillStyle = '#3399ff';

ctx.beginPath();
ctx.rect(centerX, centerY - R, R, R);
ctx.fill();

ctx.beginPath();
ctx.moveTo(centerX, centerY);
ctx.arc(centerX, centerY, R / 2, 0, Math.PI / 2, false);
ctx.lineTo(centerX, centerY);
ctx.fill();

ctx.beginPath();
ctx.moveTo(centerX, centerY);
ctx.lineTo(centerX - R / 2, centerY);
ctx.lineTo(centerX, centerY - R / 2);
ctx.closePath();
ctx.fill();

ctx.beginPath();
ctx.moveTo(centerX, 0);  // Y-axis
ctx.lineTo(centerX, height);
ctx.strokeText("Y", centerX + 6, 10);
ctx.moveTo(0, centerY);  // X-axis
ctx.lineTo(width, centerY);
ctx.strokeText("X", width - 10, centerY - 10);
ctx.stroke();

ctx.font = "12px monospace";

ctx.strokeText("0", centerX + 6, centerY - 6);
ctx.strokeText("R/2", centerX + R / 2 - 6, centerY - 6);
ctx.strokeText("R", centerX + R - 6, centerY - 6);

ctx.strokeText("-R/2", centerX - R / 2 - 18, centerY - 6);
ctx.strokeText("-R", centerX - R - 6, centerY - 6);

ctx.strokeText("R/2", centerX + 6, centerY - R / 2 + 6);
ctx.strokeText("R", centerX + 6, centerY - R + 6);

ctx.strokeText("-R/2", centerX + 6, centerY + R / 2 + 6);
ctx.strokeText("-R", centerX + 6, centerY + R + 6);