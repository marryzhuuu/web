<template>
  <div class="login-form card">
    <h2>{{ isRegisterMode ? 'Регистрация' : 'Вход в систему' }}</h2>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">Логин:</label>
        <input
          id="username"
          v-model="form.username"
          type="text"
          required
          :class="{ 'error': errors.username }"
          @input="clearError('username')"
        />
        <div v-if="errors.username" class="error-message">{{ errors.username }}</div>
      </div>

      <div class="form-group">
        <label for="password">Пароль:</label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          required
          :class="{ 'error': errors.password }"
          @input="clearError('password')"
        />
        <div v-if="errors.password" class="error-message">{{ errors.password }}</div>
      </div>

      <div v-if="message" :class="messageClass">{{ message }}</div>

      <div class="form-actions">
        <button
          type="submit"
          class="btn btn-primary"
          :disabled="loading"
        >
          {{ loading ? 'Вход...' : 'Войти' }}
        </button>

        <button
          type="button"
          class="btn btn-secondary"
          @click="switchToRegister"
          :disabled="loading"
        >
          {{ isRegisterMode ? 'Назад к входу' : 'Регистрация' }}
        </button>
      </div>
    </form>

  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

export default {
  name: 'LoginForm',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const isRegisterMode = ref(false)
    const loading = ref(false)
    const message = ref('')
    const messageType = ref('') // 'success' or 'error'

    const form = reactive({
      username: '',
      password: ''
    })

    const errors = reactive({
      username: '',
      password: ''
    })

    const messageClass = computed(() => {
      return messageType.value === 'success' ? 'success-message' : 'error-message'
    })

    const validateForm = () => {
      let isValid = true

      // Очистка предыдущих ошибок
      Object.keys(errors).forEach(key => errors[key] = '')

      if (form.username.length < 3) {
        errors.username = 'Логин должен содержать минимум 3 символа'
        isValid = false
      }

      if (form.password.length < 6) {
        errors.password = 'Пароль должен содержать минимум 6 символов'
        isValid = false
      }

      return isValid
    }

    const clearError = (field) => {
      errors[field] = ''
      message.value = ''
    }

    const showMessage = (text, type = 'error') => {
      message.value = text
      messageType.value = type
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true
      message.value = ''

      try {
        let result

        if (isRegisterMode.value) {
          console.log('register: ', form.username, form.password)
          result = await authStore.register({
            username: form.username,
            password: form.password
          })
        } else {
          console.log('login: ', form.username, form.password)
          result = await authStore.login({
            username: form.username,
            password: form.password
          })
        }

        if (result.success) {
          showMessage(
            isRegisterMode.value ? 'Регистрация успешна!' : 'Вход выполнен!',
            'success'
          )
          authStore.login({ username: form.username })
          setTimeout(() => {
            router.push('/main')
          }, 1000)
        } else {
          showMessage(result.error)
        }
      } catch (error) {
        showMessage('Произошла ошибка при выполнении запроса')
      } finally {
        loading.value = false
      }
    }

    const switchToRegister = () => {
      isRegisterMode.value = !isRegisterMode.value
      message.value = ''
      Object.keys(errors).forEach(key => errors[key] = '')
    }

    return {
      form,
      errors,
      loading,
      message,
      messageClass,
      isRegisterMode,
      handleSubmit,
      clearError,
      switchToRegister
    }
  }
}
</script>

<style lang="scss" scoped>
.login-form {
  max-width: 400px;
  width: 100%;

  @include mobile {
    max-width: 100%;
  }

  h2 {
    text-align: center;
    margin-bottom: 30px;
    color: $dark-color;
  }
}

.form-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  @include mobile {
    flex-direction: column;
  }

  .btn {
    flex: 1;
    min-width: 120px;

    @include mobile {
      min-width: auto;
    }
  }
}

.register-info {
  margin-top: 20px;
  padding: 15px;
  background: $light-color;
  border-radius: 8px;
  font-size: 14px;

  p {
    margin: 5px 0;
  }
}

.error {
  border-color: $danger-color !important;
}
</style>