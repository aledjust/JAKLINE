package com.aledgroup.jakline;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by aled on 03/26/2016.
 */
public class Email extends Activity implements  View.OnClickListener {

    //Declaring EditText
    private EditText txtEmail;
    private EditText txtSubject;
    private EditText txtMessage;

    private TextInputLayout inputLayoutEmail,inputLayoutSubject,inputLayoutMessage;

    //Send button
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        //Initializing the views
        //inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_Email);
        inputLayoutSubject = (TextInputLayout) findViewById(R.id.input_layout_Subject);
        inputLayoutMessage = (TextInputLayout) findViewById(R.id.input_layout_Message);

        //txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtSubject = (EditText) findViewById(R.id.txtSubject);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        btnSend = (Button) findViewById(R.id.btnSend);

        //txtEmail.addTextChangedListener(new MyTextWatcher(txtEmail));
        txtSubject.addTextChangedListener(new MyTextWatcher(txtSubject));
        txtMessage.addTextChangedListener(new MyTextWatcher(txtMessage));

        //Adding click listener
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        sendEmail();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void sendEmail() {

//        if (!validateEmail()) {
//            return;
//        }

        if (!validateSubject()) {
            return;
        }

        if (!validateMessage()) {
            return;
        }

        //Getting content for activity_email
        String email = "aled.java@gmail.com";
        String subject = txtSubject.getText().toString().trim();
        String message = txtMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send activity_email
        sm.execute();
    }

    // Validating email
    private boolean validateEmail() {
        String email = txtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validating subject
    private boolean validateSubject() {
        if (txtSubject.getText().toString().trim().isEmpty()) {
            inputLayoutSubject.setError(getString(R.string.err_msg_subject));
            requestFocus(txtSubject);
            return false;
        } else {
            inputLayoutSubject.setErrorEnabled(false);
        }

        return true;
    }

    // Validating subject
    private boolean validateMessage() {
        if (txtMessage.getText().toString().trim().isEmpty()) {
            inputLayoutMessage.setError(getString(R.string.err_msg_message));
            requestFocus(txtMessage);
            return false;
        } else {
            inputLayoutMessage.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
//                case R.id.txtEmail:
//                    validateEmail();
//                    break;
                case R.id.txtSubject:
                    validateSubject();
                    break;
                case R.id.txtMessage:
                    validateMessage();
                    break;
            }
        }
    }
}
