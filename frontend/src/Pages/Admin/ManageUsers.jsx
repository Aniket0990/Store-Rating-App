
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

function ManageUsers() {
  const [users, setUsers] = useState([]);
  const [q, setQ] = useState("");
  const navigate = useNavigate();

  const loggedUser = (() => {
    try {
      return JSON.parse(localStorage.getItem("user"));
    } catch {
      return null;
    }
  })();

  const loadUsers = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in localStorage");
      window.location.href = "/login";
      return;
    }
    console.log("Token found:", token);

    try {
      const data = await apiRequest("/admin/users", "GET");
      console.log("Users data received:", data);
      setUsers(data || []);
    } catch (err) {
      console.error("Error loading users:", err);
      if (err.message.includes("401")) {
        alert("Unauthorized! Please login again.");
        window.location.href = "/login";
      }
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;
    try {
      await apiRequest(`/admin/users/${id}`, "DELETE");
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } catch (err) {
      console.error("Error deleting user:", err);
      alert(err.message || "Failed to delete user");
    }
  };

  const lower = (s) => (s ? String(s).toLowerCase() : "");
  const filtered = users.filter((u) => {
    const term = q.trim().toLowerCase();
    if (!term) return true;
    return (
      lower(u.name).includes(term) ||
      lower(u.email).includes(term) ||
      lower(u.address).includes(term) ||
      lower(u.role).includes(term)
    );
  });

  return (
    <div className="page-content">
      <button className="btn-view" onClick={() => navigate("/admin/dashboard")}>←Back</button>
      <div className="actions">
        <h1>Manage Users</h1>
        <input
          className="search-input"
          placeholder="Search by name, email, address or role"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />

      </div>
      {loggedUser?.role === "ADMIN" && (
        <button className="btn-view" onClick={() => navigate("/admin/add-user")}>
          + Add User
        </button>
      )}

      <div className="table-container">
        <table className="list-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Address</th>
              <th>Role</th>
              <th style={{ width: 180 }}>Actions</th>
            </tr>
          </thead>

          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: "center", padding: 16 }}>
                  No users found
                </td>
              </tr>
            ) : (
              filtered.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.name}</td>
                  <td>{u.email}</td>
                  <td>{u.address}</td>
                  <td>{u.role}</td>
                  <td>
                    <button className="btn-view" onClick={() => navigate(`/admin/users/${u.id}`)}>
                      View
                    </button>
                    {loggedUser?.role === "ADMIN" && (
                      <button
                        className="btn-delete"
                        style={{ marginLeft: 8 }}
                        onClick={() => handleDelete(u.id)}
                      >
                        Delete
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ManageUsers;
