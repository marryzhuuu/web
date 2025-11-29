<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="scss">
#app {
  min-height: 100vh;
  font-family: 'Arial', sans-serif;
}

// Общие стили
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;

  @include mobile {
    padding: 0 10px;
  }
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 20px;

  @include mobile {
    padding: 16px;
    border-radius: 8px;
  }
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  text-align: center;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  @include mobile {
    padding: 10px 20px;
    font-size: 14px;
  }

  &-primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
    }
  }

  &-secondary {
    background: #f8f9fa;
    color: #495057;
    border: 2px solid #e9ecef;

    &:hover:not(:disabled) {
      background: #e9ecef;
    }
  }

  &-danger {
    background: #dc3545;
    color: white;

    &:hover:not(:disabled) {
      background: #c82333;
    }
  }
}

.form-group {
  margin-bottom: 20px;

  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #495057;
  }

  input, select {
    width: 100%;
    padding: 12px;
    border: 2px solid #e9ecef;
    border-radius: 8px;
    font-size: 16px;
    transition: border-color 0.3s ease;

    &:focus {
      outline: none;
      border-color: #667eea;
    }

    @include mobile {
      padding: 10px;
      font-size: 14px;
    }
  }
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  margin-top: 5px;
}

.success-message {
  color: #28a745;
  font-size: 14px;
  margin-top: 5px;
}
</style>