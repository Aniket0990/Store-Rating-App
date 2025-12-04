
import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { useNavigate } from "react-router-dom";

export default function OwnerRatings() {
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    const loadRatings = async () => {
      try {
        const data = await apiRequest("/owner/ratings", "GET");
        setRatings(data || []);
      } catch (err) {
        console.error("Error loading ratings:", err);
        setRatings([]);
      } finally {
        setLoading(false);
      }
    };
    loadRatings();
  }, []);

  return (
    <div className="page-content" >
      <button className="btn-view" onClick={() => navigate("/owner/dashboard")}>← Back</button>
      <h1 style={{textAlign:"center"}}>Ratings Received</h1>
      {loading ? (
        <p>Loading…</p>
      ) : ratings.length === 0 ? (
        <p>No ratings yet.</p>
      ) : (
        <div className="list-box">
          {ratings.map(r => (
            <div className="list-item" key={r.id}>
              <strong>{r.userName || "Anonymous"}</strong> — ⭐ {r.score}
              <p><strong>Store:</strong> {r.storeName}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
