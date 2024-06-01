package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.Admin.UpdateInfoActivity;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.UserModel;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {
    private static List<UserModel> mData;

    public AccountAdapter(List<UserModel> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public AccountAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listacc, parent, false);
        return new AccountAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.MyViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView hovaten, email, quyen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            hovaten = itemView.findViewById(R.id.hovaten);
            email = itemView.findViewById(R.id.email);
            quyen = itemView.findViewById(R.id.quyen);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                UserModel clickedHotel = mData.get(position);
                Intent intent = new Intent(itemView.getContext(), UpdateInfoActivity.class);
                intent.putExtra("Uid", String.valueOf(clickedHotel.getUid()));
                itemView.getContext().startActivity(intent);
            });
        }

        public void bindData(UserModel data) {
            hovaten.setText(data.getName());
            email.setText("Email: " + data.getEmail());
            String role = data.getRole();
            if (role.equals("user")) {
                role = "Người dùng";
            } else if (role.equals("doitac")) {
                role = "Đối tác";
            } else if (role.equals("admin")) {
                role = "Admin";
            } else {
                role = "Người dùng";
            }
            quyen.setText("Quyền: " + role);

        }
    }
}

