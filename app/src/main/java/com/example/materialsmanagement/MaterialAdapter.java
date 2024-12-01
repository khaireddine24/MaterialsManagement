package com.example.materialsmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private List<Material> materials;
    private OnMaterialEditListener editListener;
    private OnMaterialDeleteListener deleteListener;

    public interface OnMaterialEditListener {
        void onEdit(Material material);
    }

    public interface OnMaterialDeleteListener {
        void onDelete(Material material);
    }

    public MaterialAdapter(List<Material> materials, OnMaterialEditListener editListener, OnMaterialDeleteListener deleteListener) {
        this.materials = materials;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {
        Material material = materials.get(position);
        holder.bind(material);
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, descriptionText, quantityText;
        private ImageButton editButton, deleteButton;

        MaterialViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.materialName);
            descriptionText = itemView.findViewById(R.id.materialDescription);
            quantityText = itemView.findViewById(R.id.materialQuantity);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Material material) {
            nameText.setText(material.getName());
            descriptionText.setText(material.getDescription());
            quantityText.setText(String.valueOf(material.getQuantity()));

            editButton.setOnClickListener(v -> editListener.onEdit(material));
            deleteButton.setOnClickListener(v -> deleteListener.onDelete(material));
        }
    }
}
