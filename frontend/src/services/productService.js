import http from "../http-common";

const getAllProducts = () => {
  // Call /api/products endpoint as per requirements
  return http.get("/products");
};

export default {
  getAllProducts
};

