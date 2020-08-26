package com.example.familymapclient2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.familymapclient2.caches.DataCache;
import com.example.familymapclient2.models.*;
import com.example.familymapclient2.tools.ServerProxy;

import java.util.concurrent.ExecutionException;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.AllEventResult;
import results.AllPersonResult;
import results.LoginResult;
import results.RegisterResult;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private EditText hostField;
    private EditText portField;
    private EditText userNameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioGroup genderField;
    private Button registerButton;
    private Button loginButton;
    private RegisterResult regResult;
    private LoginResult loginResult;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Boolean loginEnabled  = checkFieldsForEmptyValuesLogin();
            if(loginEnabled) {
                checkFieldsForEmptyValuesRegister();
            }
            else{
                Button b = getView().findViewById(R.id.registerButton);
                b.setEnabled(false);
            }
        }
    };

    private void checkFieldsForEmptyValuesRegister(){
        Button b = getView().findViewById(R.id.registerButton);

        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();

        if(firstName.equals("") || lastName.equals("") || email.equals("") || (genderField.getCheckedRadioButtonId() == -1)){
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    private Boolean checkFieldsForEmptyValuesLogin(){
        Button sb = getView().findViewById(R.id.signInButton);

        String host = hostField.getText().toString();
        String port = portField.getText().toString();
        String username = userNameField.getText().toString();
        String password = passwordField.getText().toString();

        if(host.equals("")|| port.equals("") || username.equals("")|| password.equals("")){
            sb.setEnabled(false);
            return false;
        } else {
            sb.setEnabled(true);
            return true;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        FillEditFields(view);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success =  signIn();
                if(success){
                    String welcome = "Welcome back " + DataCache.getInstance().getNameOfUser() + "!";
                    Toast.makeText(getActivity(), welcome, Toast.LENGTH_LONG).show();
                    try{
                        ((OnLoginSuccessListener) getActivity()).onSuccessfulLogin();
                    }catch (ClassCastException cce){System.out.println("Cast for Map opening unsuccessful"); }
                } else{
                    Toast.makeText(getActivity(), loginResult.getResultMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success =  register();
                if(success){
                    String welcome = "Welcome " + firstNameField.getText().toString() + " " +
                            lastNameField.getText().toString() + "!";
                    Toast.makeText(getActivity(), welcome, Toast.LENGTH_LONG).show();
                    try{
                        ((OnLoginSuccessListener) getActivity()).onSuccessfulLogin();
                    }catch (ClassCastException cce){ }
                }
                else{
                    Toast.makeText(getActivity(), regResult.getResultMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

    public interface OnLoginSuccessListener{
        public void onSuccessfulLogin();
    }


    private void FillEditFields(View view){
        hostField = view.findViewById(R.id.hostLineField);
        portField = view.findViewById(R.id.serverPortField);
        userNameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        firstNameField = view.findViewById(R.id.firstNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        emailField = view.findViewById(R.id.emailAddressField);
        genderField = view.findViewById(R.id.RadioGroupGender);
        loginButton= view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);

        hostField.addTextChangedListener(mTextWatcher);
        portField.addTextChangedListener(mTextWatcher);
        userNameField.addTextChangedListener(mTextWatcher);
        passwordField.addTextChangedListener(mTextWatcher);
        firstNameField.addTextChangedListener(mTextWatcher);
        lastNameField.addTextChangedListener(mTextWatcher);
        emailField.addTextChangedListener(mTextWatcher);
        genderField.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Boolean loginEnabled  = checkFieldsForEmptyValuesLogin();
                if(loginEnabled) {
                    checkFieldsForEmptyValuesRegister();
                }
                else{
                    Button b = getView().findViewById(R.id.registerButton);
                    b.setEnabled(false);
                }
            }
        });
    }

    private Boolean signIn(){
        LoginRequest request =  new LoginRequest(userNameField.getText().toString(),
                passwordField.getText().toString());
        String host = hostField.getText().toString();
        String port = portField.getText().toString();
        LoginTask loginTask = new LoginTask(request, host, port);
        LoginAsync loginAsync = new LoginAsync();
        LoginResult result = null;
        Boolean loadSuccess = false;
        String authToken;
        try {
            result = loginAsync.execute(loginTask).get();
            if(result != null) {
                if(result.getSuccess()) {
                    authToken = result.getAuth_token();
                    if (authToken != null) {
                        DataCache.getInstance().setAuthToken(authToken);
                        DataCache.getInstance().setUserPersonID(result.getPersonID());
                        DataTask dataTask = new DataTask(authToken, host, port);
                        LoadDataAsync loadDataAsync = new LoadDataAsync();
                        loadSuccess = loadDataAsync.execute(dataTask).get();
                        if(!loadSuccess){
                            loginResult = new LoginResult(loadSuccess, "Loading Error");
                        } else {
                            loginResult = result;
                        }
                    }
                } else{
                    loginResult = new LoginResult(result.getSuccess(), result.getResultMessage());
                    return false;
                }
            }
            else{
                System.out.println("We have a null result issue in login");
            }

        } catch (ExecutionException e) {
            loginResult = new LoginResult(loadSuccess, "Loading Error");
            e.printStackTrace();
        } catch (InterruptedException e) {
            loginResult = new LoginResult(loadSuccess, "Loading Error");
            e.printStackTrace();
        }

        return (result.getSuccess() && loginResult.getSuccess());
    }

    private Boolean register(){
        String gender = null;
        if((genderField.getCheckedRadioButtonId()) == 0){
            gender = "f";
        }
        else{
            gender = "m";
        }

        RegisterRequest request =  new RegisterRequest(userNameField.getText().toString(),
                passwordField.getText().toString(), emailField.getText().toString(),
                firstNameField.getText().toString(), lastNameField.getText().toString(),
                gender);
        String host = hostField.getText().toString();
        String port = portField.getText().toString();
        RegisterTask registerTask = new RegisterTask(request, host, port);
        RegisterAsync registerAsync = new RegisterAsync();
        RegisterResult result = null;
        Boolean loadSuccess = false;
        String authToken = null;

        try {
            result = registerAsync.execute(registerTask).get();
            authToken = result.getAuthToken();
            if(result.getSuccess()) {
                if (authToken != null) {
                    DataCache.getInstance().setAuthToken(authToken);
                    DataCache.getInstance().setUserPersonID(result.getPersonID());
                    DataTask dataTask = new DataTask(authToken, host, port);
                    LoadDataAsync loadDataAsync = new LoadDataAsync();
                    loadSuccess = loadDataAsync.execute(dataTask).get();
                    if(!loadSuccess){
                        regResult = new RegisterResult(loadSuccess, "Loading Error");
                    } else {
                        regResult = result;
                    }
                }
            }else{
                regResult = new RegisterResult(result.getSuccess(), result.getResultMessage());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            regResult = new RegisterResult(loadSuccess, "Loading Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
            regResult = new RegisterResult(loadSuccess, "Loading Error");
        }
        return (result.getSuccess() && regResult.getSuccess());
    }

    class LoginAsync extends AsyncTask<LoginTask, Void, LoginResult> {

        @Override
        protected LoginResult doInBackground(LoginTask... loginTasks) {

            ServerProxy proxy = new ServerProxy(loginTasks[0].getServerHost(), loginTasks[0].getServerPort());
            return proxy.login(loginTasks[0].getRequest());
        }
    }

    class RegisterAsync extends AsyncTask<RegisterTask, Void, RegisterResult> {

        @Override
        protected RegisterResult doInBackground(RegisterTask... registerTasks) {
            ServerProxy proxy = new ServerProxy(registerTasks[0].getServerHost(), registerTasks[0].getServerPort());
            RegisterResult result = proxy.register(registerTasks[0].getRequest());
            return result;
        }
    }

    class LoadDataAsync extends AsyncTask<DataTask, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataTask... dataTasks) {
            ServerProxy proxy = new ServerProxy(dataTasks[0].getServerHost(), dataTasks[0].getServerPort());
            AllEventResult eventsResult = proxy.getEvents(dataTasks[0].getAuthToken());
            AllPersonResult personsResult = proxy.getPeople(dataTasks[0].getAuthToken());
            DataCache.getInstance().setEvents(eventsResult.getEvents());
            DataCache.getInstance().setPersons(personsResult.getPersonList());

            if(personsResult.getSuccess() && eventsResult.getSuccess()){
                return true;
            } else{
                return false;
            }

        }
    }
}
