package com.example.materialsmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MaterialManagement extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText nameInput, descriptionInput, quantityInput;
    private RecyclerView materialRecyclerView;
    private MaterialAdapter materialAdapter;
    private List<Material> materialList;

    private static final String COLLECTION_MATERIALS = "materials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_material_management);
        db = FirebaseFirestore.getInstance();
        initializeUI();
        setupRecyclerView();
        loadMaterials();
    }

    private void initializeUI() {
        nameInput = findViewById(R.id.nameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        quantityInput = findViewById(R.id.quantityInput);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addMaterial());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        materialRecyclerView = findViewById(R.id.materialRecyclerView);
        materialList = new ArrayList<>();
        materialAdapter = new MaterialAdapter(materialList, this::editMaterial, this::deleteMaterial);
        materialRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        materialRecyclerView.setAdapter(materialAdapter);
    }

    private void loadMaterials() {
        db.collection(COLLECTION_MATERIALS)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        showToast("Error loading materials");
                        return;
                    }

                    materialList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Material material = doc.toObject(Material.class);
                        material.setId(doc.getId());
                        materialList.add(material);
                    }
                    materialAdapter.notifyDataSetChanged();
                });
    }

    private void addMaterial() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String quantityStr = quantityInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        String id = db.collection(COLLECTION_MATERIALS).document().getId();
        Material material = new Material(id, name, description, quantity);

        db.collection(COLLECTION_MATERIALS)
                .document(id)
                .set(material)
                .addOnSuccessListener(aVoid -> {
                    showToast("Material added successfully");
                    clearInputs();
                })
                .addOnFailureListener(e -> showToast("Error adding material"));
    }

    private void editMaterial(Material material) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.edit_material, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);

        editName.setText(material.getName());
        editDescription.setText(material.getDescription());
        editQuantity.setText(String.valueOf(material.getQuantity()));

        builder.setView(dialogView)
                .setTitle("Edit Material")
                .setPositiveButton("Save", (dialog, which) -> {
                    material.setName(editName.getText().toString());
                    material.setDescription(editDescription.getText().toString());
                    material.setQuantity(Integer.parseInt(editQuantity.getText().toString()));

                    db.collection(COLLECTION_MATERIALS)
                            .document(material.getId())
                            .set(material)
                            .addOnSuccessListener(aVoid -> showToast("Material updated successfully"))
                            .addOnFailureListener(e -> showToast("Error updating material"));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMaterial(Material material) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Material")
                .setMessage("Are you sure you want to delete this material?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection(COLLECTION_MATERIALS)
                            .document(material.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> showToast("Material deleted successfully"))
                            .addOnFailureListener(e -> showToast("Error deleting material"));
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clearInputs() {
        nameInput.setText("");
        descriptionInput.setText("");
        quantityInput.setText("");
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}