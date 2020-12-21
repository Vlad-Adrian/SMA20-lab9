package com.upt.cti.smartwallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.model.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FabACtivity extends AppCompatActivity {
    private TextView cost, name, type;
    private DatabaseReference databaseReference;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fab_activity_layout);
        cost = findViewById(R.id.costView);
        name = findViewById(R.id.nameView);
        type = findViewById(R.id.typeView);
        Button button = findViewById(R.id.saveButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        button.setOnClickListener(v -> updateDb());
    }

    private void updateDb() {
        if (cost.getText().toString().isEmpty() || name.getText().toString().isEmpty() || type.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please complete all fields in order to update", Toast.LENGTH_SHORT);
        } else {
            String time = getCurrentTimeDate();
            Double c = Double.parseDouble(cost.getText().toString());
            String n = name.getText().toString();
            String t = type.getText().toString();
            databaseReference.child("wallet").child(time).setValue(new Payment(c, n, t));
            this.finish();
        }
    }

    private static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }
}
