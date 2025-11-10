function handleCanvasClick(event) {
    const canvas = document.getElementById('area-canvas');
    const rect = canvas.getBoundingClientRect();

    // Convert click coordinates to graph coordinates
    const x = ((event.clientX - rect.left) - 150) / 30; // 150 is center, 30 is scale
    const y = (150 - (event.clientY - rect.top)) / 30;

    // Send coordinates to server
    document.getElementById('hidden-x').value = x.toFixed(2);
    document.getElementById('hidden-y').value = y.toFixed(2);
    document.getElementById('graph-form:graph-check').click();
}

function drawGraph() {
    const canvas = document.getElementById('area-canvas');
    const ctx = canvas.getContext('2d');
    const r = #{areaCheckBean.r};

    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw coordinate system
    drawCoordinateSystem(ctx);

    // Draw area based on R
    drawArea(ctx, r);

    // Draw points from results
    drawPoints(ctx, r);
}

function drawCoordinateSystem(ctx) {
    const width = 300, height = 300;
    const centerX = width / 2, centerY = height / 2;

    // Set styles
    ctx.strokeStyle = '#000';
    ctx.fillStyle = '#000';
    ctx.lineWidth = 1;
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';

    // Draw axes
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.stroke();

    // Draw arrows
    ctx.beginPath();
    ctx.moveTo(width - 10, centerY - 5);
    ctx.lineTo(width, centerY);
    ctx.lineTo(width - 10, centerY + 5);
    ctx.moveTo(centerX - 5, 10);
    ctx.lineTo(centerX, 0);
    ctx.lineTo(centerX + 5, 10);
    ctx.stroke();

    // Draw labels and ticks
    drawTicks(ctx, centerX, centerY);
}

function drawTicks(ctx, centerX, centerY) {
    const scale = 30; // pixels per unit

    for (let i = -5; i <= 5; i++) {
        if (i === 0) continue;

        // X-axis ticks
        const xPos = centerX + i * scale;
        ctx.beginPath();
        ctx.moveTo(xPos, centerY - 5);
        ctx.lineTo(xPos, centerY + 5);
        ctx.stroke();
        ctx.fillText(i.toString(), xPos, centerY + 20);

        // Y-axis ticks
        const yPos = centerY - i * scale;
        ctx.beginPath();
        ctx.moveTo(centerX - 5, yPos);
        ctx.lineTo(centerX + 5, yPos);
        ctx.stroke();
        ctx.fillText(i.toString(), centerX - 20, yPos + 5);
    }

    // Axis labels
    ctx.fillText('X', 290, 140);
    ctx.fillText('Y', 160, 10);
}

function drawArea(ctx, r) {
    const centerX = 150, centerY = 150;
    const scale = 30;

    ctx.fillStyle = 'rgba(0, 100, 255, 0.5)';
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 1;

    // Rectangle (first quadrant)
    ctx.beginPath();
    ctx.rect(centerX, centerY - (r * scale / 2), r * scale, r * scale / 2);
    ctx.fill();
    ctx.stroke();

    // Triangle (second quadrant)
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX - r * scale, centerY);
    ctx.lineTo(centerX, centerY - r * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Quarter circle (fourth quadrant)
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, r * scale, 0, Math.PI / 2, false);
    ctx.lineTo(centerX, centerY);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
}

function drawPoints(ctx, r) {
    const centerX = 150, centerY = 150;
    const scale = 30;

    // Get results from the table (simplified approach)
    const results = #{resultsBean.results};

    results.forEach(result => {
        const x = centerX + result.x * scale;
        const y = centerY - result.y * scale;

        ctx.fillStyle = result.result ? 'green' : 'red';
        ctx.beginPath();
        ctx.arc(x, y, 3, 0, 2 * Math.PI);
        ctx.fill();
    });
}

// Redraw graph when page loads and when R changes
document.addEventListener('DOMContentLoaded', drawGraph);
