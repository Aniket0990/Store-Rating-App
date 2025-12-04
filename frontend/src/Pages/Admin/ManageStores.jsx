
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

function ManageStores() {
  const [stores, setStores] = useState([]);
  const [q, setQ] = useState("");
  const navigate = useNavigate();

  // Load stores with logging and token check
  const loggedUser = (() => {
    try {
      return JSON.parse(localStorage.getItem("user"));
    } catch {
      return null;
    }
  })();
  const loadStores = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in localStorage");
      window.location.href = "/login";
      return;
    }
    console.log("Token found:", token);

    try {
      const data = await apiRequest("/admin/stores", "GET");
      console.log("Stores data received:", data);
      setStores(data || []);
    } catch (err) {
      console.error("Error loading stores:", err);
      if (err.message.includes("401")) {
        alert("Unauthorized! Please login again.");
        window.location.href = "/login";
      }
    }
  };

  useEffect(() => {
    loadStores();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this store?")) return;
    try {
      await apiRequest(`/admin/stores/${id}`, "DELETE");
      setStores((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      console.error("Error deleting store:", err);
      alert(err.message || "Failed to delete store");
    }
  };

  const lower = (s) => (s ? String(s).toLowerCase() : "");
  const filtered = stores.filter((s) => {
    const term = q.trim().toLowerCase();
    if (!term) return true;
    return (
      lower(s.name).includes(term) ||
      lower(s.email).includes(term) ||
      lower(s.address).includes(term)
    );
  });

  return (
    <div className="page-content">
      <button className="btn-view" onClick={() => navigate("/admin/dashboard")}>←Back</button>
      <div className="actions">
        <h1 >Manage Stores</h1>
        <input
          className="search-input"
          placeholder="Search by name, email, or address"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />

      </div>
      {loggedUser?.role === "ADMIN" && (
            <button className="btn-view" onClick={() => navigate("/admin/add-store")}>
              + Add Store
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
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ textAlign: "center", padding: 16 }}>
                  No stores found
                </td>
              </tr>
            ) : (
              filtered.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td>{s.name}</td>
                  <td>{s.email}</td>
                  <td>{s.address}</td>
                  <td>
                    <button className="btn-view" onClick={() => navigate(`/admin/store-details/${s.id}`)}>
                      View
                    </button>
                    <button
                      className="btn-delete"
                      style={{ marginLeft: 8 }}
                      onClick={() => handleDelete(s.id)}
                    >
                      Delete
                    </button>
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

export default ManageStores;
