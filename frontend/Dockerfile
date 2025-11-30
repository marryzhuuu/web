# Frontend/Dockerfile
FROM httpd:2.4-alpine

# Копируем собранные файлы Vue.js
COPY dist/ /usr/local/apache2/htdocs/

# Копируем конфигурацию Apache
COPY httpd-local.conf /usr/local/apache2/conf/httpd.conf

# Копируем .htaccess для SPA
COPY .htaccess /usr/local/apache2/htdocs/.htaccess

EXPOSE 80

CMD ["httpd-foreground"]