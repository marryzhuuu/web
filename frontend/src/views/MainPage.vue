<template>
  <div class="main-page">
    <header class="header card">
      <div class="header-content">
        <div class="user-info">
          <h1>Проверка попадания точек в область</h1>
          <p>Вы вошли как: <strong>{{ username }}</strong></p>
        </div>
        <button class="btn btn-danger" @click="logout">
          Выйти
        </button>
      </div>
    </header>

    <div class="container">
      <div class="main-layout">
        <!-- Левая колонка - форма ввода -->
        <div class="left-column">
          <PointChecker
            @point-checked="handlePointChecked"
            @radius-changed="handleRadiusChanged"
            @clear-all="handleClearAll"
          />
        </div>

        <!-- Правая колонка - график и результаты -->
        <div class="right-column">
          <CoordinatePlot
            :radius="currentRadius"
            @point-selected="handlePointSelected"
            @point-checked="handlePointChecked"
            ref="plot"
          />
          <ResultsTable ref="resultsTable" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store'
import { authService } from '@/services/auth'
import PointChecker from '@/components/PointChecker.vue'
import CoordinatePlot from '@/components/CoordinatePlot.vue'
import ResultsTable from '@/components/ResultsTable.vue'

export default {
  name: 'MainPage',
  components: {
    PointChecker,
    CoordinatePlot,
    ResultsTable
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const plot = ref(null)
    const resultsTable = ref(null)
    const currentRadius = ref(null)
    const username = ref(authService.getStoredUsername() || 'пользователь')

    const handlePointChecked = (pointData) => {
      // Обновляем таблицу результатов
      if (resultsTable.value) {
        resultsTable.value.loadHistory()
      }
    }

    const handleClearAll = (pointData) => {
      // Очищаем отображение при невыбранном радиусе
      if (plot.value) {
        currentRadius.value = null;
        plot.value.clearPlot()
      }
    }

    const handleRadiusChanged = (radius) => {
      currentRadius.value = radius
    }

    const handlePointSelected = (point) => {
    }

    const logout = () => {
      authService.logout()
      authStore.logout()
      router.push('/')
    }

    onMounted(() => {
      if (!authStore.isAuthenticated) {
        router.push('/')
      }
    })

    return {
      plot,
      resultsTable,
      currentRadius,
      username,
      handlePointChecked,
      handleRadiusChanged,
      handlePointSelected,
      handleClearAll,
      logout
    }
  }
}
</script>

<style lang="scss" scoped>
.main-page {
  min-height: 100vh;
  padding: 20px 0;
  background: $primary-gradient;

  @include mobile {
    padding: 10px 0;
  }
}

.header {
  margin-bottom: 30px;

  @include mobile {
    margin-bottom: 20px;
  }
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;

  @include mobile {
    flex-direction: column;
    text-align: center;
  }
}

.user-info {
  h1 {
    margin-bottom: 8px;
    font-size: 24px;

    @include mobile {
      font-size: 20px;
    }

    @include tablet {
      font-size: 22px;
    }
  }

  p {
    color: $dark-color;
    margin: 0;
    font-size: 16px;

    @include mobile {
      font-size: 14px;
    }
  }
}

.main-layout {
  display: grid;
  gap: 30px;

  @include desktop {
    grid-template-columns: 1fr 1.5fr;
  }

  @include tablet {
    grid-template-columns: 1fr;
  }

  @include mobile {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

.left-column, .right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-column {
  @include desktop {
    min-height: 800px;
  }
}
</style>