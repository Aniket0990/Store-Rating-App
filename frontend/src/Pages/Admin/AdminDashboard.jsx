
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

function AdminDashboard() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [stats, setStats] = useState({ totalUsers: 0, totalStores: 0, totalRatings: 0 });
  const [stores, setStores] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      window.location.href = "/login";
      return;
    }

    let mounted = true;

    const fetchData = async () => {
      try {
        setLoading(true);
        const statsData = await apiRequest("/admin/stats", "GET");
        if (mounted && statsData) setStats(statsData);

        const storesData = await apiRequest("/admin/stores", "GET");
        if (mounted && storesData) setStores(storesData);

        const usersData = await apiRequest("/admin/users", "GET");
        if (mounted && usersData) setUsers(usersData);
      } catch (error) {
        console.error("Error fetching admin data:", error);
      } finally {
        if (mounted) setLoading(false);
      }
    };

    fetchData();
    return () => (mounted = false);
  }, []);

  return (
    <div className="page-layout">

      {/* MOBILE SIDEBAR */}
      <div className={`mobile-sidebar ${menuOpen ? "open" : ""}`}>
        <button className="close-btn" onClick={() => setMenuOpen(false)}>✕</button>
        <div className="sidebar-brand">Admin Panel</div>
        <ul>
          <li><Link to="/admin/stores">Stores</Link></li>
          <li><Link to="/admin/users">Users</Link></li>
        </ul>
      </div>

      <div
        className={`sidebar-overlay ${menuOpen ? "show" : ""}`}
        onClick={() => setMenuOpen(false)}
      ></div>

      {/* NORMAL SIDEBAR */}
      <aside className="app-sidebar">
        <div className="sidebar-brand">Admin Panel</div>
        <ul>
          <li><Link to="/admin/stores">Stores</Link></li>
          <li><Link to="/admin/users">Users</Link></li>
        </ul>
      </aside>

      {/* MAIN */}
      <div className="main-area">

        <div className="app-navbar">
          <button className="mobile-menu-btn" onClick={() => setMenuOpen(true)}>☰</button>
          <div className="brand">🏪 Store Rating App</div>

          <button
            className="btn-logout"
            onClick={() => {
              localStorage.clear();
              window.location.href = "/";
            }}
          >
            Logout
          </button>
        </div>

        <div className="page-content">
          <h1>Welcome 👋</h1>

          <div className="stats-cards">
            <div className="card"><h3>Total Users</h3><p>{stats.totalUsers}</p></div>
            <div className="card"><h3>Total Stores</h3><p>{stats.totalStores}</p></div>
            <div className="card"><h3>Total Ratings</h3><p>{stats.totalRatings}</p></div>
          </div>

          <h3>Stores</h3>
          <div className="list-table-wrapper">
            <table className="list-table">
              <thead>
                <tr><th>Name</th><th>Email</th><th>Address</th><th>Avg Rating</th></tr>
              </thead>
              <tbody>
                {stores.map((s) => (
                  <tr key={s.id}>
                    <td>{s.name}</td>
                    <td>{s.email}</td>
                    <td>{s.address}</td>
                    <td>{s.avgRating?.toFixed(2) ?? 0}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <h3>Users</h3>
          <div className="list-table-wrapper">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Name</th><th>Email</th><th>Address</th>
                  <th>Role</th><th>Rating (Owner)</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.name}</td>
                    <td>{u.email}</td>
                    <td>{u.address}</td>
                    <td>{u.role}</td>
                    <td>{u.role === "OWNER" ? (u.avgRating?.toFixed(2) ?? 0) : "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
