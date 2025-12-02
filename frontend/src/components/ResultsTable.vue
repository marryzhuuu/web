<template>
  <div class="results-table card">
    <div class="table-header">
      <h2>Результаты проверки:</h2>
      <button
        class="btn btn-secondary btn-sm"
        @click="loadHistory"
        :disabled="loading"
      >
        {{ loading ? 'Обновление...' : 'Обновить' }}
      </button>
    </div>

    <div class="table-container">
      <table class="results-table">
        <thead>
          <tr>
            <th>X</th>
            <th>Y</th>
            <th>R</th>
            <th>Результат</th>
            <th>Время проверки</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="point in paginatedPoints"
            :key="point.id || point.checkTime"
            :class="point.result ? 'hit' : 'miss'"
          >
            <td>{{ formatCoordinate(point.x) }}</td>
            <td>{{ formatCoordinate(point.y) }}</td>
            <td>{{ point.r }}</td>
            <td>
              <span class="result-badge" :class="point.result ? 'hit' : 'miss'">
                {{ point.result ? 'Попадает' : 'Не попадает' }}
              </span>
            </td>
            <td>{{ formatDateTime(point.checkTime) }}</td>
          </tr>
        </tbody>
      </table>

      <div v-if="pointChecks.length === 0" class="empty-state">
        <p>Нет данных о проверках</p>
        <p class="hint">Проверьте точки, чтобы увидеть результаты</p>
      </div>
    </div>

    <!-- Пагинация -->
    <div v-if="totalPages > 1" class="pagination">
      <button
        class="pagination-btn"
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        ← Назад
      </button>

      <span class="pagination-info">
        Страница {{ currentPage }} из {{ totalPages }}
      </span>

      <button
        class="pagination-btn"
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Вперед →
      </button>
    </div>

    <div class="table-stats">
      <div class="stat-item">
        <strong>Всего проверок:</strong> {{ pointChecks.length }}
      </div>
      <div class="stat-item">
        <strong>Попаданий:</strong> {{ hitCount }}
      </div>
      <div class="stat-item">
        <strong>Промахов:</strong> {{ missCount }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from '@/store'
import { pointsAPI } from '@/services/api'

export default {
  name: 'ResultsTable',
  setup() {
    const store = useStore()
    const loading = ref(false)
    const currentPage = ref(1)
    const itemsPerPage = ref(10)

    const pointChecks = computed(() => store.state.pointChecks)

    const hitCount = computed(() => {
      return pointChecks.value.filter(point => point.result).length
    })

    const missCount = computed(() => {
      return pointChecks.value.filter(point => !point.result).length
    })

    const totalPages = computed(() => {
      return Math.ceil(pointChecks.value.length / itemsPerPage.value)
    })

    const paginatedPoints = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return pointChecks.value.slice(start, end)
    })

    const formatCoordinate = (value) => {
      return Number.isInteger(value) ? value.toString() : value.toFixed(2)
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '—'

      const date = new Date(dateTime)
      return date.toLocaleString('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    const loadHistory = async () => {
      loading.value = true
      try {
        const response = await pointsAPI.getHistory()
        store.dispatch('setPointChecks', response.data)
      } catch (error) {
        console.error('Ошибка при загрузке истории:', error)
      } finally {
        loading.value = false
      }
    }

    // Автоматически загружаем историю при монтировании компонента
    onMounted(() => {
      if (pointChecks.value.length === 0) {
        loadHistory()
      }
    })

    // Сбрасываем пагинацию при изменении данных
    watch(pointChecks, () => {
      if (currentPage.value > totalPages.value) {
        currentPage.value = Math.max(1, totalPages.value)
      }
    })

    // Адаптивное количество элементов на странице
    const updateItemsPerPage = () => {
      if (window.innerWidth < 657) { // mobile
        itemsPerPage.value = 5
      } else if (window.innerWidth < 1248) { // tablet
        itemsPerPage.value = 8
      } else { // desktop
        itemsPerPage.value = 10
      }
      currentPage.value = 1
    }

    onMounted(() => {
      updateItemsPerPage()
      window.addEventListener('resize', updateItemsPerPage)
    })

    return {
      pointChecks,
      paginatedPoints,
      loading,
      currentPage,
      totalPages,
      hitCount,
      missCount,
      formatCoordinate,
      formatDateTime,
      loadHistory
    }
  }
}
</script>

<style lang="scss" scoped>
.results-table {
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 15px;

    h2 {
      color: #2c3e50;
      margin: 0;

      @include mobile {
        font-size: 20px;
      }
    }
  }
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;

  @include mobile {
    padding: 6px 12px;
    font-size: 12px;
  }
}

.table-container {
  overflow-x: auto;
  margin-bottom: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;

  @include mobile {
    font-size: 12px;
  }
}

th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #e9ecef;

  @include mobile {
    padding: 8px 6px;
  }
}

td {
  padding: 12px 8px;
  border-bottom: 1px solid #e9ecef;

  @include mobile {
    padding: 8px 6px;
  }
}

tr {
  transition: background-color 0.3s ease;

  &:hover {
    background: #f8f9ff;
  }

  &.hit {
    border-left: 3px solid #28a745;
  }

  &.miss {
    border-left: 3px solid #dc3545;
  }
}

.result-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;

  &.hit {
    background: #d4edda;
    color: #155724;
  }

  &.miss {
    background: #f8d7da;
    color: #721c24;
  }
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;

  .hint {
    font-size: 12px;
    margin-top: 8px;
    opacity: 0.7;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #e9ecef;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;

  &:hover:not(:disabled) {
    background: #667eea;
    color: white;
    border-color: #667eea;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  @include mobile {
    padding: 6px 12px;
    font-size: 12px;
  }
}

.pagination-info {
  color: #495057;
  font-size: 14px;

  @include mobile {
    font-size: 12px;
  }
}

.table-stats {
  display: flex;
  justify-content: space-around;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
  gap: 15px;

  @include mobile {
    flex-direction: column;
    gap: 10px;
  }
}

.stat-item {
  font-size: 14px;
  color: #495057;

  @include mobile {
    font-size: 12px;
    text-align: center;
  }
}
</style>