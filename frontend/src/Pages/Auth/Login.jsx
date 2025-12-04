
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";
import InputField from "../../Components/InputField";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const data = await apiRequest("/auth/login", "POST", form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.user.role);
      localStorage.setItem("user", JSON.stringify(data.user));

      alert("Login successful!");
      if (data.user.role === "ADMIN") navigate("/admin/dashboard");
      else if (data.user.role === "OWNER") navigate("/owner/dashboard");
      else navigate("/user/dashboard");

    } catch (error) {
      setError("Login failed — check credentials");
      console.error(error);
    }
  };

  return (
    <div className="welcome">
      <h1>Welcome to Store Rating App</h1>
      <div className="auth-container">
        <h2>Login</h2>
        <form onSubmit={handleSubmit} className="auth-form">
          <InputField type="email" name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
          <InputField type="password" name="password" placeholder="Password" value={form.password} onChange={handleChange} required />
          {error && <p className="error">{error}</p>}
          <button type="submit">Login</button>
        </form>
        <p>
          Don’t have an account? <span onClick={() => navigate("/signup")} className="link">Signup</span>
        </p>
      </div>
    </div>
  );
}
