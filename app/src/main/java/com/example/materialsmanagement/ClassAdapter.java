package com.example.materialsmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private final List<Classe> classes;
    private List<Material> materials;
    private final String username;
    private final ClassActionListener listener;

    public interface ClassActionListener {
        void onEditClick(Classe classe, int position);
        void onDeleteClick(Classe classe, int position);
    }

    public ClassAdapter(List<Classe> classes, List<Material> materials, String username, ClassActionListener listener) {
        this.classes = classes;
        this.materials = materials;
        this.username = username;
        this.listener = listener;
    }

    public void updateMaterials(List<Material> newMaterials) {
        this.materials = new ArrayList<>(newMaterials);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Classe classe = classes.get(position);
        holder.bind(classe, position);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView classNameText;
        private final TextView materialNameText;
        private final TextView userNameText;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameText = itemView.findViewById(R.id.classNameText);
            materialNameText = itemView.findViewById(R.id.materialNameText);
            userNameText = itemView.findViewById(R.id.userNameText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final Classe classe, final int position) {
            classNameText.setText("Class: " + classe.getName());

            StringBuilder materialsText = new StringBuilder("Materials: ");
            if (classe.getMaterialIds() != null && !classe.getMaterialIds().isEmpty() && materials != null) {
                boolean first = true;
                for (String materialId : classe.getMaterialIds()) {
                    for (Material material : materials) {
                        if (material.getId().equals(materialId)) {
                            if (!first) {
                                materialsText.append(", ");
                            }
                            materialsText.append(material.getName());
                            first = false;
                            break;
                        }
                    }
                }
            } else {
                materialsText.append("None");
            }
            materialNameText.setText(materialsText.toString());
            userNameText.setText("User: " + username);

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(classe, position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(classe, position);
                }
            });
        }
    }
}