# build stage
FROM node:16.8.0-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ENTRYPOINT [ "npm", "run", "serve" ] 