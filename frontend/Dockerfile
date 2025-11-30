# Frontend/Dockerfile
FROM httpd:2.4-alpine

# Копируем собранные файлы Vue.js
COPY dist/ /usr/local/apache2/htdocs/

# Копируем конфигурацию Apache
COPY apache-config.conf /usr/local/apache2/conf/httpd.conf

# Копируем .htaccess для SPA
COPY .htaccess /usr/local/apache2/htdocs/.htaccess

# Устанавливаем необходимые модули
RUN echo "LoadModule rewrite_module modules/mod_rewrite.so" >> /usr/local/apache2/conf/httpd.conf

EXPOSE 80

CMD ["httpd-foreground"]