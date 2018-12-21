package me.nathan3882.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.testingapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LessonSelectActivity extends AppCompatActivity {

    private static Context context;
    private String email;
    private boolean isUpdating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        setContentView(R.layout.activity_lesson_select);
        Bundle extras = getIntent().getExtras();
        setEmail(extras.getString("email"));
        setUpdating(extras.getBoolean("isUpdating"));

        TextView alreadyConfiguredText = findViewById(R.id.headerText);
        String[] everyConfiguredLessonEver = getNewLessonArray();

        String newText;
        if (isUpdating) { //Has previously set, load and show to prevent duplicate
            everyConfiguredLessonEver = getLessonNames(); //10 array size, should be enough
            int length = everyConfiguredLessonEver.length;
            newText = "<html>Bare in mind... you already have " + length + " lessons configured:\n";
            for (int i = 0; i < length; i++) {
                String aLesson = everyConfiguredLessonEver[i];
                if (i != length - 1) { //not the last
                    newText += aLesson + ", ";
                } else if (i == length - 2) { //one before the final, "'aLesson' and FINAL"
                    newText += aLesson + " and ";
                }
            }
            newText += ".</html>";
        } else {
            newText = "<html>Enter your lessons below! One per line</html>";
        }
        alreadyConfiguredText.setText(Util.html(newText));

        Button advanceButton = findViewById(R.id.advanceToTimeDisplayBtn);

        String[] lessons = everyConfiguredLessonEver; //effectively final
        advanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText lessonInput = findViewById(R.id.inputBox);
                String text = lessonInput.getText().toString();

                String splitChar = ", ";

                if (text.contains(splitChar)) {
                    String[] splitted = text.split(splitChar);
                    for (String s : splitted) {
                        Util.addToArray(lessons, s, true);
                    }
                } else {
                    Util.addToArray(lessons, text, true);
                }
                Intent toTimeDisplay = new Intent(getBaseContext(), TimeDisplayActivity.class);
                toTimeDisplay.putExtra("email", getEmail());
                toTimeDisplay.putExtra("wasUpdating", isUpdating());
                toTimeDisplay.putExtra("lessons", lessons);
                startActivity(toTimeDisplay);
            }
        });
    }

    private String[] getNewLessonArray() {
        return new String[10];
    }

    public String[] getLessonNames() { //TODO
        File file = new File(getFilesDir(), "Lesson Names.txt");
        String[] lessons = getNewLessonArray();

        try {
            if (!file.exists()) {
                file.createNewFile();
                return lessons;
            }

            //load existing stored
            BufferedReader bReader = new BufferedReader(new FileReader(file));
            String lessonsSepByComma = bReader.readLine();
            lessons = lessonsSepByComma.split(", ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lessons;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }
}
