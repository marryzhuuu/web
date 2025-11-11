function isValidFloat(str) {
    return /^-?\d*\.?\d+$/.test(str.trim());
}



function isRSelected() {
    const inputR = document.getElementById('input-form:r');
    if (isValidFloat(inputR.value)) {
        return true;
    }
    return false;
}

function selectedR() {
    const inputR = document.getElementById('input-form:r');
    return parseFloat(inputR.value).toFixed(2);
}

function isRValid() {
    if (!isRSelected()) return false;
    const r = selectedR();
    return r >= 1 && r <= 4;
}

function handleGraphClick(event) {
    const inputR = document.getElementById('input-form:r');
    const graphMessage = document.getElementById('graph-message');
    const graph = document.getElementById('graph');

    // Проверяем что выбран R
    if (!isRValid()) {
        graphMessage.textContent = 'Сначала выберите радиус R';
        return;
    }
    graphMessage.textContent = '';
    const r = selectedR();
    const rect = graph.getBoundingClientRect();
    const x = event.clientX - rect.left - 200;
    const y = 200 - (event.clientY - rect.top);

    // Масштабирование координат
    const scale = 150 / r;
    const realX = x / scale;
    const realY = y / scale;

   // Отправка координат на сервер
    document.getElementById('graph-form:hidden-x').value = realX.toFixed(2);
    document.getElementById('graph-form:hidden-y').value = realY.toFixed(2);
    document.getElementById('graph-form:hidden-r').value = r;
    document.getElementById('graph-form:graph-check').click();

}

// Обновление графика при изменении R

document.addEventListener('DOMContentLoaded', function() {
    setupRInputListener();
});

function updateGraph() {

    const checked = isRValid();

    let r = 1;
    if (!checked) r = 1;
    else r = selectedR();

    const scale = 150 / r;

    // Обновляем область
    const area = document.getElementById('area');

    area.setAttribute('d',
        `
         M 0,0 L ${-r/2 * scale},0 L ${-r/2 * scale},${r * scale} L 0,${r * scale} Z
         M 0,0 L ${r/2 * scale},0 L 0,${r/2 * scale} Z
         M 0,0 L 0,${-r/2 * scale} A ${r/2 * scale},${r/2 * scale} 0 0,1 ${r/2 * scale},0 L 0,0 Z
         `
    );

    // Обновляем точки
    updatePoints(checked, r);

    // Обновляем засечки и подписи
    updateGrid(r, scale, checked);
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

function drawCoordinateSystem(ctx) {
    const width = 300, height = 300;
    const centerX = width / 2, centerY = height / 2;

    // Set styles
    ctx.strokeStyle = '#000';
    ctx.fillStyle = '#000';
    ctx.lineWidth = 1;
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';

    // Draw axes
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.stroke();

    // Draw arrows
    ctx.beginPath();
    ctx.moveTo(width - 10, centerY - 5);
    ctx.lineTo(width, centerY);
    ctx.lineTo(width - 10, centerY + 5);
    ctx.moveTo(centerX - 5, 10);
    ctx.lineTo(centerX, 0);
    ctx.lineTo(centerX + 5, 10);
    ctx.stroke();

    // Draw labels and ticks
    drawTicks(ctx, centerX, centerY);
}

function drawTicks(ctx, centerX, centerY) {
    const scale = 30; // pixels per unit

    for (let i = -5; i <= 5; i++) {
        if (i === 0) continue;

        // X-axis ticks
        const xPos = centerX + i * scale;
        ctx.beginPath();
        ctx.moveTo(xPos, centerY - 5);
        ctx.lineTo(xPos, centerY + 5);
        ctx.stroke();
        ctx.fillText(i.toString(), xPos, centerY + 20);

        // Y-axis ticks
        const yPos = centerY - i * scale;
        ctx.beginPath();
        ctx.moveTo(centerX - 5, yPos);
        ctx.lineTo(centerX + 5, yPos);
        ctx.stroke();
        ctx.fillText(i.toString(), centerX - 20, yPos + 5);
    }

    // Axis labels
    ctx.fillText('X', 290, 140);
    ctx.fillText('Y', 160, 10);
}

function updatePoints(checked, r) {
    const pointsGroup = document.getElementById('points');
    pointsGroup.innerHTML = ''; // Clear existing points
    const resultsTable = document.getElementById('results-table');

    if (!checked) return;

    if (!window.graphData.points.length) return;

    const data = window.graphData.points;

    data.forEach(point => {
        const scale = 150 / r;
        const scaledX = point.x * scale;
        const scaledY = -point.y * scale;

        const circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
        circle.setAttribute('cx', scaledX);
        circle.setAttribute('cy', scaledY);
        circle.setAttribute('r', 3);
        circle.setAttribute('fill', point.result ? 'green' : 'red');

        pointsGroup.appendChild(circle);
    });
}

function updateGraphDataAfterPointAdded() {

    // Получаем актуальные данные из таблицы
    const points = getPointsFromTable();
    window.graphData.points = points;

    // Обновляем график
    const r = selectedR();
    updatePoints(true, r);

    // ПЕРЕУСТАНАВЛИВАЕМ event listeners после обновления DOM
    setTimeout(setupRInputListener, 50);
}

function setupRInputListener() {
    const rInput = document.getElementById('input-form:r');
    if (rInput) {
        // Удаляем старые listeners (на всякий случай)
        rInput.removeEventListener('input', handleRInput);

        // Добавляем новый listener
        rInput.addEventListener('input', handleRInput);
    }
}

function handleRInput(event) {
    let changeTimeout;
    clearTimeout(changeTimeout);
    changeTimeout = setTimeout(() => {
        updateGraphDataForNewR();
    }, 500);
}
function getPointsFromTable() {
    const resultsTable = document.getElementById('results-table');
    if (!resultsTable) return [];

    const rows = resultsTable.querySelectorAll('tbody tr');
    const points = [];

    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        if (cells.length >= 4) {
            try {
                const x = parseFloat(cells[0].textContent.trim());
                const y = parseFloat(cells[1].textContent.trim());
                const r = parseFloat(cells[2].textContent.trim());
                const resultText = cells[3].textContent.trim();
                const result = resultText.includes('Попадание');

                points.push({ x, y, r, result });
            } catch (e) {
                console.error('Error parsing table row:', e);
            }
        }
    });

    return points;
}

function updateGraphDataForNewR() {
    const r = selectedR();
    if (isRValid()) {
        // Используем PrimeFaces для обновления graph-data-container
        if (window.PrimeFaces) {
            // Находим форму и обновляем компонент
            const form = document.getElementById('input-form');
            if (form) {
                PrimeFaces.ab({
                    source: 'input-form:r',
                    process: '@this',
                    update: 'graph-data-container',
                    oncomplete: function() {
                        updateGraph();
                    }
                });
            }
        } else {
            // Fallback: обновляем из таблицы
            updateGraphDataFromTable();
            updateGraph();
        }
    }
}