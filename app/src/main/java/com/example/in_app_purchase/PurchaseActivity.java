package com.example.in_app_purchase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.in_app_purchase.adapter.MyProductAdapter;
import com.example.in_app_purchase.ultils.BillingClientSetup;

import java.util.Arrays;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient ;
    ConsumeResponseListener listener ;
    Button btn_loadProduct ;
    RecyclerView recyclerView_product ;
    TextView tv_premium ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        tv_premium = findViewById(R.id.text_premium) ;
        btn_loadProduct = findViewById(R.id.btn_loadProduct) ;
        recyclerView_product = findViewById(R.id.recyclerView_product) ;
        setupBillingClient();
        recyclerView_product.setHasFixedSize(true);
        recyclerView_product.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_product.addItemDecoration(new DividerItemDecoration(this,
                new LinearLayoutManager(this).getOrientation()));


        //event
        btn_loadProduct.setOnClickListener(view -> {
            if (billingClient.isReady()){
                SkuDetailsParams params = SkuDetailsParams.newBuilder()
                        .setSkusList(Arrays.asList("jewel_of_time","sword_of_angle")).setType(BillingClient.SkuType.INAPP).build() ;
                billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                            loadProductToRecyclerView(list) ;
                        }else {
                            Toast.makeText(PurchaseActivity.this, "Erro Code "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    private void loadProductToRecyclerView(List<SkuDetails> list) {
        MyProductAdapter adapter = new MyProductAdapter(this,list,billingClient) ;
        recyclerView_product.setAdapter(adapter);
    }

    private void setupBillingClient() {
        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(PurchaseActivity.this,"Consume OK",Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this,this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(PurchaseActivity.this,"Success to connect billing",Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList() ;
                    handleItemAlreadyPurchase(purchases) ;
                }
                else {
                    Toast.makeText(PurchaseActivity.this,"Erro Code : "+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PurchaseActivity.this,"You are disconnect from Billing Service",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemAlreadyPurchase(List<Purchase> purchases) {
        StringBuilder purchasedItem = new StringBuilder(tv_premium.getText()); //empty
        for (Purchase purchase : purchases){
            if (purchase.getSku().equals("")) //Consume Item
            {
                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken())
                        .build() ;
                billingClient.consumeAsync(consumeParams,listener);
            }
            purchasedItem.append("\n"+purchase.getSku())
                    .append("\n") ;
        }
        tv_premium.setText(purchasedItem.toString());
        tv_premium.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
        && list != null){
            handleItemAlreadyPurchase(list);
        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(this, "User has been canceled", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Erro "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }
}