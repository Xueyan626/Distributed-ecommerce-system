import React from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

const Navbar = () => {
  const navigate = useNavigate();
  const username = authService.getUsername();

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  return (
    <nav className="navbar-custom">
      <div
        className="navbar-brand-custom"
        onClick={() => navigate("/")}
        style={{ cursor: "pointer" }}
      >
        <span className="navbar-icon">🛍️</span>
        <span className="navbar-title">Online Store</span>
      </div>
      <div className="navbar-right">
        <div className="navbar-links">
          <button
            className="navbar-link-btn"
            onClick={() => navigate("/orders")}
          >
            📋 My Orders
          </button>
        </div>
        <div className="navbar-user">
          <span className="user-avatar">
            {username?.charAt(0).toUpperCase() || "U"}
          </span>
          <span className="user-name">{username}</span>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;

