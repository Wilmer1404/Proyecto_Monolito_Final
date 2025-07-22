FROM nginx:alpine

# Elimina los archivos default de Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copia tus archivos estáticos
COPY . /usr/share/nginx/html

EXPOSE 80
