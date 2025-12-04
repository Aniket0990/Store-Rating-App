
import React, { useState } from "react";
import apiRequest from "../../api";
import { useNavigate } from "react-router-dom";
import "../../Styles/App.css";

function AddUser() {
  const navigate = useNavigate();
  const [user, setUser] = useState({
    name: "",
    email: "",
    address: "",
    role: "USER",
    password: "",
  });

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiRequest("/admin/add-user", "POST", user);
      alert("User added successfully!");
      navigate("/admin/users");
    } catch (err) {
      console.error("Error adding user:", err);
      alert(err.message || "Failed to add user");
    }
  };

  return (
    <div className="page-content">
      <div className="add-store-container">
        <h1>Add New User</h1>

        <form className="add-store-form" onSubmit={handleSubmit}>
          <input
            name="name"
            placeholder="Full name"
            value={user.name}
            onChange={handleChange}
            required
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={user.email}
            onChange={handleChange}
            required
          />
          <input
            name="address"
            placeholder="Address"
            value={user.address}
            onChange={handleChange}
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={user.password}
            onChange={handleChange}
            required
          />
          <select name="role" value={user.role} onChange={handleChange} className="rating-select">
            <option value="USER">USER</option>
            <option value="OWNER">OWNER</option>
            <option value="ADMIN">ADMIN</option>
          </select>

            <button className="btn-view" type="submit">
              Add User
            </button>
            <button
              className="btn-delete"
              type="button"
              onClick={() => navigate("/admin/users")}
            >
              Cancel
            </button>
        </form>
      </div>
    </div>
  );
}

export default AddUser;
