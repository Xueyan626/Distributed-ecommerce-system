import http from "../http-common";

const createOrder = (itemId, quantity) => {
  return http.post("/orders", {
    itemId,
    quantity
  });
};

const getAllOrders = () => {
  return http.get("/orders");
};

const getOrderById = (id) => {
  return http.get(`/orders/${id}`);
};

const processPayment = (orderId) => {
  return http.post(`/orders/${orderId}/pay`);
};

const cancelOrder = (orderId) => {
  return http.post(`/orders/${orderId}/cancel`);
};

export default {
  createOrder,
  getAllOrders,
  getOrderById,
  processPayment,
  cancelOrder
};

