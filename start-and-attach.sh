#!/bin/bash

# Функция для проверки порта
wait_for_port() {
    local port=$1
    local timeout=30
    local counter=0

    echo "⏳ Waiting for port $port to become available..."

    while ! nc -z localhost $port; do
        sleep 1
        counter=$((counter + 1))

        if [ $counter -ge $timeout ]; then
            echo "❌ Timeout waiting for port $port"
            exit 1
        fi
    done

    echo "✅ Port $port is ready!"
}

# Останавливаем предыдущий сервер
echo "🛑 Stopping previous server..."
./gradlew appStop > /dev/null 2>&1
sleep 2

# Собираем проект
echo "🔨 Building project..."
./gradlew clean war

# Запускаем сервер в фоне
echo "🚀 Starting server..."
./gradlew appRunDebug &

# Ждем пока порт отладки станет доступен
wait_for_port 5005

# Даем серверу дополнительное время для полного старта
sleep 3

echo "🎯 Server is ready! You can now attach debugger."
echo "📝 Run 'Attach Debugger' configuration in IDEA"