import http from "../http-common";

const login = (username, password) => {
  return http.post("/auth/login", { username, password });
};

const register = (username, password, emailAddress, bankAccountNumber) => {
  return http.post("/auth/register", {
    username,
    password,
    emailAddress,
    bankAccountNumber
  });
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
};

const getAuthToken = () => {
  return localStorage.getItem("token");
};

const getUsername = () => {
  return localStorage.getItem("username");
};

const isAuthenticated = () => {
  return !!localStorage.getItem("token");
};

export default {
  login,
  register,
  logout,
  getAuthToken,
  getUsername,
  isAuthenticated
};

