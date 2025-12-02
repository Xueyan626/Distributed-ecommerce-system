FROM node:18-alpine

WORKDIR /app

# Copy package files first to leverage Docker layer caching
COPY package*.json ./

# Install dependencies
# Using --legacy-peer-deps if needed for compatibility
RUN npm install --legacy-peer-deps

# Copy application code
COPY . .

# Expose the port React will run on
EXPOSE 3000

# Start the development server
CMD ["npm", "start"]

