<template>
  <div class="point-checker card">
    <h2>Координаты точки и радиус:</h2>

    <div class="input-sections">
      <!-- Координата X -->
      <div class="input-section">
        <h3>Координата X:</h3>
        <div class="button-group">
          <button
            v-for="xValue in xValues"
            :key="xValue"
            class="coordinate-btn"
            :class="{ 'active': form.x === xValue }"
            @click="setX(xValue)"
            type="button"
          >
            {{ xValue }}
          </button>
        </div>
        <div v-if="errors.x" class="error-message">{{ errors.x }}</div>
      </div>

      <!-- Координата Y -->
      <div class="input-section">
        <h3>Координата Y:</h3>
        <input
          v-model="form.y"
          type="text"
          placeholder="Введите число от -5 до 5"
          :class="{ 'error': errors.y }"
          @input="validateY"
        />
        <div v-if="errors.y" class="error-message">{{ errors.y }}</div>
      </div>

      <!-- Радиус R -->
      <div class="input-section">
        <h3>Радиус R:</h3>
        <div class="button-group">
          <button
            v-for="rValue in rValues"
            :key="rValue"
            class="coordinate-btn"
            :class="{ 'active': form.r === rValue }"
            @click="setR(rValue)"
            type="button"
          >
            {{ rValue }}
          </button>
        </div>
        <div v-if="errors.r" class="error-message">{{ errors.r }}</div>
      </div>
    </div>

    <div class="actions">
      <button
        class="btn btn-primary"
        @click="checkPoint"
        :disabled="!isFormValid || loading"
      >
        {{ loading ? 'Проверка...' : 'Проверить точку' }}
      </button>

      <button
        class="btn btn-secondary"
        @click="clearForm"
        :disabled="loading"
      >
        Очистить
      </button>
    </div>

    <div v-if="message" :class="messageClass">{{ message }}</div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useStore } from '@/store'
import { pointsAPI } from '@/services/api'

export default {
  name: 'PointChecker',
  emits: ['pointChecked', 'clear-all', 'radiusChanged'],
  setup(props, { emit }) {
    const store = useStore()

    const loading = ref(false)
    const message = ref('')
    const messageType = ref('')

    const xValues = [-4, -3, -2, -1, 0, 1, 2, 3, 4]
    const rValues = [-4, -3, -2, -1, 0, 1, 2, 3, 4]

    const form = reactive({
      x: null,
      y: '',
      r: null
    })

    const errors = reactive({
      x: '',
      y: '',
      r: ''
    })

    const isFormValid = computed(() => {
      return form.x !== null &&
             form.y !== '' &&
             form.y > 0 &&
             !errors.y &&
             form.r !== null
    })

    const messageClass = computed(() => {
      return messageType.value === 'success' ? 'success-message' : 'error-message'
    })

    const setX = (value) => {
      form.x = value
      errors.x = ''
    }

    const setR = (value) => {
      form.r = value
      errors.r = ''
      if (value <= 0) {
        errors.r = 'R должен быть положительным'
        return
      }
      emit('radiusChanged', value)
    }

    const validateY = () => {
      const value = form.y.trim()

      if (value === '') {
        errors.y = 'Введите значение Y'
        return
      }

      // Проверка на число
      const numValue = parseFloat(value.replace(',', '.'))
      if (isNaN(numValue)) {
        errors.y = 'Y должен быть числом'
        return
      }

      // Проверка диапазона
      if (numValue < -5 || numValue > 5) {
        errors.y = 'Y должен быть в диапазоне от -5 до 5'
        return
      }

      // Проверка точности
      if (value.split('.')[1]?.length > 10) {
        errors.y = 'Слишком много знаков после запятой'
        return
      }

      errors.y = ''
    }

    const clearForm = () => {
      form.x = null
      form.y = ''
      form.r = null
      Object.keys(errors).forEach(key => errors[key] = '')
      message.value = ''
      // Отправляем событие родителю
      emit('clear-all')
    }

    const showMessage = (text, type = 'error') => {
      message.value = text
      messageType.value = type
    }

    const checkPoint = async () => {
      if (!isFormValid.value) return

      loading.value = true
      message.value = ''

      try {
        const pointData = {
          x: form.x,
          y: parseFloat(form.y.replace(',', '.')),
          r: form.r
        }

        const response = await pointsAPI.checkPoint(pointData)

        if (response.data) {
          store.dispatch('addPointCheck', response.data)
          showMessage(
            `Точка (${pointData.x}, ${pointData.y}) ${response.data.result ? 'попадает' : 'не попадает'} в область при R=${pointData.r}`,
            'success'
          )
          emit('pointChecked', response.data)
        }
      } catch (error) {
        const errorMsg = error.response?.data?.error || 'Ошибка при проверке точки'
        showMessage(errorMsg)
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      errors,
      loading,
      message,
      messageClass,
      xValues,
      rValues,
      isFormValid,
      setX,
      setR,
      validateY,
      clearForm,
      checkPoint
    }
  }
}
</script>

<style lang="scss" scoped>
.point-checker {
  h2 {
    text-align: start;
    margin-bottom: 30px;
    color: #2c3e50;

    @include mobile {
      font-size: 20px;
      margin-bottom: 20px;
    }
  }
}

.input-sections {
  display: grid;
  gap: 24px;
  margin-bottom: 30px;

  @include desktop {
    grid-template-columns: repeat(3, 1fr);
  }

  @include tablet {
    grid-template-columns: repeat(2, 1fr);

    .input-section:nth-child(2) {
      grid-column: 1 / -1;
    }
  }

  @include mobile {
    grid-template-columns: 1fr;
  }
}

.input-section {
  h3 {
    margin-bottom: 12px;
    color: #495057;
    font-size: 16px;

    @include mobile {
      font-size: 14px;
    }
  }

  input {
    width: 100%;
  }
}

.button-group {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;

  @include mobile {
    grid-template-columns: repeat(3, 1fr);
    gap: 6px;
  }
}

.coordinate-btn {
  padding: 10px 8px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  background: white;
  color: #495057;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(.active) {
    border-color: #667eea;
    background: #f8f9ff;
  }

  &.active {
    background: $primary-gradient;
    color: white;
    border-color: #667eea;
  }

  @include mobile {
    padding: 8px 6px;
    font-size: 12px;
  }
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  @include mobile {
    flex-direction: column;
  }

  .btn {
    flex: 1;
    min-width: 140px;

    @include mobile {
      min-width: auto;
    }
  }
}

.error {
  border-color: #dc3545 !important;
}
</style>