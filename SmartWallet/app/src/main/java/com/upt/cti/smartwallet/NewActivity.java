package com.upt.cti.smartwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity {
    private TextView tStatus;
    private Button bPrevious, bNext;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();
    private PaymentAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_activity);

        tStatus = (TextView) findViewById(R.id.ttStatus);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        listPayments = (ListView) findViewById(R.id.listPayments);

        bPrevious.setOnClickListener(v -> onPrev());
        bNext.setOnClickListener(v -> onNext());
        fabAdd.setOnClickListener(v -> onFab());

        // setup firebase
        addPayments(p -> {
            adapter = new PaymentAdapter(this, R.layout.item_payment, p);
            listPayments.setAdapter(adapter);
            tStatus.setText("Found " + payments.size() + " in the DB");
            listPayments.setOnItemClickListener((parent, view, position, id) -> {
                AppState.get().setCurrentPayment(payments.get(position));
                AppState.get().setDatabaseReference(databaseReference);
                startActivity(new Intent(getApplicationContext(), AddPaymentActivity.class));
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addPayments(FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {

                    Payment payment = snapshot.getValue(Payment.class);
                    payment.setTimestamp(snapshot.getKey());
                    payments.add(payment);
                    callback.onCallBack(payments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < payments.size(); i++) {
                    if (payments.get(i).timestamp.equals(snapshot.getKey().toString()))
                        try {
                            Payment updatePayment = snapshot.getValue(Payment.class);
                            updatePayment.setTimestamp(snapshot.getKey());

                            payments.set(i, updatePayment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < payments.size(); i++) {
                    if (payments.get(i).timestamp.equals(snapshot.getValue(Payment.class).timestamp))
                        payments.remove(i);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onPrev() {
        Toast.makeText(this, "Previous pressed", Toast.LENGTH_SHORT).show();
    }

    public void onNext() {
        Toast.makeText(this, "Next pressed", Toast.LENGTH_SHORT).show();
    }

    public void onFab() {
        AppState.get().setCurrentPayment(null);
        startActivity(new Intent(this, AddPaymentActivity.class));
    }

    private interface FirebaseCallback {
        void onCallBack(List<Payment> p);
    }
}
