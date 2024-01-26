package com.example.modul_2_type1;
import static java.lang.Math.floor;
import static java.lang.Math.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private  String filePath;
    private Sensor stepSensor;

    private TextView textViewStepCount;
    private TextView textViewDistance;
    private TextView textviewGoal;
    private TextView textViewCalories;
    private Person person;
    private double Calories;
    private int stepCount = 0; // Переменная для хранения количества шагов
    private double distanceCovered = 0.0; // Переменная для хранения пройденной дистанции


    private static final int PERMISSION_REQUEST_BODY_SENSORS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath = getApplicationContext().getFilesDir() + "/person.json";
        textviewGoal=findViewById(R.id.textViewGoal);
        if (!isFileEmpty(filePath)) {
            person = LoadDataJson.readFromJSON(filePath);
            if (person!=null)
            textviewGoal.setText("Цель :"+String.valueOf(person.getGoal()));
            else textviewGoal.setText("No data");
        }
        else {Toast.makeText(getApplicationContext(), "NOO Data!!!fill the data", Toast.LENGTH_SHORT).show();

        }


        // Привязываем элементы интерфейса к соответствующим переменным

        textViewStepCount = findViewById(R.id.textViewStepCount);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCalories= findViewById(R.id.textViewCalory);
        Button clearButton=findViewById(R.id.buttonClearData);
        Button buttonPersonActivity = findViewById(R.id.buttonPersonActivity);
        buttonPersonActivity.setOnClickListener(view -> openPersonActivity());
        clearButton.setOnClickListener(view->ClearData());
        // Проверка разрешения на использование датчиков
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, PERMISSION_REQUEST_BODY_SENSORS);
        }else{
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        updateUI();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d( "onRequestPermissionsResult: ",String.valueOf(requestCode));
        if (requestCode == PERMISSION_REQUEST_BODY_SENSORS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (sensorManager != null) {
                    stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                    if (stepSensor == null) {
                        Toast.makeText(getApplicationContext(), "Your phone has not a sensor", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "Your phone has  a sensor", Toast.LENGTH_SHORT).show();
                        sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Метод для установки цели дистанции


    // Метод для обновления отображения количества шагов и пройденной дистанции
    @SuppressLint("SuspiciousIndentation")
    private void updateUI() {
        textViewStepCount.setText("Количество шагов: " + stepCount);
        textViewDistance.setText("Пройденная дистанция: " + distanceCovered + "м");
        textViewCalories.setText("Сожжено калорий: "+Calories+" ккал");
        Log.d( "updateUI: ",String.valueOf(stepCount));
        if(person!=null) {
            if(person.getGoal()<=distanceCovered)
            Toast.makeText(getApplicationContext(), "Цель достигнута", Toast.LENGTH_SHORT).show();
        }
    }
    private int previousStepCount = 0;
    // Пример метода, который будет вызываться при изменении количества шагов
    private void onStepCountChange(int newStepCount) {
        Log.d( "onStepCountChange: ",String.valueOf(previousStepCount));
        Log.d( "onStepCountChange: ",String.valueOf(newStepCount));
        int stepsIncrement = newStepCount - previousStepCount; // Определяем прирост шагов
        previousStepCount = newStepCount; // Обновляем предыдущее значение шагов
        // Используем только инкремент шагов для обновления данных

        stepCount += stepsIncrement;
        if (person != null)
            distanceCovered = stepCount * ((person.getHeight() / 100 / 4) + 0.37);// Приращение дистанции на основе шагов
        else openPersonActivity();
            distanceCovered = floor(distanceCovered * 1000) / 1000;
            double bmr = estimateBMR(person.getWeight(), person.getHeight(), person.getAge(), person.getGender());
            if (stepCount != 0)
                Calories = floor(calculateBurnedCalories(bmr, stepCount) * 1000) / 1000; // Приращение калорий на основе шагов
            else Calories = 0;

            saveData();
            updateUI(); // Обновляем отображение на экране


    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                int stepsSinceReboot = (int) event.values[0];
                onStepCountChange(stepsSinceReboot);
                Log.d( "onSensorChanged: ",String.valueOf(stepsSinceReboot));
            }
            else Log.d( "sensorEventListener: ","Null");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Метод, вызываемый при изменении точности датчика
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        if (stepSensor != null) {
            Log.d( "onResume: ",String.valueOf(stepSensor));
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else Log.d( "onResume: ","Null");
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadData();
        if (stepSensor != null) {
            Log.d( "onPause: ",String.valueOf(stepSensor));
            sensorManager.unregisterListener(sensorEventListener);
        }
        else Log.d( "onPause: ","Null");
    }


    public static double estimateBMR(int weight, int height, int age, String gender) {
        double bmr;

        if (gender.equalsIgnoreCase("male")) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        return bmr;
    }

    // Метод для оценки сожженных калорий на основе BMR и времени активности
    public double calculateBurnedCalories(double bmr, int stepCount) {
        double met = 3.5;
        double stepLength = person.getHeight() * 0.75;

        double distance = stepCount * stepLength;
        double burnedCalories = (bmr / 24 * met + 0.04 * distance)/1000;

        // Форматирование результата с тремя знаками после запятой

        return burnedCalories;
    }

    private void ClearData(){
        previousStepCount=stepCount;
        Log.d( "ClearData: ",String.valueOf(previousStepCount));

        distanceCovered=0;
        stepCount=0;
        Calories=0;
        textViewStepCount.setText("Количество шагов: " + stepCount);
        textViewDistance.setText("Пройденная дистанция: " + distanceCovered + " м");
        textViewCalories.setText("Сожжено калорий: "+Calories);
        saveData();
        updateUI();

    }
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("StepCount", stepCount);
        float value1=(float) distanceCovered;
        float value2=(float) Calories;
        editor.putFloat("DistanceCovered", value1);
        editor.putFloat("Calories", value2);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("StepCount", 0);
        distanceCovered = sharedPreferences.getFloat("DistanceCovered", 0.0f);
        Calories = sharedPreferences.getFloat("Calories", 0.0f);
    }
    private boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.length() == 0;
    }
    private void openPersonActivity() {
        Intent intent = new Intent(this, Person_activity.class);
        startActivity(intent);
    }
}

