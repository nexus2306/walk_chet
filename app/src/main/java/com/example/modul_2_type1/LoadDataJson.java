package com.example.modul_2_type1;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
public class LoadDataJson {

        public static void writeToJSON( Person person,String filePath) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Age", person.getAge());
                jsonObject.put("Height", person.getHeight());
                jsonObject.put("Weight", person.getWeight());
                jsonObject.put("Gender", person.getGender());
                jsonObject.put("Goal", person.getGoal());

                FileWriter fileWriter = new FileWriter(filePath);
                fileWriter.write(jsonObject.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        // Метод для чтения данных из JSON-файла и возврата объекта Person
        public static Person readFromJSON(String filePath) {
            Person person = null;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                int age = jsonObject.getInt("Age");
                int height = jsonObject.getInt("Height");
                int weight = jsonObject.getInt("Weight");
                double goal = jsonObject.getInt("Goal");
                String gender=jsonObject.getString("Gender");
                person = new Person(age, height, weight,gender,goal);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return person;
        }

    public static void updateJSON(String filePath, Person updatedPerson) {
        // Считываем текущие данные из файла
        Person existingPerson = readFromJSON(filePath);

        // Обновляем только те поля, которые необходимо изменить
        if (existingPerson != null) {
            existingPerson.setAge(updatedPerson.getAge());
            existingPerson.setHeight(updatedPerson.getHeight());
            existingPerson.setWeight(updatedPerson.getWeight());
            existingPerson.setGender(updatedPerson.getGender());
            existingPerson.setGoal(updatedPerson.getGoal());

            // Перезаписываем файл с обновленными данными
            writeToJSON(existingPerson,filePath);
        }
    }
    public static void deleteJSON(String filePath) {
        try {
            // Удаление файла
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
