package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.List;

import static com.student.ramirez.quizplusplus.Question.IDENTIFICATION_TYPE;
import static com.student.ramirez.quizplusplus.Question.TRUEFALSE_TYPE;
import static com.student.ramirez.quizplusplus.Question.MULTIPLECHOICE_TYPE;

/**
 * Created by Ramirez on 1/27/2018.
 */

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Question> questionList;
    public QuestionAdapterListener questionAdapterListener;
    public QuestionAdapter(List<Question> questionList, QuestionAdapterListener questionAdapterListener) {
        this.questionList = questionList;
        this.questionAdapterListener =questionAdapterListener;
    }
    @Override
    public int getItemViewType(int position) {
        Question question = questionList.get(position);
        Log.e("QuestionPosition", String.valueOf(question));
        if (question != null){
            return question.getType();
        }
        return 0;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.e("QuestionViewType", String.valueOf(viewType));
        switch (viewType){
            case TRUEFALSE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_truefalse, parent, false);
                Log.e("ViewType", "TrueFalse");
                return new TrueFalseViewHolder(view);
            case MULTIPLECHOICE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_multiplechoice, parent, false);
                Log.e("ViewType", "MultipleChoice");
                return new MultipleChoiceViewHolder(view);
            case IDENTIFICATION_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_identification, parent, false);
                Log.e("ViewType", "Identification");
                return new IdentificationViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Question question = questionList.get(position);
        Log.e("QuestionPositionBind", String.valueOf(question));
        switch (question.getType()){
            // USE THIS WHEN A USER CHOOSE TRUE FALSE TYPE
            case TRUEFALSE_TYPE:
                // Delete Action
                ((TrueFalseViewHolder) holder).btnDeleteThisQuestion.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                // Handles the DeleteThisQuestion Button Event
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Confirm");
                                builder.setMessage("Are you sure you want to delete this question?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        questionAdapterListener.DeleteButtonOnClicked(view, holder.getAdapterPosition());
                                        Log.e("TFDeleteButton", "ActionTriggered");
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                        }
                );
                // Text Change Event - used to save data when typing
                ((TrueFalseViewHolder) holder).etQuestion.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setQuestion(s.toString().trim());
                                Log.e("GETQUESTION",  "Position" + holder.getAdapterPosition() + " Question: " + questionList.get(holder.getAdapterPosition()).getQuestion());
                                if (!questionList.get(holder.getAdapterPosition()).getQuestion().isEmpty() && !questionList.get(holder.getAdapterPosition()).getAnswer().isEmpty()){
                                    ((TrueFalseViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#325F92"));
                                }

                            }
                });

                // Get the Answer using Checked Change - if it doesn't change = send error message
                ((TrueFalseViewHolder) holder).rgAnswerTrueFalse.setOnCheckedChangeListener(
                        new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                Log.e("Answer", String.valueOf(checkedId));
                                switch (checkedId){
                                    case R.id.rbAnswerTrue:
                                        questionList.get(holder.getAdapterPosition()).setAnswer("True");
                                        break;
                                    case R.id.rbAnswerFalse:
                                        questionList.get(holder.getAdapterPosition()).setAnswer("False");
                                        break;
                                }
                                // Change the color upon picking / Question not empty
                                if (!questionList.get(holder.getAdapterPosition()).getQuestion().isEmpty()){
                                    ((TrueFalseViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#325F92"));
                                }
                                Log.e("GETANSWER", "Position" +holder.getAdapterPosition() + " Question: " + questionList.get(holder.getAdapterPosition()).getAnswer());
                            }
                        }
                );
                // Default Values
                ((TrueFalseViewHolder) holder).etQuestion.setText(null);
                ((TrueFalseViewHolder) holder).rgAnswerTrueFalse.setVisibility(View.INVISIBLE);
                ((TrueFalseViewHolder) holder).rgAnswerTrueFalse.clearCheck();
                questionList.get(holder.getAdapterPosition()).setAnswer("");
                ((TrueFalseViewHolder) holder).rgAnswerTrueFalse.setVisibility(View.VISIBLE);
                ((TrueFalseViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#923232"));
                break;
            // USE THIS WHEN A USER CHOOSE MULTIPLE CHOICE TYPE
            case MULTIPLECHOICE_TYPE:
                // Text Change Event - used to save data when typing
                ((MultipleChoiceViewHolder) holder).etQuestion.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setQuestion(s.toString().trim());
                                Log.e("GETQUESTION",  "Position" + holder.getAdapterPosition() + " Question: " + questionList.get(holder.getAdapterPosition()).getQuestion());
                                if (!questionList.get(holder.getAdapterPosition()).getQuestion().isEmpty() && !questionList.get(holder.getAdapterPosition()).getAnswer().isEmpty()){
                                    ((MultipleChoiceViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#325F92"));
                                }
                            }
                        }
                );
                // Choice A
                ((MultipleChoiceViewHolder) holder).etChoiceA.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setChoiceA(s.toString().trim());
                                Log.e("GETCHOICEA",  "Position" + holder.getAdapterPosition() + " A = " + questionList.get(holder.getAdapterPosition()).getChoiceA());
                                if (questionList.get(holder.getAdapterPosition()).getChoiceA().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceB().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceC().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceD().isEmpty()){
                                    Log.e("VALIDATEINPUT", "INCOMPLETE INPUT");
                                } else {
                                    ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
                // Choice B
                ((MultipleChoiceViewHolder) holder).etChoiceB.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setChoiceB(s.toString().trim());
                                Log.e("GETCHOICEB",  "Position" + holder.getAdapterPosition() + " B = " + questionList.get(holder.getAdapterPosition()).getChoiceB());
                                if (questionList.get(holder.getAdapterPosition()).getChoiceA().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceB().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceC().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceD().isEmpty()){
                                    Log.e("VALIDATEINPUT", "INCOMPLETE INPUT");
                                } else {
                                    ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
                // Choice C
                ((MultipleChoiceViewHolder) holder).etChoiceC.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setChoiceC(s.toString().trim());
                                Log.e("GETCHOICEC",  "Position" + holder.getAdapterPosition() + " C = " + questionList.get(holder.getAdapterPosition()).getChoiceC());
                                if (questionList.get(holder.getAdapterPosition()).getChoiceA().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceB().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceC().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceD().isEmpty()){
                                    Log.e("VALIDATEINPUT", "INCOMPLETE INPUT");
                                } else {
                                    ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
                // Choice D
                ((MultipleChoiceViewHolder) holder).etChoiceD.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                questionList.get(holder.getAdapterPosition()).setChoiceD(s.toString().trim());
                                Log.e("GETCHOICED",  "Position" + holder.getAdapterPosition() + " C = " + questionList.get(holder.getAdapterPosition()).getChoiceD());
                                if (questionList.get(holder.getAdapterPosition()).getChoiceA().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceB().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceC().isEmpty() || questionList.get(holder.getAdapterPosition()).getChoiceD().isEmpty()){
                                    Log.e("VALIDATEINPUT", "INCOMPLETE INPUT");
                                } else {
                                    ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
                // Get the selected Radio Button - Used to get the Answer of the Question
                ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setOnCheckedChangeListener(
                        new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                Log.e("Answer", String.valueOf(checkedId));
                                switch (checkedId){
                                    case R.id.rbChoiceA:
                                        questionList.get(holder.getAdapterPosition()).setAnswer(questionList.get(holder.getAdapterPosition()).getChoiceA());
                                        break;
                                    case R.id.rbChoiceB:
                                        questionList.get(holder.getAdapterPosition()).setAnswer(questionList.get(holder.getAdapterPosition()).getChoiceB());
                                        break;
                                    case R.id.rbChoiceC:
                                        questionList.get(holder.getAdapterPosition()).setAnswer(questionList.get(holder.getAdapterPosition()).getChoiceC());
                                        break;
                                    case R.id.rbChoiceD:
                                        questionList.get(holder.getAdapterPosition()).setAnswer(questionList.get(holder.getAdapterPosition()).getChoiceD());
                                        break;
                                }
                                // Change the color upon picking / Question not empty
                                if (!questionList.get(holder.getAdapterPosition()).getQuestion().isEmpty()){
                                    ((MultipleChoiceViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#325F92"));
                                }
                                Log.e("GETANSWER", "Position" +holder.getAdapterPosition() + " Question: " + questionList.get(holder.getAdapterPosition()).getAnswer());
                            }
                        }
                );

                // Delete Action
                ((MultipleChoiceViewHolder) holder).btnDeleteThisQuestion.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                // Handles the DeleteThisQuestion Button Event
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Confirm");
                                builder.setMessage("Are you sure you want to delete this question?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("MCDeleteButton", "ActionTriggered");
                                        questionAdapterListener.DeleteButtonOnClicked(view, holder.getAdapterPosition());
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                        }
                );
                // Default Values
                ((MultipleChoiceViewHolder) holder).etQuestion.setText(null);
                ((MultipleChoiceViewHolder) holder).etChoiceA.setText(null);
                ((MultipleChoiceViewHolder) holder).etChoiceB.setText(null);
                ((MultipleChoiceViewHolder) holder).etChoiceC.setText(null);
                ((MultipleChoiceViewHolder) holder).etChoiceD.setText(null);
                ((MultipleChoiceViewHolder) holder).rgAnswerChoices.setVisibility(View.INVISIBLE);
                ((MultipleChoiceViewHolder) holder).rgAnswerChoices.clearCheck();
                ((MultipleChoiceViewHolder) holder).QuestionContainer.setBackgroundColor(Color.parseColor("#923232"));
                break;
            // USE THIS WHEN A USER CHOOSE IDENTIFICATION TYPE
            case IDENTIFICATION_TYPE:
                break;
        }
    }

    class TrueFalseViewHolder extends RecyclerView.ViewHolder {
        EditText etQuestion;
        Button btnDeleteThisQuestion;
        RadioGroup rgAnswerTrueFalse;
        RelativeLayout QuestionContainer;
        public TrueFalseViewHolder(View itemView) {
            super(itemView);
            etQuestion = (EditText) itemView.findViewById(R.id.txtScore);
            btnDeleteThisQuestion = (Button) itemView.findViewById(R.id.btnDeleteThisQuestion);
            rgAnswerTrueFalse = (RadioGroup) itemView.findViewById(R.id.rgAnswerTrueFalse);
            QuestionContainer = (RelativeLayout) itemView.findViewById(R.id.QuestionContainer);
        }
    }
    class MultipleChoiceViewHolder extends RecyclerView.ViewHolder {
        EditText etQuestion;
        Button btnDeleteThisQuestion;
        RadioGroup rgAnswerChoices;
        EditText etChoiceA, etChoiceB, etChoiceC, etChoiceD;
        RelativeLayout QuestionContainer;
        public MultipleChoiceViewHolder(View itemView) {
            super(itemView);
            etQuestion = (EditText) itemView.findViewById(R.id.txtScore);
            btnDeleteThisQuestion = (Button) itemView.findViewById(R.id.btnDeleteThisQuestion);
            rgAnswerChoices = (RadioGroup) itemView.findViewById(R.id.rgAnswerChoices);
            etChoiceA = (EditText) itemView.findViewById(R.id.etChoiceA);
            etChoiceB = (EditText) itemView.findViewById(R.id.etChoiceB);
            etChoiceC = (EditText) itemView.findViewById(R.id.etChoiceC);
            etChoiceD = (EditText) itemView.findViewById(R.id.etChoiceD);
            QuestionContainer = (RelativeLayout) itemView.findViewById(R.id.QuestionContainer);
        }
    }
    class IdentificationViewHolder extends RecyclerView.ViewHolder {
        public IdentificationViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface QuestionAdapterListener {
        void DeleteButtonOnClicked(View view, int position);
    }

    public void removeAt(int position){
        questionList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(questionList.size(), 0);
    }
    @Override
    public int getItemCount() {
        return questionList.size();
    }

}
