function initializeClock() {
    const clockElement = document.getElementById('clock-display');
    if (!clockElement) return;

    function updateClock() {
        const now = new Date();

        // Русская локаль для даты и времени
        const dateString = now.toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            weekday: 'long'
        });

        const timeString = now.toLocaleTimeString('ru-RU', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        });

        clockElement.innerHTML = `
            <div class="clock-date">${dateString}</div>
            <div class="clock-time">${timeString}</div>
        `;
    }

    // Обновляем каждые 12 секунд
    updateClock();
    setInterval(updateClock, 12000);
}

// Запускаем при полной загрузке DOM
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeClock);
} else {
    initializeClock();
}