import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import productService from "../services/productService";
import Navbar from "./Navbar";
import "../App.css";

const Home = () => {
  const navigate = useNavigate();
  const [allProducts, setAllProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    loadProducts();
  }, []);

  useEffect(() => {
    // Filter products based on search query
    if (searchQuery.trim() === "") {
      setFilteredProducts(allProducts);
    } else {
      const filtered = allProducts.filter((product) =>
        product.name.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredProducts(filtered);
    }
  }, [searchQuery, allProducts]);

  const loadProducts = async () => {
    try {
      const response = await productService.getAllProducts();
      setAllProducts(response.data);
      setFilteredProducts(response.data);
    } catch (err) {
      console.error("Error loading products:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    // Search is handled by useEffect
  };

  const formatPrice = (price) => {
    if (!price) return "$0.00";
    return `$${parseFloat(price).toFixed(2)}`;
  };

  return (
    <div className="home-container">
      {/* Top Navigation Bar */}
      <Navbar />

      {/* Main Content */}
      <div className="container main-content">
        {/* Search Section */}
        <div className="search-section">
          <h2 className="section-title">Browse Products</h2>
          <form onSubmit={handleSearch} className="search-form">
            <div className="search-input-wrapper">
              <input
                type="text"
                className="search-input"
                placeholder="Search for product name or keyword..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <button type="submit" className="search-button">
                <span className="search-icon">🔍</span>
              </button>
            </div>
          </form>
        </div>

        {/* Products Section */}
        <div className="products-section">

          {loading ? (
            <div className="text-center loading-container">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            </div>
          ) : filteredProducts.length === 0 ? (
            <div className="no-results">
              <p>No products found matching "{searchQuery}"</p>
            </div>
          ) : (
            <div className="products-grid">
              {filteredProducts.map((product) => (
                <div
                  key={product.id}
                  className="product-card"
                  onClick={() => navigate(`/products/${product.id}`)}
                >
                  <div className="product-image-container">
                    <img
                      src={product.imageUrl || "/images/default-product.jpg"}
                      alt={product.name}
                      className="product-image"
                      onError={(e) => {
                        e.target.src = "/images/default-product.jpg";
                      }}
                    />
                  </div>
                  <div className="product-card-body">
                    <h5 className="product-title">{product.name}</h5>
                    <div className="product-info">
                      <div className="product-price">
                        <strong>{formatPrice(product.price)}</strong>
                      </div>
                      <div className="product-stock">
                        <span className="stock-label">Stock:</span>{" "}
                        <span
                          className={
                            product.stock > 0
                              ? "stock-available"
                              : "stock-out"
                          }
                        >
                          {product.stock || 0} items
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;

