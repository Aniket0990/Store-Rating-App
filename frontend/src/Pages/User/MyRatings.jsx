
import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { useNavigate } from "react-router-dom";

export default function MyRatings() {
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadRatings = async () => {
      try {
        const data = await apiRequest("/ratings/user", "GET");
        setRatings(data || []);
      } catch (err) {
        console.error("Error fetching ratings:", err);
        setRatings([]);
      } finally {
        setLoading(false);
      }
    };
    loadRatings();
  }, []);

  if (loading) return <div className="page-content"><p>Loading...</p></div>;

  return (
    <div className="page-content">
        <button className="btn-view" onClick={() => navigate("/user/dashboard")}>← Back</button>
        <h1 style={{textAlign:"center"}}>My Ratings For Each Store</h1>
      {ratings.length === 0 ? (
        <p>You haven't rated any store yet.</p>
      ) : (
        <div className="ratings-list">
          {ratings.map(r => (
            <div key={r.id} className="rating-card">
              <div className="rating-card-header">
                <h4>{r.storeName}</h4>
                <div className="rating-score">⭐ {r.score}</div>
              </div>
              <p><strong>User:</strong> {r.userName}</p>
              <p><strong>Store Email:</strong> {r.storeEmail}</p>
              <p><strong>Store Address:</strong> {r.storeAddress}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
