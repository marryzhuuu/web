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

    if (isNaN(yValue) || yValue < -3 || yValue > 5) {
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
  newRow.insertCell().innerText = x.toFixed(1);
  newRow.insertCell().innerText = y.toFixed(1);
  newRow.insertCell().innerText = r.toFixed(1);
  newRow.insertCell().innerHTML = result
    ? "<span class=\"hit\">Попадание</span>"
    : "<span class=\"miss\">Промах</span>";
  newRow.insertCell().innerHTML = formatDate(timestamp);
}




document.addEventListener("DOMContentLoaded", () => {
  const table = document.getElementById("resultsTable");

  if (table) {
    for (let item of table.rows) {
      const x = parseFloat(item.children[0].innerText.trim());
      const y = parseFloat(item.children[1].innerText.trim());
      const r = parseFloat(item.children[2].innerText.trim());
      if (isNaN(x) || isNaN(y) || isNaN(r)) continue;

      const result = item.children[3].innerText.trim() === "Попадание";
      drawPoint(x, y, r, result);
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
  form.append("x", x.toFixed(1));
  form.append("y", y.toFixed(1));
  form.append("r", r.toFixed(1));
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


function drawGraph() {
    const scale = 150;

    // область
    const area = document.getElementById('area');

    area.setAttribute('d',
        `
         M 0,0 L 0,${-1/2 * scale} L ${-1 * scale},${-1/2 * scale} L ${-1 * scale},0 Z
         M 0,0 L ${-1 * scale},0 L 0,${1 * scale} Z
         M 0,0 L 0, ${-1 * scale} A ${1 * scale},${1 * scale} 0 0,1 ${1 * scale},0 L 0,0 Z
         `
    );

    // засечки и подписи
    drawGrid();
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

// Инициализация графика
drawGraph();