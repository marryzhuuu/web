// Обработка кнопок для координаты X
const xButtons = document.querySelectorAll('.x-btn');
const xInput = document.getElementById('x');
const xError = document.getElementById('x-error');

xButtons.forEach(button => {
    button.addEventListener('click', function() {
        // Снимаем выделение со всех кнопок
        xButtons.forEach(btn => btn.classList.remove('selected'));
        // Выделяем текущую кнопку
        this.classList.add('selected');
        // Устанавливаем значение
        xInput.value = this.getAttribute('data-value');
        xError.style.display = 'none';
    });
});

// Обработка чекбоксов для радиуса R
const rCheckboxes = document.querySelectorAll('.r-checkbox');
const rError = document.getElementById('r-error');

// Получить параметры из текущего URL и чекнуть радиус
const urlParams = new URLSearchParams(window.location.search);
const r = urlParams.get('r');
const checkbox = Array.from(rCheckboxes).find(cb => cb.value == r);
if (checkbox) checkbox.checked = true;


rCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        // Если этот чекбокс включили, выключаем остальные
        if (this.checked) {
            rCheckboxes.forEach(cb => {
                if (cb !== this) cb.checked = false;
            });
        }
        rError.style.display = 'none';
    });
});

function isValidFloat(str) {
    return /^-?\d*\.?\d+$/.test(str.trim());
}

// Валидация формы
document.getElementById('pointForm').addEventListener('submit', function(e) {
    let isValid = true;

    // Проверка X
    if (!xInput.value) {
        xError.style.display = 'block';
        isValid = false;
    }

    // Проверка Y
    const yInput = document.getElementById('y');
    const yValue = parseFloat(yInput.value);
    const yError = document.getElementById('y-error');

    if (isNaN(yValue) || yValue < -3 || yValue > 5 || !isValidFloat(yInput.value)) {
        yError.style.display = 'block';
        isValid = false;
    } else {
        yError.style.display = 'none';
    }

    // Проверка R
    const checkedR = document.querySelectorAll('.r-checkbox:checked');
    if (checkedR.length === 0) {
        rError.style.display = 'block';
        isValid = false;
    }

    if (!isValid) {
        e.preventDefault();
    }
});

// Обработка клика по графику
const graph = document.getElementById('graph');
const graphMessage = document.getElementById('graphMessage');

graph.addEventListener('click', function(e) {
    // Проверяем что выбран R
    const checkedR = document.querySelectorAll('.r-checkbox:checked');
    if (checkedR.length === 0) {
        graphMessage.textContent = 'Сначала выберите радиус R';
        return;
    }

    graphMessage.textContent = '';

    const r = parseFloat(checkedR[0].value);
    const rect = graph.getBoundingClientRect();
    const x = e.clientX - rect.left - 200;
    const y = 200 - (e.clientY - rect.top);

    // Масштабирование координат
    const scale = 150 / r;
    const realX = x / scale;
    const realY = y / scale;

    sendToServer(realX, realY, r);
});

async function sendToServer(x, y, r) {
  const data = await checkPoint(x, y, r);

  if (!data.error) {
    drawPoint(x, y, r, data.hit);
    addToTable(x, y, r, data.hit, data.timestamp);
  }
}

function addToTable(x, y, r, result, timestamp) {
  const table = document.getElementById("resultsTable");

  const newRow = table.insertRow();
  newRow.insertCell().innerText = x.toFixed(2);
  newRow.insertCell().innerText = y.toFixed(2);
  newRow.insertCell().innerText = r.toFixed(2);
  newRow.insertCell().innerHTML = result
    ? "<span class=\"hit\">Попадание</span>"
    : "<span class=\"miss\">Промах</span>";
  newRow.insertCell().innerHTML = formatDate(timestamp);
}




document.addEventListener("DOMContentLoaded", () => {
  const table = document.getElementById("resultsTable");

  if (table && table.rows.length > 2) {
    for (let item of table.rows) {
      const x = parseFloat(item.children[0].innerText.trim());
      const y = parseFloat(item.children[1].innerText.trim());
      const r = parseFloat(item.children[2].innerText.trim());
      if (isNaN(x) || isNaN(y) || isNaN(r)) continue;

      const result = item.children[3].innerText.trim() === "Попадание";
    }
  }
});



function formatDate(inputString) {
  const date = new Date(inputString);

  const weekday = date.toLocaleString('en-US', { weekday: 'short', timeZone: 'Europe/Moscow' });
  const month = date.toLocaleString('en-US', { month: 'short', timeZone: 'Europe/Moscow' });
  const day = date.getDate();
  const time = date.toLocaleString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
    timeZone: 'Europe/Moscow'
  });
  const year = date.getFullYear();

  return `${weekday} ${month} ${day} ${time} MSK ${year}`;
}

async function checkPoint(x, y, r) {
  const form = new FormData();
  form.append("x", x.toFixed(2));
  form.append("y", y.toFixed(2));
  form.append("r", r.toFixed(2));
  form.append("action", "checkPoint");

  const params = new URLSearchParams(form).toString();

  const url = "calculate";
  const response = await fetch(url, {
    method: "post",
    body: params,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
    }
  });

  if (!response.ok) {
    console.log("Не удалось отправить точку.");
  }

  const data = await response.json();
  if (data.error) console.log(data.error);

  return data;
}


function  drawGrid() {
    const gridGroup = document.getElementById('grid');

    // Координаты для засечек и подписей
    const tickLength = 5; // Длина засечек

    const valueMappings = [
      [-1, '-R'],
      [-1/2, '-R/2'],
      [1/2, 'R/2'],
      [1, 'R']
    ];

    valueMappings.forEach(value => {
        const scale = value[0];
        const label = value[1];
        // Ось X
        const xTick = document.createElementNS("http://www.w3.org/2000/svg", 'line');
        xTick.setAttribute('x1', 150 * scale);
        xTick.setAttribute('y1', -tickLength);
        xTick.setAttribute('x2', 150 * scale);
        xTick.setAttribute('y2', tickLength);
        xTick.setAttribute('stroke', 'black');
        xTick.setAttribute('stroke-width', 1);
        gridGroup.appendChild(xTick);

        const xLabel = document.createElementNS("http://www.w3.org/2000/svg", 'text');
        xLabel.setAttribute('x', 150 * scale - 5);
        xLabel.setAttribute('y', -10); // выше оси X
        xLabel.setAttribute('font-size', 12);
        xLabel.textContent = label;
        gridGroup.appendChild(xLabel);

        // ось Y
        const yTick = document.createElementNS("http://www.w3.org/2000/svg", 'line');
        yTick.setAttribute('x1', -tickLength);
        yTick.setAttribute('y1', -150 * scale);
        yTick.setAttribute('x2', tickLength);
        yTick.setAttribute('y2', -150 * scale);
        yTick.setAttribute('stroke', 'black');
        yTick.setAttribute('stroke-width', 1);
        gridGroup.appendChild(yTick);

        const yLabel = document.createElementNS("http://www.w3.org/2000/svg", 'text');
        yLabel.setAttribute('x', 10); // справа от оси Y
        yLabel.setAttribute('y', -150 * scale + 5);
        yLabel.setAttribute('font-size', 12);
        yLabel.textContent = label;
        gridGroup.appendChild(yLabel);
    });
}


function drawPoint(x, y, r, hit) {
    const pointsGroup = document.getElementById('points');
    const scale = 150;
    const scaledX = x * scale / r;
    const scaledY = -y * scale / r;

    const circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
    circle.setAttribute('cx', scaledX);
    circle.setAttribute('cy', scaledY);
    circle.setAttribute('r', 3);

    circle.setAttribute('fill', hit ? 'green' : 'red');

    pointsGroup.appendChild(circle);
}

// Функция для очистки истории
function clearHistory() {
    if (confirm('Вы уверены, что хотите очистить всю историю проверок?')) {
        fetch('clear-history', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                // Обновляем таблицу
                location.reload();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Ошибка при очистке истории');
        });
    }
}


// Обновление графика при изменении R
rCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', updateGraph);
});

function updateGraph() {
    const checkedR = document.querySelectorAll('.r-checkbox:checked');

    let r = 1;
    const checked = checkedR.length !== 0
    if (!checked) r = 1;
    else r = parseFloat(checkedR[0].value)

    const scale = 150 / r;

    // Обновляем область
    const area = document.getElementById('area');

    area.setAttribute('d',
        `
         M 0,0 L 0,${-r/2 * scale} L ${-r * scale},${-r/2 * scale} L ${-r * scale},0 Z
         M 0,0 L ${-r * scale},0 L 0,${r * scale} Z
         M 0,0 L 0, ${-r * scale} A ${r * scale},${r * scale} 0 0,1 ${r * scale},0 L 0,0 Z
         `
    );

    // Обновляем точки
    updatePoints(checked, r);

    // Обновляем засечки и подписи
    updateGrid(r, scale, checked);
}

function updateGrid(r, scale, checked) {
    const gridGroup = document.getElementById('grid');

    // Очищаем предыдущие засечки и подписи
    while (gridGroup.firstChild) {
        gridGroup.removeChild(gridGroup.firstChild);
    }
    if (!checked) {
        drawGrid();
        return;
    }

    // Координаты для засечек и подписей
    const tickLength = 5; // Длина засечек

    const values = [-r, -r / 2, r / 2, r];

    values.forEach(value => {
        const scaledValue = value * scale;
        // Ось X
        if (value !== 0) {
            const xTick = document.createElementNS("http://www.w3.org/2000/svg", 'line');
            xTick.setAttribute('x1', scaledValue);
            xTick.setAttribute('y1', -tickLength);
            xTick.setAttribute('x2', scaledValue);
            xTick.setAttribute('y2', tickLength);
            xTick.setAttribute('stroke', 'black');
            xTick.setAttribute('stroke-width', 1);
            gridGroup.appendChild(xTick);

            const xLabel = document.createElementNS("http://www.w3.org/2000/svg", 'text');
            xLabel.setAttribute('x', scaledValue - 5);
            xLabel.setAttribute('y', -10); // выше оси X
            xLabel.setAttribute('font-size', 12);
            xLabel.textContent = value.toString();
            gridGroup.appendChild(xLabel);
        }

        // ось Y
        if (value !== 0) {
            const yTick = document.createElementNS("http://www.w3.org/2000/svg", 'line');
            yTick.setAttribute('x1', -tickLength);
            yTick.setAttribute('y1', -scaledValue);
            yTick.setAttribute('x2', tickLength);
            yTick.setAttribute('y2', -scaledValue);
            yTick.setAttribute('stroke', 'black');
            yTick.setAttribute('stroke-width', 1);
            gridGroup.appendChild(yTick);

            const yLabel = document.createElementNS("http://www.w3.org/2000/svg", 'text');
            yLabel.setAttribute('x', 10); // справа от оси Y
            yLabel.setAttribute('y', -scaledValue + 5);
            yLabel.setAttribute('font-size', 12);
            yLabel.textContent = value.toString();
            gridGroup.appendChild(yLabel);
        }
    });
}

function updatePoints(checked, r) {
    const pointsGroup = document.getElementById('points');
    pointsGroup.innerHTML = ''; // Clear existing points

    if (!checked) return;

    fetch(`/points?r=${r}`)
        .then(response => response.json())
        .then(data => {

            if (!data) return;

            let pointIndex = 0;
            let lastPointR = 0;
            const checkedR = document.querySelectorAll('.r-checkbox:checked');
            let r = 1;
            if (checkedR.length === 0) r = 1;
            else r = parseFloat(checkedR[0].value)

            data.forEach(point => {
                pointIndex++;

                const scale = 150 / r;
                const scaledX = point.x * scale;
                const scaledY = -point.y * scale;

                const circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
                circle.setAttribute('cx', scaledX);
                circle.setAttribute('cy', scaledY);
                circle.setAttribute('r', 3);
                circle.setAttribute('fill', point.hit ? 'green' : 'red');

                pointsGroup.appendChild(circle);
            });

        });
}

// Инициализация графика
updateGraph();