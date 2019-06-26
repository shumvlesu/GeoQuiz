package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;

    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    //Массив в котором хранится состояние блокировки или разблокировки кнопок ответов
    //Если пользователь нажал ответ то кнопка должна быть неактивна.
    private boolean[] stateQuestion = new boolean[mQuestionBank.length];

    //Счетчик правильных ответов
    private int correctAnswerCounter = 0;

    //Счетчик вопросов
    private int questionCounter = mQuestionBank.length;

    private int mCurrentIndex = 0;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_Q = "indexStateQuestion";
    private static final String KEY_QC = "index_questionCounter";
    private static final String KEY_QAC = "index_correctAnswerCounter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Если активити был воссоздан после уничтожения, востанавливаем значение что хранилось в mCurrentIndex на тот момент.
        if (savedInstanceState != null) {

            stateQuestion = savedInstanceState.getBooleanArray(KEY_Q);
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            questionCounter = savedInstanceState.getInt(KEY_QC, 0);
            correctAnswerCounter = savedInstanceState.getInt(KEY_QAC, 0);

        } else {
            for (int i = 0; i < stateQuestion.length; i++) {
                stateQuestion[i] = true;
            }
        }

               //int question = mQuestionBank[mCurrentIndex].getTextResId();
        //mQuestionTextView.setText(question);


        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast messToast = Toast.makeText(QuizActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
                //messToast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                //messToast.show();
                checkAnswer(true);

            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast messToast = Toast.makeText(QuizActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
                //messToast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
                //messToast.show();
                checkAnswer(false);

            }
        });


        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                //updateQuestion();
                nextQuestion();
            }
        });

        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex > 0) {
                    mCurrentIndex = (mCurrentIndex - 1);
                } else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CheatActivity
                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                startActivity(intent);
            }
        });


        //При нажатии на надпись (где вопрос написан)
        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });


        updateQuestion();


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    //При уничтожении (повороте экрана) сохраняем mCurrentIndex в savedInstanceState
    //Сохраняются только примитивные типы  и  классы,  реализующие  интерфейс  Serializable  или  Parcelable
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_Q, stateQuestion);
        savedInstanceState.putInt(KEY_QC, questionCounter);
        savedInstanceState.putInt(KEY_QAC, correctAnswerCounter);

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


    public void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
    }

    //обновляем вопрос
    private void updateQuestion() {
        //Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        getButtonState();
    }

    //проверка на правильность ответа
    private void checkAnswer(boolean uuserPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (uuserPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            correctAnswerCounter++;
        } else {
            messageResId = R.string.incorrect_toast;
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        setButtonState();
        getButtonState();
        finalMessage();

    }

    //Как только ответ на вопрос получен запрещаем нажимать на кнопку в этом вопросе
    private void setButtonState() {
        stateQuestion[mCurrentIndex] = false;
        //mFalseButton.setClickable(false);
        //mTrueButton.setClickable(false);
    }

    private void getButtonState() {
        //mFalseButton.setClickable(stateQuestion[mCurrentIndex]);
        //mTrueButton.setClickable(stateQuestion[mCurrentIndex]);
        mFalseButton.setEnabled(stateQuestion[mCurrentIndex]);
        mTrueButton.setEnabled(stateQuestion[mCurrentIndex]);
    }

    //Метод проверяет что на все вопросы дан ответ и выводит сообщение с результатом
    private void finalMessage() {
        questionCounter--;
        if (questionCounter == 0) {

            mNextButton.setVisibility(View.GONE);
            mPreviousButton.setVisibility(View.GONE);
            //mQuestionTextView.setEnabled(false);


            String fMessage = getResources().getString(R.string.finalMessage);
            int procent = ((correctAnswerCounter * 100) / mQuestionBank.length);
            mQuestionTextView.setText(fMessage + " " + Integer.toString(procent) + "%");
            Toast.makeText(this, fMessage + " " + Integer.toString(procent) + "%", Toast.LENGTH_LONG).show();

        }
    }

}
