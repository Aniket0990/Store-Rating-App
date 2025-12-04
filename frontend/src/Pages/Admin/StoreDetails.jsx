
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

export default function StoreDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [store, setStore] = useState(null);
  const [avgRating, setAvgRating] = useState(0);
  const [userRating, setUserRating] = useState(null);
  const [ratingValue, setRatingValue] = useState(0);
  const [loading, setLoading] = useState(true);
  const [owners, setOwners] = useState([]);
  const [selectedOwner, setSelectedOwner] = useState("");


  const raw = localStorage.getItem("user");
  const user = raw ? JSON.parse(raw) : null;
  const isAdmin = user?.role === "ADMIN";

  // Decide correct endpoint
  const storeEndpoint = isAdmin
    ? `/admin/stores/${id}`
    : `/stores/${id}`;

  const loadStoreDetails = async () => {
    try {
      const data = await apiRequest(storeEndpoint, "GET");
      setStore(data);
      setSelectedOwner(data?.ownerId ?? "");
      setAvgRating(data.averageRating ?? data.avgRating ?? 0);

    } catch (err) {
      console.error("Failed to load store:", err);
      alert("Store not found or access denied");
      navigate(isAdmin ? "/admin/stores" : "/stores");
    }
  };

  const loadUserRating = async () => {
    if (isAdmin) return; // admin should not load rating
    try {
      const data = await apiRequest(`/ratings/user/${id}`, "GET");
      setUserRating(data?.score || null);
      setRatingValue(data?.score || 0);
    } catch {
      setUserRating(null);
    }
  };

  const submitRating = async () => {
    if (ratingValue < 1 || ratingValue > 5) {
      alert("Rating must be between 1 and 5");
      return;
    }

    try {
      await apiRequest(`/ratings/${id}`, "POST", { score: ratingValue });
      alert("Rating submitted!");
      loadStoreDetails();
      loadUserRating();
    } catch (err) {
      alert(err.message);
    }
  };

  const loadOwners = async () => {
    try {
      const data = await apiRequest("/admin/owners", "GET");
      setOwners(data || []);
    } catch (err) {
      console.error("Failed to load owners:", err);
      setOwners([]);
    }
  };
  const updateOwner = async (ownerId) => {
    try {
      // backend expects PUT /admin/stores/{storeId}/assign-owner with body { ownerId }
      const payload = { ownerId: ownerId ? Number(ownerId) : null };
      await apiRequest(`/admin/stores/${id}/assign-owner`, "PUT", payload);
      alert("Owner updated!");
      await loadStoreDetails(); // refresh store info
    } catch (err) {
      console.error("Failed to update owner:", err);
      alert(err.message || "Failed to update owner");
    }
  };


  useEffect(() => {
    loadStoreDetails();
    if (isAdmin) loadOwners();
    loadUserRating();
    setLoading(false);
  }, []);

  if (!store || loading)
    return <div className="page-content"><p>Loading...</p></div>;

  return (
    <div className="page-content">

      {/* Back Button */}
      <button
        className="btn-view"
        onClick={() => navigate(isAdmin ? "/admin/stores" : "/stores")}
      >
        Back
      </button>

      <div className="store-card">
        <div className="store-card-header">
          <h3>{store.name}</h3>
          <div className="store-rating">
            ⭐ {avgRating.toFixed(1)} / 5
          </div>
        </div>
        {isAdmin && (
          <>
            <select
              className="rating-select"
              value={selectedOwner}
              onChange={(e) => setSelectedOwner(e.target.value)}
              >
              <option value="">Select New Owner</option>
              {owners.map((o) => (
                <option value={o.id} key={o.id}>{o.name}</option>
              ))}
            </select>

            <button
              className="btn-view"
              onClick={() => updateOwner(selectedOwner)}
            >
              Update Owner
            </button>
              <p><strong>Owner:</strong> {store.ownerName  || "No Owner Assigned"}</p>
          </>
        )}
        <p><strong>Email:</strong> {store.email}</p>
        <p><strong>Address:</strong> {store.address}</p>
        {store.description && (
          <p><strong>Description:</strong> {store.description}</p>
        )}
        


        {/* USER ONLY: Rating UI */}
        {!isAdmin && (
          <div className="rating-box">
            <h4>Your Rating</h4>

            <select
              value={ratingValue}
              onChange={(e) => setRatingValue(Number(e.target.value))}
              className="rating-select"
            >
              <option value="0">Select Rating</option>
              {[1, 2, 3, 4, 5].map((n) => (
                <option value={n} key={n}>{n} Star</option>
              ))}
            </select>
            <button className="btn-view" onClick={submitRating}>
              {userRating ? "Update Rating" : "Submit Rating"}
            </button>
          </div>
        )}

        {/* Ratings List */}
        <div style={{ marginTop: 20 }}>
          <h4>All Ratings</h4>
          {store.ratings?.length ? (
            store.ratings.map((r) => (
              <div key={r.id} className="rating-item">
                <div><strong>User:</strong> {r.userName}</div>
                <div><strong>Score:</strong> {r.score}</div>
              </div>
            ))
          ) : (
            <p>No ratings yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}
