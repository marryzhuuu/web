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

    const r = parseFloat(checkedR[0].value);
    const rect = graph.getBoundingClientRect();
    const x = e.clientX - rect.left - 200;
    const y = 200 - (e.clientY - rect.top);

    // Масштабирование координат
    const scale = 150 / r;
    const realX = x / scale;
    const realY = y / scale;

    // Установка значений в форму
    // Находим кнопку X с ближайшим значением
    const closestX = findClosestX(realX);
    const xButton = document.querySelector(`.x-btn[data-value="${closestX}"]`);
    if (xButton) {
        xButton.click(); // Эмулируем клик по кнопке
    }

    document.getElementById('y').value = realY.toFixed(2);

    // Отправляем форму
    document.getElementById('pointForm').submit();
});

// Функция для нахождения ближайшего значения X
function findClosestX(value) {
    const xValues = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
    return xValues.reduce((prev, curr) => {
        return (Math.abs(curr - value) < Math.abs(prev - value) ? curr : prev);
    });
}

// Обновление графика при изменении R
rCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', updateGraph);
});

function updateGraph() {
    const checkedR = document.querySelectorAll('.r-checkbox:checked');

    let r = 1;
    if (checkedR.length === 0) r = 1;
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
    updatePoints();

    if (checkedR.length === 0) return;

    // Обновляем засечки и подписи
    updateGrid(r, scale);
}

function updateGrid(r, scale) {
    const gridGroup = document.getElementById('grid');

    // Очищаем предыдущие засечки и подписи
    while (gridGroup.firstChild) {
        gridGroup.removeChild(gridGroup.firstChild);
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
            xLabel.setAttribute('font-size', 10);
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
            yLabel.setAttribute('font-size', 10);
            yLabel.textContent = value.toString();
            gridGroup.appendChild(yLabel);
        }
    });
}

function updatePoints() {
    fetch('/points') // Replace with the URL of your Servlet
        .then(response => response.json())
        .then(data => {
            const pointsGroup = document.getElementById('points');
            pointsGroup.innerHTML = ''; // Clear existing points

            data.forEach(point => { //Assumed that data is an array of json objects
    const checkedR = document.querySelectorAll('.r-checkbox:checked');

                let r = 1;
                if (checkedR.length === 0) r = 1;
                else r = parseFloat(checkedR[0].value)

                const scale = 150 / r;
                const scaledX = point.x * scale;
                const scaledY = -point.y * scale;

                const circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
                circle.setAttribute('cx', scaledX);
                circle.setAttribute('cy', scaledY);
                circle.setAttribute('r', 3);

                if(point.hit) {
                    circle.setAttribute('fill', 'green');
                } else {
                    circle.setAttribute('fill', 'red');
                }

                pointsGroup.appendChild(circle);
            });
        });
}

// Инициализация графика
updateGraph();