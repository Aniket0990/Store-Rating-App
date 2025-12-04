
import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { useNavigate } from "react-router-dom";

export default function OwnerUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const data = await apiRequest("/owner/users-who-rated", "GET");
        setUsers(data || []);
      } catch (err) {
        console.error("Error loading users:", err);
        setUsers([]);
      } finally {
        setLoading(false);
      }
    };
    loadUsers();
  }, []);

  return (
    <div className="page-content">
      <button className="btn-view" onClick={() => navigate("/owner/dashboard")}>← Back</button>
      <h2 style={{textAlign:"center"}}>Users Who Rated Your Store</h2>
      {loading ? (
        <p>Loading…</p>
      ) : users.length === 0 ? (
        <p>No users have rated your store yet.</p>
      ) : (
        <div className="list-box">
          {users.map(u => (
            <div className="list-item" key={u.id}>
              <strong>{u.name}</strong>
              <p>{u.email}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
