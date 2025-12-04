
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

function UserDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [ownerStores, setOwnerStores] = useState([]);

  const loadUser = async () => {
    try {
      const data = await apiRequest(`/admin/users/${id}`, "GET");
      setUser(data);
    } catch (err) {
      console.error("Error loading user:", err);
      alert(err.message || "Failed to load user");
      navigate("/admin/users");
    }
  };
  const loadOwnerStores = async () => {
    try {
      const data = await apiRequest(`/admin/owners/${id}/stores`, "GET");
      setOwnerStores(data);
    } catch (err) {
      console.error("Failed to load owner stores:", err);
    }
  };

  useEffect(() => {
  const fetchUser = async () => {
    await loadUser();
  };
  fetchUser();
}, [id]);

useEffect(() => {
  if (user?.role === "OWNER") {
    loadOwnerStores();
  }
}, [user]);


  if (!user) return <div className="page-content">Loading user...</div>;

  return (
    
    <div className="page-content">
      <div style={{ marginBottom: 12 }}>
      <button className="btn-view" onClick={() => navigate("/admin/users")}>
        Back
      </button>
        </div>
      <div className="store-card">
        <h2>User Details</h2>

        <div className="store-meta">
          <div><strong>ID:</strong> {user.id}</div>
        </div>

        <div className="store-desc">
          <div><strong>Name:</strong> {user.name}</div>
          <div><strong>Email:</strong> {user.email}</div>
          <div><strong>Address:</strong> {user.address}</div>
          <div><strong>Role:</strong> {user.role}</div>
          {user.role === "OWNER" && (
            <div><strong>Average Rating:</strong> {user.avgRating.toFixed(2)}</div>
          )}
        </div>
        {user?.role === "OWNER" && (
          <div className="owner-stores-box">
            <h3>Owner's Stores</h3>

            {ownerStores.length === 0 ? (
              <p>No stores assigned</p>
            ) : (
              ownerStores.map((s) => (
                <div key={s.id} className="owner-store-item">
                  <p><strong>Store Name:</strong> {s.name}</p>
                  <p><strong>Avg Rating:</strong> ⭐ {s.avgRating.toFixed(1)}</p>
                </div>
              ))
            )}
          </div>
        )}
        
      </div>
    </div>
  );
}

export default UserDetails;
