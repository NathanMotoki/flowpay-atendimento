FROM node:18-alpine AS build

WORKDIR /app

# Copiar package.json e package-lock.json
COPY package*.json ./

# Instalar dependências
RUN npm ci

# Copiar código fonte
COPY . .

# Build da aplicação
RUN npm run build

# Imagem final com servidor
FROM node:18-alpine

WORKDIR /app

# Instalar servidor HTTP simples
RUN npm install -g serve

# Copiar build da etapa anterior
COPY --from=build /app/dist ./dist

# Expor porta
EXPOSE 5173

# Executar servidor
CMD ["serve", "-s", "dist", "-l", "5173"]