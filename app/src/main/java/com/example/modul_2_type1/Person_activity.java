package com.example.modul_2_type1;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Person_activity extends AppCompatActivity {

    private EditText editTextAge;
    private EditText editTextHeight;
    private EditText editTextWeight;
    private EditText editTextGender;
    private EditText editTextDistanceGoal;
    private double distanceGoal = 0.0; // Переменная для хранения пройденной дистанции
    private String filePath ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_data_layout);
        filePath = getApplicationContext().getFilesDir() + "/person.json";
        editTextAge = findViewById(R.id.editTextAge);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextGender = findViewById(R.id.editTextGender);
        editTextDistanceGoal = findViewById(R.id.editTextDistanceGoal);


        Button saveButton = findViewById(R.id.buttonSavePhysicalData);
        Button loadButton = findViewById(R.id.buttonLoadData);
        Button updateButton = findViewById(R.id.buttonUpdateData);
        Button deleteButton = findViewById(R.id.buttonDeleteData);

        updateButton.setOnClickListener(view -> updateData());
        deleteButton.setOnClickListener(view -> deleteData());
        loadButton.setOnClickListener(view -> loadData());
        if (isFileEmpty(filePath)) {
            saveButton.setOnClickListener(view -> saveData());
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);

        } else {
            saveButton.setVisibility(View.GONE);
            updateButton.setOnClickListener(view -> updateData());
            deleteButton.setOnClickListener(view -> deleteData());
        }
    }

    private void saveData() {
        int age = Integer.parseInt(editTextAge.getText().toString());
        int height = Integer.parseInt(editTextHeight.getText().toString());
        int weight = Integer.parseInt(editTextWeight.getText().toString());
        int distanceGoal = Integer.parseInt(editTextDistanceGoal.getText().toString());
        String gender=editTextGender.getText().toString();
        Person person = new Person(age, height, weight,gender,distanceGoal);
        try {
            LoadDataJson.writeToJSON(person, filePath);
            Toast.makeText(getApplicationContext(), "Your data was  save", Toast.LENGTH_SHORT).show();
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), "Your data was not save", Toast.LENGTH_SHORT).show();

        }
        finish();
        // Запускаем новую активность, если необходимо
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loadData() {

                if(!isFileEmpty(filePath)) {
                    Person person = LoadDataJson.readFromJSON(filePath);

                    if (person != null) {
                        editTextAge.setText(String.valueOf(person.getAge()));
                        editTextHeight.setText(String.valueOf(person.getHeight()));
                        editTextWeight.setText(String.valueOf(person.getWeight()));
                        editTextGender.setText(person.getGender());
                    } else {
                        editTextAge.setText(String.valueOf(0));
                        editTextHeight.setText(String.valueOf(0));
                        editTextWeight.setText(String.valueOf(0));
                        editTextGender.setText("Noname");
                        editTextDistanceGoal.setText("0");
                    }
                    Log.d("loadData: ", person.getGender());
                }
    }


    private void updateData() {
        int age = Integer.parseInt(editTextAge.getText().toString());
        int height = Integer.parseInt(editTextHeight.getText().toString());
        int weight = Integer.parseInt(editTextWeight.getText().toString());
        int goal = Integer.parseInt(editTextDistanceGoal.getText().toString());
        String gender=editTextWeight.getText().toString();
        Person updatedPerson = new Person(age, height, weight,gender,goal);
        LoadDataJson.updateJSON(filePath, updatedPerson);
        finish();
        // Запускаем новую активность, если необходимо
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteData() {
        LoadDataJson.deleteJSON(filePath);
        // Очистка полей ввода после удаления данных
        editTextAge.setText("");
        editTextHeight.setText("");
        editTextWeight.setText("");
        editTextGender.setText("");
        editTextDistanceGoal.setText("");
        finish();
        // Запускаем новую активность, если необходимо
        Intent intent = new Intent(this, Person_activity.class);
        startActivity(intent);
    }

    // Метод для проверки, пуст ли JSON-файл
    private boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.length() == 0;
    }
}
