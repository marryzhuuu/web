<template>
  <div class="coordinate-plot card">
    <h2>Координатная плоскость</h2>

    <div class="plot-container">
      <canvas
        ref="canvas"
        :width="canvasSize"
        :height="canvasSize"
        @click="handleCanvasClick"
        class="plot-canvas"
      ></canvas>
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
        <strong>Текущий радиус:</strong> {{ currentRadius || 'не выбран' }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useStore } from '@/store'

export default {
  name: 'CoordinatePlot',
  props: {
    radius: {
      type: Number,
      default: null
    }
  },
  emits: ['pointSelected'],
  setup(props, { emit }) {
    const store = useStore()
    const canvas = ref(null)
    const ctx = ref(null)

    const canvasSize = ref(400)
    const scale = ref(30) // пикселей на единицу координат
    const currentRadius = ref(props.radius)

    const pointColors = {
      hit: '#28a745',
      miss: '#dc3545'
    }

    onMounted(() => {
      initializeCanvas()
      drawCoordinateSystem()
      drawPoints()
    })

    watch(() => props.radius, (newRadius) => {
      currentRadius.value = newRadius
      redrawPlot()
    })

    watch(() => store.state.pointChecks, () => {
      drawPoints()
    }, { deep: true })

    const initializeCanvas = () => {
      if (canvas.value) {
        ctx.value = canvas.value.getContext('2d')

        // Адаптивный размер canvas
        const container = canvas.value.parentElement
        const containerWidth = container.clientWidth
        canvasSize.value = Math.min(400, containerWidth - 40)
        scale.value = canvasSize.value / 14 // 7 единиц в каждую сторону от центра
      }
    }

    const redrawPlot = () => {
      drawCoordinateSystem()
      drawPoints()
    }

    const drawCoordinateSystem = () => {
      if (!ctx.value) return

      const center = canvasSize.value / 2
      ctx.value.clearRect(0, 0, canvasSize.value, canvasSize.value)

      // Сетка
      ctx.value.strokeStyle = '#e9ecef'
      ctx.value.lineWidth = 1

      for (let i = -7; i <= 7; i++) {
        const pos = center + i * scale.value

        // Вертикальные линии
        ctx.value.beginPath()
        ctx.value.moveTo(pos, 0)
        ctx.value.lineTo(pos, canvasSize.value)
        ctx.value.stroke()

        // Горизонтальные линии
        ctx.value.beginPath()
        ctx.value.moveTo(0, pos)
        ctx.value.lineTo(canvasSize.value, pos)
        ctx.value.stroke()
      }

      // Оси координат
      ctx.value.strokeStyle = '#495057'
      ctx.value.lineWidth = 2

      // Ось X
      ctx.value.beginPath()
      ctx.value.moveTo(0, center)
      ctx.value.lineTo(canvasSize.value, center)
      ctx.value.stroke()

      // Ось Y
      ctx.value.beginPath()
      ctx.value.moveTo(center, 0)
      ctx.value.lineTo(center, canvasSize.value)
      ctx.value.stroke()

      // Стрелки осей
      drawArrow(center, 10, Math.PI * 1.5) // Y вверх
      drawArrow(canvasSize.value - 10, center, 0) // X вправо

      // Подписи осей
      ctx.value.fillStyle = '#495057'
      ctx.value.font = '12px Arial'
      ctx.value.fillText('Y', center - 15, 15)
      ctx.value.fillText('X', canvasSize.value - 15, center - 10)

      // Засечки и цифры на осях
      drawAxisLabels(center)

      // Отрисовка области, если выбран радиус
      if (currentRadius.value) {
        drawArea(center)
      }
    }

    const drawArrow = (x, y, angle) => {
      ctx.value.save()
      ctx.value.translate(x, y)
      ctx.value.rotate(angle)

      ctx.value.beginPath()
      ctx.value.moveTo(-5, -5)
      ctx.value.lineTo(0, 0)
      ctx.value.lineTo(-5, 5)
      ctx.value.stroke()

      ctx.value.restore()
    }

    const drawAxisLabels = (center) => {
      ctx.value.fillStyle = '#495057'
      ctx.value.font = '10px Arial'
      ctx.value.textAlign = 'center'

      for (let i = -6; i <= 6; i++) {
        if (i === 0) continue

        const pos = center + i * scale.value

        // Подписи оси X
        ctx.value.fillText(i.toString(), pos, center + 15)

        // Подписи оси Y
        ctx.value.fillText((-i).toString(), center - 15, pos + 3)
      }
    }

    const drawArea = (center) => {
      if (!currentRadius.value) return

      const r = currentRadius.value
      ctx.value.fillStyle = 'rgba(102, 126, 234, 0.3)'
      ctx.value.strokeStyle = '#667eea'
      ctx.value.lineWidth = 2

      ctx.value.beginPath()

      // 1-я четверть: прямоугольный треугольник
      ctx.value.moveTo(center, center)
      ctx.value.lineTo(center + r * scale.value, center) // вправо по X
      ctx.value.lineTo(center, center - (r/2) * scale.value) // вверх по Y (половина R)
      ctx.value.closePath()

      // 2-я четверть: четверть круга
      ctx.value.moveTo(center, center)
      ctx.value.arc(center, center, r * scale.value, Math.PI, Math.PI * 1.5, false)

      // 3-я четверть: прямоугольник
      ctx.value.moveTo(center, center)
      ctx.value.lineTo(center - (r/2) * scale.value, center) // влево по X (половина R)
      ctx.value.lineTo(center - (r/2) * scale.value, center + r * scale.value) // вниз по Y
      ctx.value.lineTo(center, center + r * scale.value) // вправо по X
      ctx.value.closePath()

      ctx.value.fill()
      ctx.value.stroke()
    }

    const drawPoints = () => {
      if (!ctx.value || !store.state.pointChecks.length) return

      const center = canvasSize.value / 2

      store.state.pointChecks.forEach(point => {
        // Рисуем только точки с текущим радиусом или все, если радиус не выбран
        if (!currentRadius.value || point.r === currentRadius.value) {
          const x = center + point.x * scale.value
          const y = center - point.y * scale.value // инвертируем Y для canvas

          ctx.value.beginPath()
          ctx.value.arc(x, y, 4, 0, Math.PI * 2)
          ctx.value.fillStyle = point.result ? pointColors.hit : pointColors.miss
          ctx.value.fill()
          ctx.value.strokeStyle = '#fff'
          ctx.value.lineWidth = 1
          ctx.value.stroke()
        }
      })
    }

    const handleCanvasClick = (event) => {
      if (!canvas.value) return

      const rect = canvas.value.getBoundingClientRect()
      const x = event.clientX - rect.left
      const y = event.clientY - rect.top

      const center = canvasSize.value / 2
      const coordX = (x - center) / scale.value
      const coordY = (center - y) / scale.value // инвертируем Y

      // Округляем координаты для лучшего UX
      const roundedX = Math.round(coordX * 2) / 2 // округление до 0.5
      const roundedY = Math.round(coordY * 2) / 2

      emit('pointSelected', { x: roundedX, y: roundedY })
    }

    const resizeCanvas = () => {
      nextTick(() => {
        initializeCanvas()
        redrawPlot()
      })
    }

    return {
      canvas,
      canvasSize,
      currentRadius,
      handleCanvasClick,
      resizeCanvas
    }
  }
}
</script>

<style lang="scss" scoped>
.coordinate-plot {
  h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #2c3e50;

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

.plot-canvas {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  background: white;
  cursor: crosshair;
  transition: border-color 0.3s ease;
  max-width: 100%;
  height: auto;

  &:hover {
    border-color: #667eea;
  }
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
  color: #495057;

  @include mobile {
    font-size: 12px;
  }
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.3);

  &.hit {
    background: #28a745;
  }

  &.miss {
    background: #dc3545;
  }
}

.current-radius {
  padding: 8px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 14px;
  color: #495057;

  @include mobile {
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>