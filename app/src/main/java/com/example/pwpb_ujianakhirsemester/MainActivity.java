package com.example.pwpb_ujianakhirsemester;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnUserActionListener{
    FloatingActionButton buttonAdd;
    DatabaseReference databaseData;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Data> Datalist = new ArrayList<>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        recyclerView=findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        databaseData = FirebaseDatabase.getInstance().getReference("Data");
        buttonAdd = (FloatingActionButton) findViewById(R.id.add);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(context,InputDataActivity.class);
                startActivity(move);
            }

        });
        readData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readData();
    }

    private void readData(){
        databaseData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Datalist.clear();
                for (DataSnapshot dataSnapShot : dataSnapshot.getChildren()){
                    Data data = dataSnapShot.getValue(Data.class);
                    Datalist.add(data);
                }
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this,MainActivity.this,Datalist);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onUserAction(final Data data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pilihan")
                .setPositiveButton("Edit Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent updatedata = new Intent(context, InputDataActivity.class);
                        updatedata.putExtra(new InputDataActivity().UPDATE_INTENT,(Parcelable) data);
                        updatedata.putExtra(new InputDataActivity().UPDATE_ACTION, "Edit");
                        startActivity(updatedata);
                    }
                })
                .setNegativeButton("Hapus Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = data.getDataid();
                        DatabaseReference databaseData = FirebaseDatabase.getInstance().getReference("Data").child(id);
                        databaseData.removeValue();
                        onStart();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
