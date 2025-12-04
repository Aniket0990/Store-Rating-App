
import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { NavLink, useNavigate } from "react-router-dom";

export default function OwnerDashboard() {
  const [avgRating, setAvgRating] = useState(0);
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const loadAvg = async () => {
      try {
        const data = await apiRequest("/owner/average-rating", "GET");
        setAvgRating(Number(data || 0));
      } catch (err) {
        console.error("Error loading average rating:", err);
        setAvgRating(0);
      }
    };
    loadAvg();
  }, []);

  return (
    <div className="page-layout">

      {/* ▬▬▬ MOBILE SIDEBAR ▬▬▬ */}
      <div className={`mobile-sidebar ${menuOpen ? "open" : ""}`}>
        <button className="close-btn" onClick={() => setMenuOpen(false)}>✕</button>
        <div className="sidebar-brand">Owner Panel</div>
        <ul>
          <li><NavLink to="/owner/stores">Stores</NavLink></li>
          <li><NavLink to="/owner/users">Users</NavLink></li>
          <li><NavLink to="/owner/ratings">Ratings</NavLink></li>
          <li><NavLink to="/owner/update-password">Update Password</NavLink></li>
        </ul>
      </div>

      <div
        className={`sidebar-overlay ${menuOpen ? "show" : ""}`}
        onClick={() => setMenuOpen(false)}
      ></div>

      {/* NORMAL SIDEBAR */}
      <aside className="app-sidebar">
        <div className="sidebar-brand">Owner Panel</div>
        <ul>
          <li><NavLink to="/owner/stores">Stores</NavLink></li>
          <li><NavLink to="/owner/users">Users</NavLink></li>
          <li><NavLink to="/owner/ratings">Ratings</NavLink></li>
          <li><NavLink to="/owner/update-password">Update Password</NavLink></li>
        </ul>
      </aside>

      {/* MAIN AREA */}
      <div className="main-area">

        {/* NAVBAR */}
        <div className="app-navbar">
          <button className="mobile-menu-btn" onClick={() => setMenuOpen(true)}>☰</button>
          <div className="brand">🏪 Store Rating App</div>
          <button
            className="btn-logout"
            onClick={() => {
              localStorage.removeItem("owner");
              window.location.href = "/";
            }}
          >
            Logout
          </button>
        </div>

        <div className="page-content">
          <h2>Welcome 👋</h2>
          <div className="dashboard-card">
            <h3>Your All Stores Average Rating</h3>
            <p className="big-rating">⭐ {avgRating.toFixed(1)}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
