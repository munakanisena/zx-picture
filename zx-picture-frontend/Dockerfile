FROM node:21-slim AS builder
WORKDIR /web
COPY . /web
RUN npm install; \
    npm run build-only

FROM nginx:1.22.1-alpine
WORKDIR /web
COPY --from=builder /web/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

