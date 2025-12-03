<template>
  <div class="coordinate-plot card">
    <h2>Область проверки:</h2>

    <div class="plot-container">
      <svg id="graph" width="400" height="400"  viewBox="-200 -200 400 400" @click="handleGraphClick">
          <!-- Координатные оси -->
          <line x1="-200" y1="0" x2="200" y2="0" stroke="black" stroke-width="2"/>
          <line x1="0" y1="-200" x2="0" y2="200" stroke="black" stroke-width="2"/>

          <!-- Подписи осей -->
          <text x="190" y="-10" font-size="12">X</text>
          <text x="5" y="-190" font-size="12">Y</text>

          <!-- Область -->
          <path id="area" fill="lightblue" fill-opacity="0.5" stroke="blue"/>

          <!-- Сетка и разметка -->
          <g id="grid"> </g>

          <!-- Точки результатов -->
          <g id="points"> </g>
      </svg>
    </div>

    <div class="plot-info">
      <div class="legend">
        <div class="legend-item">
          <span class="color-dot hit"></span>
          <span>Точка попадает в область</span>
        </div>
        <div class="legend-item">
          <span class="color-dot miss"></span>
          <span>Точка не попадает в область</span>
        </div>
      </div>

      <div class="current-radius">
        <span  :class="{'miss': !currentRadius}"><strong>Текущий радиус:</strong> {{ currentRadius || 'не выбран' }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import {ref, onMounted, watch, nextTick, computed} from 'vue'
import { useStore } from '@/store'
import {pointsAPI} from "@/services/api";

export default {
  name: 'CoordinatePlot',
  props: {
    radius: {
      type: Number,
      default: null
    }
  },
  emits: ['pointSelected', 'pointChecked'],
  setup(props, { emit }) {
    const store = useStore()

    const pointChecks = computed(() => store.state.drawPointChecks)
    const loading = ref(false)

    const currentRadius = ref(props.radius)

    const pointColors = {
      hit: '#28a745',
      miss: '#dc3545'
    }

    onMounted(() => {
      updateGraph()
    })

    watch(() => props.radius, async(newRadius) => {
      currentRadius.value = newRadius
      if(newRadius) {
        loading.value = true
        try {
          const response = await pointsAPI.getHistory(newRadius)
          store.dispatch('setDrawPointChecks', response.data)
        } catch (error) {
          console.error('Ошибка при загрузке истории:', error)
        } finally {
          loading.value = false
        }
      }
      updateGraph()
    })

    watch(() => store.state.pointChecks, () => {
      // drawPoints()
    }, { deep: true })

    const handleGraphClick = async (event) => {

      const graph = document.getElementById('graph');

      // Проверяем что выбран R
      if (!currentRadius.value) {
          console.log('Сначала выберите радиус R');
          return;
      }
      const r = currentRadius.value;
      const rect = graph.getBoundingClientRect();
      const x = event.clientX - rect.left - 200;
      const y = 200 - (event.clientY - rect.top);

      // Масштабирование координат
      const scale = 150 / r;
      const realX = (x / scale).toFixed(2);
      const realY = (y / scale).toFixed(2);

      emit('pointSelected', { x: realX, y: realY})

     // Отправка координат на сервер
      try {
        const pointData = {
          x: realX,
          y: realY,
          r: currentRadius.value
        }

        const response = await pointsAPI.checkPoint(pointData)

        if (response.data) {
          store.dispatch('addPointCheck', response.data)
          store.dispatch('addDrawPointCheck', response.data)
          emit('pointChecked', response.data)
          updatePoints(currentRadius.value)

        }
      } catch (error) {
        const errorMsg = error.response?.data?.error || 'Ошибка при проверке точки'
        console.log(errorMsg)
      }
    }

    const updateGraph = () => {

      const r = currentRadius.value || 1;

      const scale = 150 / r;

      // Обновляем область
      const area = document.getElementById('area');

      area.setAttribute('d',
          `
           M 0,0 L ${-r/2 * scale},0 L ${-r/2 * scale},${r * scale} L 0,${r * scale} Z
           M 0,0 L ${-r/2 * scale},0 L 0,${-r/2 * scale} Z
           M 0,0 L 0,${-r * scale} A ${r * scale},${r * scale} 0 0,1 ${r * scale},0 L 0,0 Z
           `
      );

      // Обновляем точки
      updatePoints(currentRadius.value);

      // Обновляем засечки и подписи
      updateGrid(r, scale);
  }

  const updateGrid = (r, scale) => {
    const gridGroup = document.getElementById('grid');

    // Очищаем предыдущие засечки и подписи
    while (gridGroup.firstChild) {
        gridGroup.removeChild(gridGroup.firstChild);
    }
    if (!currentRadius.value) {
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

  const drawGrid = () => {
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

  const updatePoints = (r) => {
    const pointsGroup = document.getElementById('points');
    pointsGroup.innerHTML = ''; // Clear existing points
    if(!r) {
      return
    }

    const data = pointChecks.value;

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
    const clearPlot = () => {
      drawGrid()
    }

    return {
      currentRadius,
      handleGraphClick,
      // resizeCanvas,
      clearPlot
    }
  }
}
</script>

<style lang="scss" scoped>
.coordinate-plot {
  h2 {
    text-align: start;
    margin-bottom: 20px;
    color: $dark-color;

    @include mobile {
      font-size: 20px;
    }
  }
}

.plot-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.plot-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;

  @include mobile {
    flex-direction: column;
    align-items: flex-start;
  }
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: $dark-color;

  @include mobile {
    font-size: 12px;
  }
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 2px $dark-color;

  &.hit {
    background: $success-color;
  }

  &.miss {
    background: $danger-color;
  }
}
.miss {
  color: $danger-color;
}

.current-radius {
  padding: 8px 16px;
  background: $light-color;
  border-radius: 6px;
  font-size: 14px;
  color: $dark-color;


  @include mobile {
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>