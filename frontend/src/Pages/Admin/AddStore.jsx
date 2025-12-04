
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import apiRequest from "../../api";
import "../../Styles/App.css";

function AddStore() {
  const navigate = useNavigate();

  const [owners, setOwners] = useState([]);
  const [store, setStore] = useState({
    name: "",
    email: "",
    address: "",
    description: "",
    ownerId: "",
  });

  const handleChange = (e) => {
    setStore({ ...store, [e.target.name]: e.target.value });
  };

  const loadOwners = async () => {
    try {
      const data = await apiRequest("/admin/owners", "GET");
      setOwners(data);
    } catch (err) {
      console.error("Failed to load owners", err);
    }
  };

  useEffect(() => {
    loadOwners();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiRequest("/admin/add-store", "POST", store);
      alert("Store added successfully!");
      navigate("/admin/stores");
    } catch (err) {
      console.error("Error adding store:", err);
      alert(err.message || "Failed to add store");
    }
  };

  return (
    <div className="page-content">
      <div className="add-store-container">
        <h1>Add New Store</h1>

        <form className="add-store-form" onSubmit={handleSubmit}>
          <input
            name="name"
            placeholder="Store Name"
            value={store.name}
            onChange={handleChange}
            required />

          <input
            name="email"
            placeholder="Store Email"
            value={store.email}
            onChange={handleChange}
            required />
          <input
            name="address"
            placeholder="Store Address"
            value={store.address}
            onChange={handleChange}
            required />
          <textarea
            placeholder="Store Description"
            name="description"
            value={store.description}
            onChange={handleChange}
            required />
          {/* Owner Selection */}
          <select
            name="ownerId"
            value={store.ownerId}
            onChange={handleChange}
            required
            className="rating-select">
            <option value="">Select Owner</option>
            {owners.map((owner) => (
              <option value={owner.id} key={owner.id}>
                {owner.name} ({owner.email})
              </option>
            ))}
          </select>
          <button className="btn-view"
            type="submit">
            Add Store
          </button>
          <button
            className="btn-delete"
            type="button"
            onClick={() => navigate("/admin/stores")}
          >
            Cancel
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddStore;
