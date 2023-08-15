package com.example.pharmacy_store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharmacy_store.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentStrip extends AppCompatActivity {

    Button payment;
    Button back;
    String PublishableKey = "pk_test_51MPGi4CtZNkPMxHGfgC8RkiVKXgM8JBdbe77y2ucY7hYVGjykSGuAMyM9GGDjJ5dH3FvxLLReGnAkq7GZ0fKQ2Zp00rX43d9DM";
    String SecretKey = "sk_test_51MPGi4CtZNkPMxHG3dUPaRqTwgSMhXqe0SHh6fyHI2mULT3o7m2TC1WHXS4Kt3ewynVUPCAoYG07nzRaX7XRAkCl00uRGeDDFL";
    String CustomerId;
    String Ephericalkey;
    String ClientSecret;
    PaymentSheet paymentSheet;

    String totalPrice;
    String userId;

    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripepay);

        payment = findViewById(R.id.button2);
        back =findViewById(R.id.button4);


        PaymentConfiguration.init(this,PublishableKey);

        Intent intent = getIntent();
        totalPrice = intent.getStringExtra("totalPrice");
        userId = intent.getStringExtra("userId");
        documentId = intent.getStringExtra("documentId");
        Log.d("PaymentStrip", "documentId: " + documentId);


        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);

        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delay execution of paymentFlow() by 2 seconds
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paymentFlow();
                    }
                }, 4000);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deleteOrderDocument(documentId);
                finish();

            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject Object = new JSONObject(response);

                            CustomerId = Object.getString("id");

                            Toast.makeText(PaymentStrip.this,CustomerId, Toast.LENGTH_SHORT).show();

                            getEmphericalKey();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PaymentStrip.this,error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SecretKey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void deleteOrderDocument(String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Specify the collection and document to delete
        firestore.collection("orders")
                .document(documentId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("PaymentStrip", "DocumentSnapshot successfully deleted!");
                        } else {
                            Log.e("PaymentStrip", "Error deleting document", task.getException());
                        }
                    }
                });
    }

    private void paymentFlow() {

        paymentSheet.presentWithPaymentIntent(ClientSecret,new PaymentSheet.Configuration("Alpha Dashit",new PaymentSheet.CustomerConfiguration(

                CustomerId,
                Ephericalkey
        )));
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();



            if (!TextUtils.isEmpty(userId)) {
                deleteCartDocumentsForUser(userId);
                Intent intent = new Intent(this, OrderSucess.class);
                intent.putExtra("documentId", documentId);
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);
                finish();
            }
        }
    }

    private void deleteCartDocumentsForUser(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference cartCollection = firestore.collection("cart");

        cartCollection.whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        for (DocumentSnapshot document : documents) {
                            cartCollection.document(document.getId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document deleted successfully
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle deletion failure
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle query failure
                    }
                });
    }


    private void getEmphericalKey() {


        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject Object = new JSONObject(response);

                            Ephericalkey = Object.getString("id");

                            Toast.makeText(PaymentStrip.this,CustomerId, Toast.LENGTH_SHORT).show();

                            getClientSecret(CustomerId,Ephericalkey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SecretKey);
                header.put("Stripe-Version" ,"2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parms = new HashMap<>();
                parms.put("customer",CustomerId);

                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void getClientSecret(String customerId, String ephericalkey) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject Object = new JSONObject(response);

                            ClientSecret = Object.getString("client_secret");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SecretKey);

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parms = new HashMap<>();
                parms.put("customer",CustomerId);
                parms.put("amount", totalPrice);
                parms.put("currency" , "USD");
                parms.put("automatic_payment_methods[enabled]" ,"true");


                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }
}









