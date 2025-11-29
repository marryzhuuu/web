<template>
  <div class="login-page">
    <div class="container">
      <header class="header card">
        <h1>Веб-приложение для проверки попадания точек в область</h1>
        <div class="student-info">
          <p><strong>Студент:</strong> Жукова Мария</p>
          <p><strong>Группа:</strong> P3232</p>
          <p><strong>Вариант:</strong> 381731</p>
        </div>
      </header>

      <main class="main-content">
        <LoginForm />
      </main>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/LoginForm.vue'

export default {
  name: 'LoginPage',
  components: {
    LoginForm
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 20px 0;
  display: flex;
  align-items: center;

  @include mobile {
    padding: 10px 0;
    align-items: flex-start;
  }
}

.header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 28px;

    @include mobile {
      font-size: 20px;
    }

    @include tablet {
      font-size: 24px;
    }
  }

  .student-info {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 15px;
    text-align: left;

    p {
      margin: 0;
      padding: 10px;
      background: #f8f9fa;
      border-radius: 6px;
      font-size: 14px;

      @include mobile {
        font-size: 12px;
        padding: 8px;
      }
    }
  }
}

.main-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;

  @include mobile {
    min-height: auto;
  }
}
</style>