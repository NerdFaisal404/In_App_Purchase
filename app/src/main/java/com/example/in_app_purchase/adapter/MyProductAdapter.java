package com.example.in_app_purchase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.example.in_app_purchase.R;
import com.example.in_app_purchase.listener.IRecyclerViewListener;

import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {
    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public MyProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext()).inflate(R.layout.layout_product_display, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_product_name.setText(skuDetailsList.get(position).getTitle());
        holder.txt_description.setText(skuDetailsList.get(position).getDescription());
        holder.txt_price.setText(skuDetailsList.get(position).getPrice());

        holder.setListener(new IRecyclerViewListener() {
            @Override
            public void OnClick(View view, int potision) {
                //Launch billing flow
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(potision))
                        .build();
                int response = billingClient.launchBillingFlow(appCompatActivity, billingFlowParams)
                        .getResponseCode();
                switch (response) {
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, "BILLING UNAVAILABLE", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                        Toast.makeText(appCompatActivity, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                        Toast.makeText(appCompatActivity, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                        Toast.makeText(appCompatActivity, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                        Toast.makeText(appCompatActivity, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                        Toast.makeText(appCompatActivity, "SERVICE_TIMEOUT", Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_product_name, txt_price, txt_description;
        IRecyclerViewListener listener;

        public void setListener(IRecyclerViewListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_description = itemView.findViewById(R.id.txt_description);
            itemView.setOnClickListener(view -> {
                listener.OnClick(view, getAdapterPosition());
            });
        }
    }
}
