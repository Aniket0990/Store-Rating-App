import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { useNavigate } from "react-router-dom";

export default function OwnerStores() {
    const [stores, setStores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const loadStores = async () => {
            try {
                const data = await apiRequest("/owner/stores", "GET"); // only owner stores
                setStores(data || []);
            } catch (err) {
                console.error("Error loading owner stores:", err);
                setStores([]);
            } finally {
                setLoading(false);
            }
        };
        loadStores();
    }, []);

    const filteredStores = stores.filter((s) =>
        s.name.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className="page-content">
            <button className="btn-view" onClick={() => navigate("/owner/dashboard")}>← Back</button>
            <div className="actions" style={{ display: "flex", gap: 20, alignItems: "center" }}>
                <h1 >Your Stores</h1>
                <input
                    className="search-input"
                    placeholder="Search stores..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
            </div>
            {loading ? (
                <p>Loading stores…</p>
            ) : stores.length === 0 ? (
                <p>No stores assigned to you.</p>
            ) : (
                <div className="store-grid">
                    {stores.map((store) => (
                        <div className="store-card" key={store.id}>
                            <div className="store-card-header">
                                <h3>{store.name}</h3>
                                <div>⭐ {store.avgRating?.toFixed(2) || 0}</div>
                            </div>
                            <div className="store-desc">
                                <p><strong>Email:</strong> {store.email}</p>
                                <p><strong>Address:</strong> {store.address}</p>
                                <p><strong>Description:</strong> {store.description}</p>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
