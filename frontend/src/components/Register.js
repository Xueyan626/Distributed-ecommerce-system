import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import authService from "../services/authService";
import "../App.css";

const Register = () => {
  const [formData, setFormData] = useState({
    username: "",
    emailAddress: "",
    password: "",
    confirmPassword: "",
    bankAccountNumber: ""
  });
  const [agree, setAgree] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // Validation
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (!agree) {
      setError("Please agree to the User Agreement and Privacy Policy");
      return;
    }

    setLoading(true);

    try {
      const response = await authService.register(
        formData.username,
        formData.password,
        formData.emailAddress,
        formData.bankAccountNumber || "DEFAULT_BANK_001"
      );
      
      if (response.data && response.data.token) {
        // Store token and username
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("username", response.data.username);
        
        // Navigate to home page
        navigate("/");
      } else {
        setError(response.data?.message || "Registration failed");
      }
    } catch (err) {
      setError(
        err.response?.data?.message || 
        err.message || 
        "Registration failed. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h1>Store Application</h1>
          <p className="auth-subtitle">Create your account and start your shopping journey</p>
        </div>

        {error && (
          <div className="auth-error" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              className="form-input"
              placeholder="Please enter your username"
              value={formData.username}
              onChange={handleChange}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="emailAddress">Email</label>
            <input
              type="email"
              id="emailAddress"
              name="emailAddress"
              className="form-input"
              placeholder="Please enter your email"
              value={formData.emailAddress}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              className="form-input"
              placeholder="Please enter your password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              className="form-input"
              placeholder="Please enter your password again"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="bankAccountNumber">Bank Account Number</label>
            <input
              type="text"
              id="bankAccountNumber"
              name="bankAccountNumber"
              className="form-input"
              placeholder="Please enter your bank account number"
              value={formData.bankAccountNumber}
              onChange={handleChange}
            />
          </div>

          <div className="form-agreement">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={agree}
                onChange={(e) => setAgree(e.target.checked)}
              />
              <span>I have read and agree to the</span>
            </label>
            <div className="agreement-links">
              <Link to="#" className="auth-link">User Agreement</Link>
              <span> and </span>
              <Link to="#" className="auth-link">Privacy Policy</Link>
            </div>
          </div>

          <button
            type="submit"
            className="auth-button"
            disabled={loading || !agree}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </form>

        <div className="auth-footer">
          <span>Already have an account?</span>
          <Link to="/login" className="auth-link">
            Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Register;

