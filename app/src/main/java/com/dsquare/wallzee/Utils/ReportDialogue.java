package com.dsquare.wallzee.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dsquare.wallzee.R;


public class ReportDialogue {

    String text = "";
    ReportDialogelistner dialogelistner;
    Context context;
    Activity activity;
    Dialog dialog;

    public ReportDialogue(Context context, Activity activity, ReportDialogelistner dialogelistner) {
        this.context = context;
        this.activity = activity;
        this.dialog = dialog;
        this.dialogelistner = dialogelistner;
    }

    public void showDialogue() {
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_report, null);
        dialog.setContentView(v);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout btnSubmit = v.findViewById(R.id.mbtnYes);
        LinearLayout btnCancle = v.findViewById(R.id.mbtnNo);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        btnSubmit.setOnClickListener(v1 -> {
        });
        btnCancle.setOnClickListener(v1 -> {
            dialog.dismiss();
        });
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = v.findViewById(checkedId);
            text = (String) radioButton.getText();
        });

        btnSubmit.setOnClickListener(v1 -> {
            Log.d("TAG", "showDialogue: TEXT" + text);
            dialogelistner.onSubmitClick(text);
            dialog.dismiss();

        });
        dialog.show();
    }

    public interface ReportDialogelistner {
        public void onSubmitClick(String data);
    }
}

