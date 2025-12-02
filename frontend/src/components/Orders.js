import React, { useState, useEffect, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import orderService from "../services/orderService";
import Navbar from "./Navbar";
import "../App.css";

const Orders = () => {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [refreshing, setRefreshing] = useState(false);
  const [autoRefresh, setAutoRefresh] = useState(false);
  const [cancellingOrderId, setCancellingOrderId] = useState(null);
  const [payingOrderId, setPayingOrderId] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const autoRefreshInterval = useRef(null);

  const loadOrders = useCallback(async (silent = false) => {
    try {
      if (!silent) {
        setLoading(true);
      } else {
        setRefreshing(true);
      }
      setError("");
      const response = await orderService.getAllOrders();
      // Sort orders by creation time, newest first
      const sortedOrders = response.data.sort((a, b) => {
        const dateA = new Date(a.createdAt || 0);
        const dateB = new Date(b.createdAt || 0);
        return dateB - dateA;
      });
      setOrders(sortedOrders);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Failed to load orders"
      );
      console.error("Error loading orders:", err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  const handleRefresh = () => {
    loadOrders(false);
  };

  useEffect(() => {
    loadOrders();
    
    // Cleanup auto refresh on unmount
    return () => {
      if (autoRefreshInterval.current) {
        clearInterval(autoRefreshInterval.current);
      }
    };
  }, []);

  useEffect(() => {
    if (autoRefresh) {
      // Auto refresh every 10 seconds
      autoRefreshInterval.current = setInterval(() => {
        loadOrders(true);
      }, 10000);
    } else {
      if (autoRefreshInterval.current) {
        clearInterval(autoRefreshInterval.current);
      }
    }

    return () => {
      if (autoRefreshInterval.current) {
        clearInterval(autoRefreshInterval.current);
      }
    };
  }, [autoRefresh, loadOrders]);

  const formatPrice = (price) => {
    if (!price) return "$0.00";
    return `$${parseFloat(price).toFixed(2)}`;
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case "PENDING":
        return "badge bg-warning text-dark";
      case "PAID":
        return "badge bg-success";
      case "SHIPPED":
        return "badge bg-info text-dark";
      case "DELIVERED":
        return "badge bg-primary";
      case "CANCELLED":
        return "badge bg-danger";
      default:
        return "badge bg-secondary";
    }
  };

  

  // Check if order can be cancelled
  const canCancelOrder = (order) => {
    const status = order.status?.toUpperCase();
    // Can cancel if status is PENDING or CONFIRMED, and delivery request not sent
    return (
      (status === "PENDING" || status === "CONFIRMED") &&
      !order.deliveryRequestSent &&
      status !== "CANCELLED"
    );
  };

  // Check if order can be paid
  const canPayOrder = (order) => {
    const status = order.status?.toUpperCase();
    // Can pay if status is PENDING
    return status === "PENDING";
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm("Are you sure you want to cancel this order?")) {
      return;
    }

    // Check if user is authenticated
    const token = localStorage.getItem("token");
    if (!token) {
      setError("You must be logged in to cancel orders. Please login first.");
      navigate("/login");
      return;
    }

    try {
      setCancellingOrderId(orderId);
      setError("");
      setSuccessMessage("");
      
      console.log("Calling cancel API for order:", orderId);
      console.log("Token available:", !!token);
      const response = await orderService.cancelOrder(orderId);
      console.log("Cancel API response:", response);
      
      // Check response status
      if (response.status === 200 || response.status === 201) {
        setSuccessMessage("Order cancelled successfully!");
        // Refresh orders after cancellation
        await loadOrders(false);
        // Clear success message after 3 seconds
        setTimeout(() => {
          setSuccessMessage("");
        }, 3000);
      } else {
        throw new Error(`Unexpected response status: ${response.status}`);
      }
    } catch (err) {
      console.error("Cancel error details:", err);
      console.error("Error response:", err.response);
      
      let errorMsg = "Failed to cancel order";
      
      if (err.response) {
        // Handle different error status codes
        if (err.response.status === 400) {
          errorMsg = "Cannot cancel this order. It may have already been shipped or is not in a cancellable state.";
        } else if (err.response.status === 404) {
          errorMsg = "Order not found.";
        } else if (err.response.status === 401 || err.response.status === 403) {
          errorMsg = "You are not authorized to cancel this order.";
        } else {
          errorMsg = err.response.data?.message || 
                     err.response.statusText || 
                     `Cancel failed with status ${err.response.status}`;
        }
      } else if (err.message) {
        errorMsg = err.message;
      }
      
      setError(errorMsg);
    } finally {
      setCancellingOrderId(null);
    }
  };

  const handlePayOrder = async (orderId) => {
    if (!window.confirm("Proceed with payment for this order?")) {
      return;
    }

    // Check if user is authenticated
    const token = localStorage.getItem("token");
    if (!token) {
      setError("You must be logged in to process payment. Please login first.");
      navigate("/login");
      return;
    }

    try {
      setPayingOrderId(orderId);
      setError("");
      setSuccessMessage("");
      
      console.log("🚀 Attempting to pay for order:", orderId);
      console.log("Token available:", !!token);
      
      const response = await orderService.processPayment(orderId);
      
      console.log("✅ Payment API response:", response);
      console.log("Response status:", response?.status);
      console.log("Response data:", response?.data);
      
      // Check if API call was successful
      if (!response || response.status !== 200) {
        console.error("❌ Payment API failed - Unexpected status:", response?.status);
        throw new Error(`Payment API returned unexpected status: ${response?.status}`);
      }
      
      // Payment request sent successfully
      console.log("✅ Payment request sent successfully!");
      console.log("📋 Current order status from response:", response.data?.status);
      console.log("⚠️ Note: Payment processing is asynchronous via RabbitMQ.");
      console.log("⚠️ The order status may still be PENDING until Bank service processes the payment.");
      console.log("⚠️ Flow: Store → RabbitMQ → Bank → RabbitMQ → Store (updates order to CONFIRMED)");
      
      const currentStatus = response.data?.status;
      console.log(`📊 Initial status after payment request: ${currentStatus}`);
      
      setSuccessMessage("Payment request sent successfully! Processing payment asynchronously...");
      
      // Payment processing is asynchronous via RabbitMQ:
      // 1. Order status is still PENDING after this API call
      // 2. Backend sends payment request to Bank via RabbitMQ
      // 3. Bank processes payment and sends response back via RabbitMQ
      // 4. PaymentResponseListener updates order status to CONFIRMED
      // User needs to manually refresh to see updated status
      
    } catch (err) {
      console.error("❌ Payment error occurred:");
      console.error("Error object:", err);
      console.error("Error message:", err.message);
      console.error("Error response:", err.response);
      console.error("Error response status:", err.response?.status);
      console.error("Error response data:", err.response?.data);
      
      let errorMsg = "Failed to process payment";
      
      if (err.response) {
        // Handle different error status codes
        if (err.response.status === 400) {
          errorMsg = err.response.data?.message || 
                     "Cannot process payment for this order. It may not be in PENDING status or has already been processed.";
        } else if (err.response.status === 404) {
          errorMsg = "Order not found.";
        } else if (err.response.status === 401 || err.response.status === 403) {
          errorMsg = "You are not authorized to process payment for this order. Please check your authentication.";
        } else if (err.response.status === 500) {
          errorMsg = err.response.data?.message || 
                     "Server error occurred. Please try again later.";
        } else {
          errorMsg = err.response.data?.message || 
                     err.response.statusText || 
                     `Payment failed with status ${err.response.status}`;
        }
      } else if (err.message) {
        errorMsg = err.message;
      } else if (err.request) {
        errorMsg = "Network error: Could not connect to server. Please check your connection.";
      }
      
      console.error("Final error message:", errorMsg);
      setError(errorMsg);
      
      // Refresh orders to get latest status
      try {
        await loadOrders(false);
      } catch (loadErr) {
        console.error("Failed to refresh orders:", loadErr);
      }
    } finally {
      setPayingOrderId(null);
    }
  };

  if (loading) {
    return (
      <div className="home-container">
        <Navbar />
        <div className="container main-content mt-5">
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading orders...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="home-container">
      <Navbar />
      <div className="container main-content">
        <div className="orders-header mb-4">
          <h1 className="orders-title">My Orders</h1>
        </div>

        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}

        {successMessage && (
          <div className="alert alert-success" role="alert">
            {successMessage}
          </div>
        )}

        {orders.length === 0 ? (
          <div className="empty-orders">
            <p className="empty-orders-text">No orders found.</p>
            <button
              className="btn btn-primary"
              onClick={() => navigate("/")}
            >
              Browse Products
            </button>
          </div>
        ) : (
          <div className="orders-list">
            {orders.map((order) => (
              <div key={order.id} className="order-card">
                <div className="order-card-header">
                  <div className="order-id">
                    <strong>Order #{order.id}</strong>
                  </div>
                  <span className={getStatusBadgeClass(order.status)}>
                    {order.status || "UNKNOWN"}
                  </span>
                </div>
                <div className="order-card-body">
                  <div className="order-info-row">
                    <span className="order-label">Item ID:</span>
                    <span className="order-value">#{order.itemId}</span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Quantity:</span>
                    <span className="order-value">{order.quantity}</span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Price:</span>
                    <span className="order-value price">
                      {formatPrice(order.price)}
                    </span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Total:</span>
                    <span className="order-value price total">
                      {formatPrice(
                        parseFloat(order.price || 0) * (order.quantity || 1)
                      )}
                    </span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Created:</span>
                    <span className="order-value">
                      {formatDate(order.createdAt)}
                    </span>
                  </div>
                  {(canCancelOrder(order) || canPayOrder(order)) && (
                    <div className="order-card-actions">
                      {canPayOrder(order) && (
                        <button
                          className="pay-order-btn"
                          onClick={() => handlePayOrder(order.id)}
                          disabled={payingOrderId === order.id}
                        >
                          {payingOrderId === order.id ? (
                            <>
                              <span
                                className="spinner-border spinner-border-sm me-2"
                                role="status"
                                aria-hidden="true"
                              ></span>
                              Processing...
                            </>
                          ) : (
                            <>
                              <span className="me-2"></span>
                              Pay Now
                            </>
                          )}
                        </button>
                      )}
                      {canCancelOrder(order) && (
                        <button
                          className="cancel-order-btn"
                          onClick={() => handleCancelOrder(order.id)}
                          disabled={cancellingOrderId === order.id}
                        >
                          {cancellingOrderId === order.id ? (
                            <>
                              <span
                                className="spinner-border spinner-border-sm me-2"
                                role="status"
                                aria-hidden="true"
                              ></span>
                              Cancelling...
                            </>
                          ) : (
                            <>
                              <span className="me-2"></span>
                              Cancel Order
                            </>
                          )}
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Orders;

