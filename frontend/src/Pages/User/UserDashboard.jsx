import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { NavLink } from "react-router-dom";

export default function UserDashboard() {
  const [stats, setStats] = useState({ totalStores: 0, ratedStores: 0 });
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const loadStats = async () => {
      try {
        const data = await apiRequest("/user/dashboard-stats", "GET");
        setStats(data || { totalStores: 0, ratedStores: 0 });
      } catch (err) {
        console.error("Error loading stats:", err);
      }
    };
    loadStats();
  }, []);

  return (
    <div className="page-layout">

      {/* MOBILE SIDEBAR */}
      <div className={`mobile-sidebar ${menuOpen ? "open" : ""}`}>
        <button className="close-btn" onClick={() => setMenuOpen(false)}>✕</button>
        <div className="sidebar-brand">User Panel</div>
        <ul>
          <li><NavLink to="/stores">Stores</NavLink></li>
          <li><NavLink to="/my-ratings">My Ratings</NavLink></li>
          <li><NavLink to="/update-password">Update Password</NavLink></li>
        </ul>
      </div>

      <div
        className={`sidebar-overlay ${menuOpen ? "show" : ""}`}
        onClick={() => setMenuOpen(false)}
      ></div>

      {/* NORMAL SIDEBAR */}
      <aside className="app-sidebar">
        <div className="sidebar-brand">User Panel</div>
        <ul>
          <li><NavLink to="/stores">Stores</NavLink></li>
          <li><NavLink to="/my-ratings">My Ratings</NavLink></li>
          <li><NavLink to="/update-password">Update Password</NavLink></li>
        </ul>
      </aside>

      <div className="main-area">

        <div className="app-navbar">
          <button className="mobile-menu-btn" onClick={() => setMenuOpen(true)}>☰</button>
          <div className="brand">🏪 Store Rating App</div>

          <button
            className="btn-logout"
            onClick={() => {
            localStorage.removeItem("token");
            window.location.href = "/";
            }}>Logout</button>
        </div>

        <div className="page-content">
          <h2>Welcome 👋</h2>

          <div className="stats-cards">
            <div className="card">
              <h3>Total Stores</h3>
              <p>{stats.totalStores}</p>
            </div>

            <div className="card">
              <h3>Stores You Rated</h3>
              <p>{stats.ratedStores}</p>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
