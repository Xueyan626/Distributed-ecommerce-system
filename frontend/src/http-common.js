import axios from "axios";

const instance = axios.create({
  baseURL: "/api",
  headers: {
    "Content-type": "application/json"
  }
});

// Add token to requests if available
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log("JWT token added to request header");
    } else {
      console.warn("No JWT token found in localStorage. Request may fail if authentication is required.");
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle 401 errors (unauthorized)
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default instance;