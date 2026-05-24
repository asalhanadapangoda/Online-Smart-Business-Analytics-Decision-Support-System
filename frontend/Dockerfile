# Run stage
FROM nginx:alpine

# Copy custom nginx configuration to map API proxies and SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy pre-compiled static files from local dist directory
COPY dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
