
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../styles/App.css";
import InputField from "../../Components/InputField";

export default function Signup() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    address: "",
    password: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      await apiRequest("/auth/register", "POST", form);
      alert("Signup successful! Please login.");
      navigate("/");
    } catch (error) {
      setError("Signup failed — check console for details");
      console.error(error);
    }
  };

  return (
    <div className="welcome">
      <h1>Welcome to Store Rating App</h1>
    <div className="auth-container">
      <h2>Signup</h2>
      <form onSubmit={handleSubmit} className="auth-form">
        <InputField type="text" name="name" placeholder="Name" label="Full Name" value={form.name} onChange={handleChange} required />
        <InputField type="email" name="email" placeholder="Email" label="Email" value={form.email} onChange={handleChange} required />
        <InputField type="text" name="address" placeholder="Address" label="Address" value={form.address} onChange={handleChange} required />
        <InputField name="password" type="password" placeholder="Password" label="Password" value={form.password} onChange={handleChange} required />
        {error && <p className="error">{error}</p>}
        <button type="submit">Signup</button>
      </form>
      <p>
        Already have an account?{" "}
        <span onClick={() => navigate("/")} className="link">
          Login
        </span>
      </p>
    </div>
    </div>
  );
}
