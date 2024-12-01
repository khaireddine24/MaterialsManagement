package com.example.materialsmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClassManagementActivity extends AppCompatActivity implements ClassAdapter.ClassActionListener {
    private FirebaseFirestore db;
    private EditText classNameInput;
    private Spinner materialsSpinner;
    private RecyclerView classRecyclerView;
    private ClassAdapter classAdapter;
    private List<Classe> classList;
    private List<Material> materials;
    private String currentUserName;

    private static final String COLLECTION_CLASSES = "classes";
    private static final String COLLECTION_MATERIALS = "materials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);

        db = FirebaseFirestore.getInstance();
        classList = new ArrayList<>();
        materials = new ArrayList<>();

        initializeUser();
        initializeUI();
        setupRecyclerView();
        loadMaterialsAndClasses();
    }

    private void initializeUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserName = "Guest";

        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                currentUserName = user.getDisplayName();
            } else if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                currentUserName = user.getEmail();
            }

            user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(currentUserName)
                            .build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Display name updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void initializeUI() {
        classNameInput = findViewById(R.id.classNameInput);
        materialsSpinner = findViewById(R.id.materialsSpinner);

        // Initialize spinner with empty adapter
        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>());
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialsSpinner.setAdapter(emptyAdapter);

        Button addClassButton = findViewById(R.id.addClassButton);
        addClassButton.setOnClickListener(v -> addClass());

        Button backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        classRecyclerView = findViewById(R.id.classRecyclerView);
        classAdapter = new ClassAdapter(classList, materials, currentUserName, this);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        classRecyclerView.setAdapter(classAdapter);
        classRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadMaterialsAndClasses() {
        db.collection(COLLECTION_MATERIALS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    materials.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Material material = doc.toObject(Material.class);
                        if (material != null) {
                            material.setId(doc.getId());
                            materials.add(material);
                        }
                    }
                    updateSpinner();
                    loadClasses();
                    setupMaterialsListener();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load materials: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupMaterialsListener() {
        db.collection(COLLECTION_MATERIALS)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error watching materials: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    materials.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Material material = doc.toObject(Material.class);
                            if (material != null) {
                                material.setId(doc.getId());
                                materials.add(material);
                            }
                        }
                    }
                    updateSpinner();
                    if (classAdapter != null) {
                        classAdapter.updateMaterials(materials);
                    }
                });
    }

    private void updateSpinner() {
        if (materialsSpinner != null && !materials.isEmpty()) {
            List<String> materialNames = new ArrayList<>();
            for (Material material : materials) {
                if (material.getName() != null) {
                    materialNames.add(material.getName());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    materialNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            materialsSpinner.setAdapter(adapter);
        }
    }

    private void loadClasses() {
        db.collection(COLLECTION_CLASSES)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error watching classes: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    classList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Classe classe = doc.toObject(Classe.class);
                            if (classe != null) {
                                classe.setId(doc.getId());
                                classList.add(classe);
                            }
                        }
                    }
                    if (classAdapter != null) {
                        classAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addClass() {
        String className = classNameInput.getText().toString().trim();
        Object selectedItem = materialsSpinner.getSelectedItem();

        if (selectedItem == null) {
            Toast.makeText(this, "Please select a material", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedMaterialName = selectedItem.toString();

        if (className.isEmpty()) {
            Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedMaterialIds = new ArrayList<>();
        for (Material material : materials) {
            if (material.getName() != null && material.getName().equals(selectedMaterialName)) {
                selectedMaterialIds.add(material.getId());
                break;
            }
        }

        if (selectedMaterialIds.isEmpty()) {
            Toast.makeText(this, "Selected material not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = db.collection(COLLECTION_CLASSES).document().getId();
        Classe classe = new Classe(id, className, selectedMaterialIds);

        db.collection(COLLECTION_CLASSES)
                .document(id)
                .set(classe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show();
                    classNameInput.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error adding class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onEditClick(Classe classe, int position) {
        if (classe != null) {
            editClass(classe);
        }
    }

    @Override
    public void onDeleteClick(Classe classe, int position) {
        if (classe != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteClass(classe))
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void editClass(Classe classe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.edit_class, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);
        Spinner editMaterialsSpinner = dialogView.findViewById(R.id.materialsSpinner);

        editName.setText(classe.getName());

        // Setup materials spinner
        List<String> materialNames = new ArrayList<>();
        for (Material material : materials) {
            if (material.getName() != null) {
                materialNames.add(material.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                materialNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editMaterialsSpinner.setAdapter(adapter);

        // Set current material selection
        if (classe.getMaterialIds() != null && !classe.getMaterialIds().isEmpty()) {
            String currentMaterialId = classe.getMaterialIds().get(0);
            for (int i = 0; i < materials.size(); i++) {
                Material material = materials.get(i);
                if (material.getId() != null && material.getId().equals(currentMaterialId)) {
                    editMaterialsSpinner.setSelection(i);
                    break;
                }
            }
        }

        builder.setTitle("Edit Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = editName.getText().toString().trim();
                    Object selectedItem = editMaterialsSpinner.getSelectedItem();

                    if (selectedItem == null) {
                        Toast.makeText(this, "Please select a material", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String selectedMaterialName = selectedItem.toString();

                    if (updatedName.isEmpty()) {
                        Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> updatedMaterialIds = new ArrayList<>();
                    for (Material material : materials) {
                        if (material.getName() != null && material.getName().equals(selectedMaterialName)) {
                            updatedMaterialIds.add(material.getId());
                            break;
                        }
                    }

                    if (updatedMaterialIds.isEmpty()) {
                        Toast.makeText(this, "Selected material not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    classe.setName(updatedName);
                    classe.setMaterialIds(updatedMaterialIds);

                    db.collection(COLLECTION_CLASSES)
                            .document(classe.getId())
                            .set(classe)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Class updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error updating class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass(Classe classe) {
        db.collection(COLLECTION_CLASSES)
                .document(classe.getId())
                .delete()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Class deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}