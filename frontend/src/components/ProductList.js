import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import productService from "../services/productService";
import Navbar from "./Navbar";
import "../App.css";

const ProductList = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      setError("");
      const response = await productService.getAllProducts();
      setProducts(response.data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Failed to load products"
      );
      console.error("Error loading products:", err);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    if (!price) return "$0.00";
    return `$${parseFloat(price).toFixed(2)}`;
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
            <p className="mt-2">Loading products...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="home-container">
      <Navbar />
      <div className="container main-content">
        <div className="row">
          <div className="col-12">
            <h2 className="section-title mb-4">Browse Products</h2>
            
            {error && (
              <div className="alert alert-danger" role="alert">
                {error}
              </div>
            )}

            {products.length === 0 && !error ? (
              <div className="alert alert-info" role="alert">
                No products available at the moment.
              </div>
            ) : (
              <div className="row">
                <div className="col-12 mb-3">
                  <h4>
                    Popular Products{" "}
                    <span style={{ color: "red" }}>Top {products.length}</span>
                    {products.length > 10 && (
                      <Link to="#" className="text-decoration-none float-end">
                        See All →
                      </Link>
                    )}
                  </h4>
                </div>
                
                {/* Product Cards - Grid Layout */}
                {products.map((product) => (
                  <div
                    key={product.id}
                    className="col-lg-3 col-md-4 col-sm-6 mb-4"
                    onClick={() => navigate(`/products/${product.id}`)}
                    style={{ cursor: "pointer" }}
                  >
                    <div className="product-card">
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
                        <h5 className="product-title">{product.name || "Unnamed Product"}</h5>
                        <div className="product-info">
                          <div className="product-price">
                            <strong>{formatPrice(product.price)}</strong>
                          </div>
                          <div className="product-stock">
                            <span className="stock-label">Stock:</span>{" "}
                            <span className={product.stock > 0 ? "stock-available" : "stock-out"}>
                              {product.stock || 0} {product.stock === 1 ? "item" : "items"}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Table Layout */}
            {products.length > 0 && (
              <div className="row mt-5">
                <div className="col-12">
                  <h4>Product Table</h4>
                  <div className="table-responsive">
                    <table className="table table-striped table-hover">
                      <thead className="table-dark">
                        <tr>
                          <th>ID</th>
                          <th>Image</th>
                          <th>Product Name</th>
                          <th>Price</th>
                          <th>Stock</th>
                        </tr>
                      </thead>
                      <tbody>
                        {products.map((product) => (
                          <tr key={product.id}>
                            <td>{product.id}</td>
                            <td>
                              <img
                                src={product.imageUrl || "/images/default-product.jpg"}
                                alt={product.name}
                                style={{ width: "50px", height: "50px", objectFit: "cover" }}
                                onError={(e) => {
                                  e.target.src = "/images/default-product.jpg";
                                }}
                              />
                            </td>
                            <td>{product.name || "Unnamed Product"}</td>
                            <td>{formatPrice(product.price)}</td>
                            <td>
                              <span className={product.stock > 0 ? "text-success" : "text-danger"}>
                                {product.stock || 0}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductList;

