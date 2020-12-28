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

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
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

public class SubscribeActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    Button btn_loadProduct;
    RecyclerView recyclerView_product;
    TextView tv_premium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        tv_premium = findViewById(R.id.text_premium);
        btn_loadProduct = findViewById(R.id.btn_loadProduct);
        recyclerView_product = findViewById(R.id.recyclerView_product);
        setupBillingClient();
        recyclerView_product.setHasFixedSize(true);
        recyclerView_product.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_product.addItemDecoration(new DividerItemDecoration(this,
                new LinearLayoutManager(this).getOrientation()));

        //event
    }



    private void setupBillingClient() {
        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    tv_premium.setVisibility(View.VISIBLE);
                }
            }
        } ;

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(SubscribeActivity.this, "Success to connect billing", Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if (purchases.size() > 0) {
                        recyclerView_product.setVisibility(View.INVISIBLE);
                        for (Purchase purchase : purchases) {
                            handleItemAlreadyPurchase(purchase);
                        }
                    }
                    else {
                        tv_premium.setVisibility(View.GONE);
                        recyclerView_product.setVisibility(View.VISIBLE);
                        loadAllSubcriberPackage();
                    }
                } else {
                    Toast.makeText(SubscribeActivity.this, "Erro Code : " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SubscribeActivity.this, "You are disconnect from Billing Service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllSubcriberPackage() {
        if (billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("vvip_2020"))
                    .setType(BillingClient.SkuType.SUBS).build() ;
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        MyProductAdapter adapter = new MyProductAdapter(SubscribeActivity.this, list,billingClient);
                        recyclerView_product.setAdapter(adapter);
                    }else {
                        Toast.makeText(SubscribeActivity.this,"Erro "+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else {
            Toast.makeText(SubscribeActivity.this,"Billing not ready",Toast.LENGTH_SHORT).show();
        }
    }

    private void handleItemAlreadyPurchase(Purchase purchases) {
        if (purchases.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchases.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchases.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            } else {
                recyclerView_product.setVisibility(View.GONE);
                tv_premium.setVisibility(View.VISIBLE);
                tv_premium.setText("You are premium !!");
            }
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (Purchase purchase : list) {
                handleItemAlreadyPurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "User has been canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }
}