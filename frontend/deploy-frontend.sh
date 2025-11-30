#!/bin/bash

echo "Building Vue.js application..."
cd frontend

# Установка зависимостей если нужно
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Сборка проекта
echo "Building project..."
npm run build

# Проверка что сборка прошла успешно
if [ ! -d "dist" ]; then
    echo "Build failed: dist folder not found"
    exit 1
fi

echo "Build completed successfully"

# Копирование конфигурационных файлов
echo "Copying configuration files..."
cp apache-config.conf dist/
cp .htaccess dist/

# Сборка Docker образа
echo "Building Docker image..."
docker build -t frontend .

# Остановка и удаление старого контейнера
echo "Stopping old container..."
docker stop frontend || true
docker rm frontend || true

# Запуск нового контейнера
echo "Starting new container..."
docker run \
    --name frontend \
    -p 80:80 \
    frontend

echo "Frontend deployed successfully!"
echo "Access your application at: http://localhost"