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
    if (checkedR.length === 0) return;

    const r = parseFloat(checkedR[0].value);
    const scale = 150 / r;

    // Обновляем область
    const area = document.getElementById('area');
    area.setAttribute('d',
        `M 0,0 L ${75 * scale},0 L ${75 * scale},${-150 * scale} L 0,${-150 * scale} Z
         M 0,0 L ${75 * scale},0 L 0,${150 * scale} Z
         M 0,0 A ${37.5 * scale},${37.5 * scale} 0 0,1 ${-37.5 * scale},${-37.5 * scale} L 0,0 Z`);
}

// Инициализация графика
updateGraph();