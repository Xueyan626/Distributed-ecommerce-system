import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import productService from "../services/productService";
import orderService from "../services/orderService";
import Navbar from "./Navbar";
import "../App.css";

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [ordering, setOrdering] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    loadProduct();
  }, [id]);

  const loadProduct = async () => {
    try {
      setLoading(true);
      setError("");
      const response = await productService.getAllProducts();
      const foundProduct = response.data.find(
        (p) => p.id === parseInt(id)
      );

      if (foundProduct) {
        setProduct(foundProduct);
      } else {
        setError("Product not found");
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Failed to load product"
      );
      console.error("Error loading product:", err);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    if (!price) return "$0.00";
    return `$${parseFloat(price).toFixed(2)}`;
  };

  const handlePurchase = async () => {
    if (!product || product.stock <= 0) return;
    
    if (quantity <= 0 || quantity > product.stock) {
      setError(`Please enter a quantity between 1 and ${product.stock}`);
      return;
    }

    try {
      setOrdering(true);
      setError("");
      setSuccessMessage("");
      
      // Create order
      setSuccessMessage("Creating order...");
      const orderResponse = await orderService.createOrder(product.id, quantity);
      const orderId = orderResponse.data.id;
      
      // Automatically process payment
      setSuccessMessage("Processing payment...");
      await orderService.processPayment(orderId);
      
      setSuccessMessage(`Order #${orderId} created and paid successfully! Redirecting to orders page...`);
      
      // Redirect to orders page after 2 seconds
      setTimeout(() => {
        navigate("/orders");
      }, 2000);
      
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Failed to complete purchase. Please try again."
      );
      console.error("Error during purchase:", err);
    } finally {
      setOrdering(false);
    }
  };

  const handleQuantityChange = (e) => {
    const value = parseInt(e.target.value);
    if (value >= 1 && value <= product.stock) {
      setQuantity(value);
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
            <p className="mt-2">Loading product details...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="home-container">
        <Navbar />
        <div className="container main-content mt-5">
          <div className="alert alert-danger" role="alert">
            {error || "Product not found"}
          </div>
          <button 
            onClick={() => navigate("/")} 
            className="btn btn-primary"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="home-container">
      <Navbar />
      <div className="container main-content">
        <div className="row mt-4 align-items-start">
          {/* Product Image - Left Side */}
          <div className="col-lg-6 col-md-12 mb-4">
            <div className="card product-detail-image-card">
              <div className="product-detail-image-container">
                <img
                  src={product.imageUrl || "/images/default-product.jpg"}
                  alt={product.name}
                  className="product-detail-image"
                  onError={(e) => {
                    e.target.src = "/images/default-product.jpg";
                  }}
                />
              </div>
            </div>
          </div>

          {/* Product Details - Right Side */}
          <div className="col-lg-6 col-md-12">
            <div className="product-detail-info">
              <h1 className="product-detail-title">{product.name}</h1>
              <div className="product-detail-price">
                <strong>{formatPrice(product.price)}</strong>
              </div>

              <div className="product-detail-section mt-4">
                <div className="product-detail-item">
                  <span className="product-detail-label">Stock:</span>
                  <span
                    className={
                      product.stock > 0
                        ? "product-detail-stock in-stock"
                        : "product-detail-stock out-of-stock"
                    }
                  >
                    {product.stock || 0}
                  </span>
                </div>
              </div>

              {/* Quantity Selector */}
              {product.stock > 0 && (
                <div className="quantity-selector mt-4">
                  <label className="quantity-label">Quantity:</label>
                  <div className="quantity-controls">
                    <button
                      className="quantity-btn"
                      onClick={() => setQuantity(Math.max(1, quantity - 1))}
                      disabled={quantity <= 1}
                    >
                      -
                    </button>
                    <input
                      type="number"
                      className="quantity-input"
                      min="1"
                      max={product.stock}
                      value={quantity}
                      onChange={handleQuantityChange}
                    />
                    <button
                      className="quantity-btn"
                      onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}
                      disabled={quantity >= product.stock}
                    >
                      +
                    </button>
                  </div>
                </div>
              )}

              {/* Error and Success Messages */}
              {error && (
                <div className="alert alert-danger mt-3" role="alert">
                  {error}
                </div>
              )}
              {successMessage && (
                <div className="alert alert-success mt-3" role="alert">
                  {successMessage}
                </div>
              )}

              {/* Payment Button - Below Content */}
              {product.stock > 0 ? (
                <button
                  className="payment-button mt-5"
                  onClick={handlePurchase}
                  disabled={ordering}
                >
                  {ordering ? "Processing..." : "Purchase Now"}
                </button>
              ) : (
                <button
                  className="payment-button payment-button-disabled"
                  disabled
                >
                  Out of Stock
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;

