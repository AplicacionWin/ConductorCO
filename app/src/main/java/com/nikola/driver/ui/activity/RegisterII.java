package com.nikola.driver.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.nikola.driver.R;
import com.nikola.driver.network.model.SocialMediaProfile;
import com.nikola.driver.network.model.TaxiTypes;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.adapter.SpinnerAdapter;
import com.nikola.driver.ui.adapter.TaxiAdapter;
import com.nikola.driver.utils.AbstractClient;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.ItemClickSupport;
import com.nikola.driver.utils.ParseContent;
import com.nikola.driver.utils.ReadFiles;
import com.nikola.driver.utils.customText.CustomRegularEditView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.AppUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/5/2017.
 */

public class RegisterII extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int PICK_IMAGE_VECHILE = 101;
    private static final int RC_STORAGE_PERM = 1000;
    Unbinder unbinder;
    @BindView(R.id.iv_register_user_icon)
    CircleImageView ivRegisterUserIcon;
    @BindView(R.id.et_register_identificacion)
    CustomRegularEditView etRegisteridentificacion;
    @BindView(R.id.et_register_first_name)
    CustomRegularEditView etRegisterFirstName;
    @BindView(R.id.et_register_last_name)
    CustomRegularEditView etRegisterLastName;
    @BindView(R.id.et_register_your_email)
    CustomRegularEditView etRegisterYourEmail;
    @BindView(R.id.et_register_your_password)
    CustomRegularEditView etRegisterYourPassword;
    @BindView(R.id.btn_male)
    RadioButton btnMale;
    @BindView(R.id.btn_female)
    RadioButton btnFemale;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.sp_code)
    Spinner spCode;
    @BindView(R.id.et_register_phone)
    CustomRegularEditView etRegisterPhone;
    @BindView(R.id.sp_country_reg)
    Spinner spCountryReg;
    @BindView(R.id.sp_curency_reg)
    Spinner spCurencyReg;
    @BindView(R.id.et_register_vehicle_model)
    CustomRegularEditView etRegisterVehicleModel;
    @BindView(R.id.et_register_vehicle_colour)
    CustomRegularEditView etRegisterVehicleColour;
    @BindView(R.id.et_register_vehicle_number)
    CustomRegularEditView etRegisterVehicleNumber;
    @BindView(R.id.iv_register_vehicle_icon)
    ImageView ivRegisterVehicleIcon;
    @BindView(R.id.lst_vehicle)
    RecyclerView lstVehicle;
    @BindView(R.id.rigister_btn)
    Button rigisterBtn;
    @BindView(R.id.rigister_btn2)
    Button registtrarN1;
    @BindView(R.id.redirectLogin)
    CustomRegularTextView redirectLogin;
    @BindView(R.id.inputPassword)
    TextInputLayout inputPassword;
    @BindView(R.id.Generar_numero)
    CustomRegularEditView generarN;
    @BindView(R.id.recibir_numero)
    CustomRegularEditView recibirN;
    @BindView(R.id.rigister_btn1)
    Button registtrarN;
    @BindView(R.id.termsDesc)
    TextView termsDesc;
    @BindView(R.id.et_register_username)
    CustomRegularEditView et_register_username;
    @BindView(R.id.user_referral_code)
    CustomRegularEditView user_referral_code;

    Uri fileToUpload;
    Uri filetoUploadVeh;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    private static int selectIcon;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    private String type = Const.MANUAL;
    private AQuery aQuery;
    private String socialUrl;
    private boolean isclicked = false;
    private String filePath = "", filePath_vehicle = "";
    private String socialId;
    private ArrayList<String> countryCodes, countryCodesIso;
    private SpinnerAdapter adapter_currencey, adapter;
    private Uri uri = null;
    private File cameraFile;
    private ParseContent pcontent;
    private RadioButton rd_btn;
    private TaxiAdapter typesAdapter;
    private ArrayList<TaxiTypes> taxiLst = new ArrayList<>();
    private int service_id = -1;
    private boolean isClicked = false;
    private Uri filepath;

    private static final int PICK_IMAGE = 100;
    private static final int STORAGE_PERMISSION_REQUEST = 1000;


    private Uri pictureToUpload = null, carToUpload = null;
    boolean isPicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_fragment);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        countryCodes = new ArrayList<>();
        countryCodesIso = new ArrayList<>();
        getTaxiTypes();
        setUpAdapter();
        addTextChangedListener();
        setSpinners();
        inputPassword.setHintAnimationEnabled(false);
        inputPassword.setHint("");
        etRegisterYourPassword.setHint(getString(R.string.password));
        rigisterBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            rigisterBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), checkBox.isChecked() ? R.color.colorAccent : R.color.dark_grey));
            rigisterBtn.setEnabled(b);
        });

        Spannable wordtoSpan = new SpannableString(getString(R.string.already_have_account_login));
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 24, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        redirectLogin.setText(wordtoSpan);

        Spannable termsAndConditionsSpan = new SpannableString(getString(R.string.please_accept_our_terms_and_conditions_to_proceed));
        termsAndConditionsSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(getApplicationContext(), HelpwebActivity.class);
                startActivity(i);
            }
        },18, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsAndConditionsSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(getApplicationContext(), HelpwebActivity.class);
                startActivity(i);
            }
        },43, 57, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsDesc.setText(termsAndConditionsSpan);
        termsDesc.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setUpAdapter() {
        lstVehicle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        ItemClickSupport.addTo(lstVehicle)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    typesAdapter.ItemClicked(position);
                    service_id = Integer.valueOf(taxiLst.get(position).getId());
                });
    }

    private void addTextChangedListener() {
//        etRegisterYourPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                visPass.setVisibility(View.VISIBLE);
//                visPass.setOnClickListener(v -> {
//                    if (isclicked == false) {
//                        etRegisterYourPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                        etRegisterYourPassword.setSelection(etRegisterYourPassword.getText().length());
//                        isclicked = true;
//                        visPass.setVisibility(View.VISIBLE);
//
//                    } else {
//                        isclicked = false;
//                        etRegisterYourPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        visPass.setVisibility(View.VISIBLE);
//
//                        etRegisterYourPassword.setSelection(etRegisterYourPassword.getText().length());
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }
    /*
    public void recargarCodigo() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.generarcodigo(
                etRegisterYourEmail.getText().toString(),
                etRegisterPhone.getText().toString(),
                generarN.getText().toString()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject recargaResponse = null;
                try {
                    recargaResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recargaResponse != null) {
                    if (recargaResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        String message = recargaResponse.optString(APIConstants.Params.MESSAGE);
                        UiUtils.showShortToast(RegisterActivity.this, message);
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Su codigo se genero Exitosamente", Toast.LENGTH_SHORT);
                        toast1.show();

                    } else {
                        UiUtils.showShortToast(RegisterActivity.this, recargaResponse.optString(APIConstants.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }
    */
    @OnClick({/*R.id.applyRefCode, */R.id.busqueda})
    public void onViewClicked4(View view) {
        verificarEmail();
    }
    protected void verifyReferal() {
        new Thread() {
            public void run() {
                try {
                    String url = Const.ServiceType.VERIFICAR_REFERIDO;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "username=" + user_referral_code.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject obj = new JSONObject(response);
                    Boolean status = obj.getBoolean("existe");
                    if (status.equals(true)) {
                        user_referral_code.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                        //userReferralCode.requestFocus();
                    } else {
                        user_referral_code.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        //userReferralCode.requestFocus();
                    }



                } catch (Exception e) {
                    System.out.println("error catch: " + e);
                }
            }
        }.start();
    }
    @OnClick({/*R.id.applyRefCode, */R.id.rigister_btn1})
    public void onViewClicked1(View view) {
             switch (view.getId()) {
            case R.id.rigister_btn1:
                if (validateFields()){
                    Button btm1;
                    TextView tv1;
                    btm1=findViewById(R.id.rigister_btn1);
                    tv1=findViewById(R.id.Generar_numero);
                    final Random random=new Random();
                    String randonNumber=String.valueOf(random.nextInt(999999));
                    tv1.setText(randonNumber);
                    registtrarN.setEnabled(false);
                    registtrarN.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.bt_light_gray));
                    recibirN.setVisibility(View.VISIBLE);
                    registtrarN1.setVisibility(View.VISIBLE);
                    verifyReferal();
                    verifyUserApp();
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Se Envio el codigo a su correo", Toast.LENGTH_SHORT);
                    toast1.show();
                    //recargarCodigo();
                }else{

                    Toast toast1 = Toast.makeText(getApplicationContext(), "No cumple con lo necesario llene todos los campos ", Toast.LENGTH_SHORT);
                    toast1.show();
                }



                break;
        }
    }
    @OnClick({/*R.id.applyRefCode, */R.id.rigister_btn2})
    public void onViewClicked2(View view) {
        switch (view.getId()) {
            case R.id.rigister_btn2:
                generarN.getText().toString().trim().length();
                recibirN.getText().toString().trim().length();
                if(generarN.getText().toString().equals(recibirN.getText().toString())){
                    recibirN.setEnabled(false);
                    rigisterBtn.setVisibility(View.VISIBLE);
                    registtrarN1.setEnabled(false);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Su codigo se valido Exitosamente", Toast.LENGTH_SHORT);
                    toast1.show();

                }else{

                    registtrarN.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.com_facebook_blue));
                    registtrarN1.setVisibility(View.INVISIBLE);
                    recibirN.setVisibility(View.INVISIBLE);
                    registtrarN.setEnabled(true);
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Vuelva a generar un codigo muchas gracias", Toast.LENGTH_SHORT);
                    toast1.show();
                }

                break;

        }
    }
    /*
    ////metodo para ingresar el codigo autogenrado////////
    public void recargarCodigo() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.generarcodigo(
                etRegisterYourEmail.getText().toString(),
                etRegisterPhone.getText().toString(),
                generarN.getText().toString()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject recargaResponse = null;
                try {
                    recargaResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recargaResponse != null) {
                    if (recargaResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        String message = recargaResponse.optString(Params.MESSAGE);
                        UiUtils.showShortToast(RegisterII.this, message);
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Su codigo se genero Exitosamente", Toast.LENGTH_SHORT);
                        toast1.show();

                    } else {
                        UiUtils.showShortToast(RegisterII.this, recargaResponse.optString(Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

     */
    protected void verifyUserApp() {
        new Thread() {
            public void run() {
                try {
                    String url = Const.ServiceType.VERIFICAR_USUARIO;
                    AbstractClient serv = new AbstractClient();
                    String response = serv.web(url, "username=" + et_register_username.getText().toString());
                    System.out.println("response register: " + response);
                    JSONObject obj = new JSONObject(response);
                    Boolean status = obj.getBoolean("existe");

                    if (status.equals(true)) {
                        et_register_username.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        //etUsername.requestFocus();
                    } else {
                        et_register_username.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bt_light_gray));
                        //etUsername.requestFocus();
                    }



                } catch (Exception e) {
                    System.out.println("error catch: " + e);
                }
            }
        }.start();
    }
    private void getTaxiTypes() {
        UiUtils.showLoadingDialog(RegisterII.this);
        Call<String> call = apiInterface.servicesList(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject job = null;
                try {
                    job = new JSONObject(response.body());
                    if (job != null) {
                        if (job.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            taxiLst.clear();
                            JSONArray jArray = job.optJSONArray(Params.DATA);
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject typesObj = jArray.optJSONObject(i);
                                TaxiTypes types = new TaxiTypes();
                                types.setId(typesObj.optString(Params.SERVICE_TYPE_ID));
                                types.setTaxitype(typesObj.optString(Params.SERVICE_TYPE_NAME));
                                types.setTaxiimage(typesObj.optString(Params.PICTURE));
                                taxiLst.add(types);
                            }
                            if (null != taxiLst) {
                                typesAdapter = new TaxiAdapter(RegisterII.this, taxiLst);
                                lstVehicle.setAdapter(typesAdapter);
                            }
                            UiUtils.hideLoadingDialog();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    private void setSpinners() {
        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<String>(RegisterII.this, R.layout.spinner_item, parseCountryCodes());
        spCode.setAdapter(countryCodeAdapter);
        TelephonyManager tm = (TelephonyManager) RegisterII.this.getSystemService(RegisterII.this.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        for (int i = 0; i < countryCodesIso.size(); i++) {
            if (countryCodesIso.get(i).equalsIgnoreCase(countryCodeValue)) {
                spCode.setSelection(i);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterII.this, R.layout.spinner_item, parseCountry());
        String[] lst_currency = getResources().getStringArray(R.array.currency);
        Integer[] currency_imageArray = {R.drawable.us, R.drawable.ic_india};
        adapter_currencey = new SpinnerAdapter(RegisterII.this, R.layout.spinner_value_layout, lst_currency, currency_imageArray);
        spCurencyReg.setAdapter(adapter_currencey);
    }

    public ArrayList<String> parseCountry() {
        String response = "";
        ArrayList<String> list = new ArrayList<>();
        try {
            response = ReadFiles.readRawFileAsString(RegisterII.this, R.raw.countrycodes);
            JSONArray array = new JSONArray(response);
            Log.d("mahi", "countries" + response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("name"));
            }
            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> parseCountryCodes() {
        String response = "";
        ArrayList<String> list = new ArrayList<>();
        try {
            response = ReadFiles.readRawFileAsString(RegisterII.this, R.raw.countrycodes);
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("alpha-2") + " (" + object.getString("phone-code") + ")");
                countryCodes.add(object.getString("phone-code"));
                countryCodesIso.add(object.getString("alpha-2"));
            }
            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void registeration(String type, String socialId) {
        String[] items1 = spCode.getSelectedItem().toString().split(" ");
        String country = items1[0];
        String code = items1[1];

        MultipartBody.Part pictureBody = null;
        MultipartBody.Part carBody = null;
        if (pictureToUpload != null) {
            String path = getRealPathFromURI(pictureToUpload);
            File file = new File(path);
            String mimeType = RegisterII.this.getContentResolver().getType(pictureToUpload);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType), file);
            pictureBody = MultipartBody.Part.createFormData(Params.PICTURE, file.getName(), requestFile);
        }

        if (carBody != null) {
            String path = getRealPathFromURI(carToUpload);
            File file = new File(path);
            String mimeType = RegisterII.this.getContentResolver().getType(carToUpload);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType), file);
            pictureBody = MultipartBody.Part.createFormData(Params.CAR_IMAGE, file.getName(), requestFile);
        }

        UiUtils.showLoadingDialog(RegisterII.this);
        MultipartBody.Part multipartBodyUserIcon = null;
        MultipartBody.Part multipartBodyVehicleIcon = null;
        try {

            if (fileToUpload != null) {
                String path = getRealPathFromURIPath(fileToUpload, this);
                File file = new File(path);
                // create RequestBody instance tempFrom file
                String mimeType = getContentResolver().getType(fileToUpload);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType),
                                file);
                // MultipartBody.Part is used to send also the actual file name
                multipartBodyUserIcon =
                        MultipartBody.Part.createFormData(Params.PICTURE, file.getName(), requestFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




        if (filetoUploadVeh != null) {
            String path = getRealPathFromURIPath(filetoUploadVeh, this);
            File file = new File(path);
            // create RequestBody instance tempFrom file
            String mimeType = getContentResolver().getType(filetoUploadVeh);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType),
                            file);
            // MultipartBody.Part is used to send also the actual file name
            multipartBodyVehicleIcon =
                    MultipartBody.Part.createFormData(Params.CAR_IMAGE, file.getName(), requestFile);
        }

        Call<String> call = apiInterface.register(etRegisteridentificacion.getText().toString()
                ,etRegisterFirstName.getText().toString()
                ,etRegisterLastName.getText().toString()
                , etRegisterYourEmail.getText().toString()
                , etRegisterYourPassword.getText().toString()
                , "manual"
                , PrefKeys.ANDRIOD
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , etRegisterPhone.getText().toString()
                , multipartBodyUserIcon
                , TimeZone.getDefault().getID()
                , String.valueOf(service_id)
                , etRegisterVehicleNumber.getText().toString()
                , etRegisterVehicleColour.getText().toString()
                , etRegisterVehicleModel.getText().toString()
                , code
                , multipartBodyVehicleIcon
                , et_register_username.getText().toString()
                , user_referral_code.getText().toString()
        );

        //code.replace("(", "").replace(")", "") + "" + etRegisterFirstName.getText().toString()
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject loginResponse = null;
                try {
                    loginResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loginResponse != null) {
                    if (loginResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.showShortToast(RegisterII.this, loginResponse.optString(Params.MESSAGE));
                        JSONObject data = loginResponse.optJSONObject(Params.DATA);
                        loginUserInDevice(data, Constants.MANUAL_LOGIN);
                    } else {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(RegisterII.this, loginResponse.optString(Params.ERROR));
                        if (loginResponse.optInt(Params.ERROR_CODE) == APIConstants.ErrorCodes.VERIFY_USER) {
                            startActivity(new Intent(RegisterII.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }

    private String getRealPathFromURIPath(Uri fileToUpload, RegisterII registerFragment) {
        Cursor cursor = RegisterII.this.getContentResolver().query(fileToUpload, null, null, null, null);
        if (cursor == null) {
            return fileToUpload.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    protected void verificarEmail() {
        new Thread() {
            public void run() {
                String url = Const.ServiceType.VERIFICAR_EMAIL;
                AbstractClient serv = new AbstractClient();
try {

    String response = serv.web(url, "email=" + etRegisterYourEmail.getText().toString());
    System.out.println("response register: " + response);
    JSONObject obj = new JSONObject(response);
    JSONArray resultArray = obj.getJSONArray("result");
    JSONObject sponsor = resultArray.getJSONObject(0);
    sponsor.getJSONObject("usuario");
    String sponsor1 = String.valueOf(sponsor.getJSONObject("usuario").get("sponsor_username"));
    user_referral_code.setText(sponsor1);
}catch (Exception e){

}
try {
    String response4 = serv.web(url, "email=" + etRegisterYourEmail.getText().toString());
    System.out.println("response register: " + response4);
    JSONObject obj4 = new JSONObject(response4);
    JSONArray resultArray4 = obj4.getJSONArray("result");
    JSONObject mobil = resultArray4.getJSONObject(0);
    mobil.getJSONObject("usuario");
    String username4 = String.valueOf(mobil.getJSONObject("usuario").get("telefono"));
    etRegisterPhone.setText(username4);
}catch (Exception e){

}
try{
    String response3 = serv.web(url, "email=" + etRegisterYourEmail.getText().toString());
    System.out.println("response register: " + response3);
    JSONObject obj3 = new JSONObject(response3);
    JSONArray resultArray3 = obj3.getJSONArray("result");
    JSONObject apellido = resultArray3.getJSONObject(0);
    apellido.getJSONObject("usuario");
    String username3 = String.valueOf(apellido.getJSONObject("usuario").get("apellido"));
    etRegisterLastName.setText(username3);
}catch (Exception e){}
try {
    String response2 = serv.web(url, "email=" + etRegisterYourEmail.getText().toString());
    System.out.println("response register: " + response2);
    JSONObject obj2 = new JSONObject(response2);
    JSONArray resultArray2 = obj2.getJSONArray("result");
    JSONObject nombre = resultArray2.getJSONObject(0);
    nombre.getJSONObject("usuario");
    String username2 = String.valueOf(nombre.getJSONObject("usuario").get("nombre"));
    etRegisterFirstName.setText(username2);
}catch (Exception e){}
try{
    String response1 = serv.web(url, "email=" + etRegisterYourEmail.getText().toString());
    System.out.println("response register: " + response1);
    JSONObject obj1 = new JSONObject(response1);
    JSONArray resultArray1 = obj1.getJSONArray("result");
    JSONObject username = resultArray1.getJSONObject(0);
    username.getJSONObject("usuario");
    String username1 = String.valueOf(username.getJSONObject("usuario").get("username"));
    et_register_username.setText(username1);
} catch (Exception e) {

}
}
        }.start();
    }
    private void loginUserInDevice(JSONObject data, String manualLogin) {
        PrefHelper.setUserLoggedIn(RegisterII.this, data.optInt(Params.PROVIDER_ID)
                , data.optString(Params.NAME)
                , data.optString(Params.FIRSTNAME)
                , data.optString(Params.LAST_NAME)
                , data.optString(Params.EMAIL)
                , data.optString(Params.PICTURE)
                , data.optString(Params.PHONE)
                , data.optString(Params.DESCRIPTION)
                , data.optString(Params.TOKEN)
                , manualLogin
                , data.optInt(Params.PROVIDER_STATUS)
                , data.optString(Params.TIME_ZONE)
                , data.optString(Params.COUNTRY)
                , data.optInt(Params.CURRENCEY)
                , data.optString(Params.PLATE_NUMBER)
                , data.optString(Params.COLOR)
                , data.optString(Params.GENDER)
        );

        prefUtils.setValue(data.optString(PrefKeys.COUNTRY_CODE), "");
        Intent toHome = new Intent(RegisterII.this, MainActivity.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        RegisterII.this.finish();
    }


    private boolean validateFields() {
        if (etRegisterYourEmail.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.email_cant_be_empty));
            etRegisterYourEmail.setError(getResources().getString(R.string.txt_email_error));
            etRegisterYourEmail.requestFocus();
            return false;
        }

        if (!AppUtils.isValidEmail(etRegisterYourEmail.getText().toString())) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.enter_valid_email));
            return false;
        }
        if (etRegisterYourPassword.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.password_cant_be_empty));
            etRegisterYourPassword.setError(getResources().getString(R.string.txt_pass_error));
            etRegisterYourPassword.requestFocus();
            return false;
        }
        if (etRegisterYourPassword.getText().toString().length() < 6) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.minimum_six_characters));
            return false;
        }
        if (etRegisterFirstName.getText().toString().trim().length() == 0) {
            etRegisterFirstName.setError(getResources().getString(R.string.enterFirstName));
            etRegisterFirstName.requestFocus();
            return false;
        }
        if (etRegisterLastName.getText().toString().trim().length() == 0) {
            etRegisterLastName.setError(getResources().getString(R.string.enterLastName));
            etRegisterLastName.requestFocus();
            return false;
        }
        if (etRegisterPhone.getText().toString().trim().length() == 0) {
            etRegisterPhone.setError(getResources().getString(R.string.enterValidPhone));
            etRegisterPhone.requestFocus();
            return false;
        }
        if (etRegisterVehicleNumber.getText().toString().trim().length() == 0) {
            etRegisterVehicleNumber.setError(getResources().getString(R.string.enterVechicleNumb));
            etRegisterVehicleNumber.requestFocus();
            return false;
        }

        if (etRegisterVehicleColour.getText().toString().trim().length() == 0) {
            etRegisterVehicleColour.setError(getResources().getString(R.string.enterVechicleNumber));
            etRegisterVehicleColour.requestFocus();
            return false;
        }

        if (etRegisterVehicleModel.getText().toString().trim().length() == 0) {
            etRegisterVehicleModel.setError(getResources().getString(R.string.enterModel));
            etRegisterVehicleModel.requestFocus();
            return false;
        }

        if (filetoUploadVeh == null) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.upload_car_image));
            return false;
        }

        if (service_id == -1) {
            UiUtils.showShortToast(RegisterII.this, getString(R.string.error_select_taxi));
            return false;
        }
        return true;
    }

    private void showPictureDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterII.this);
        dialog.setTitle(getResources().getString(R.string.txt_slct_option));
        String[] items = {getResources().getString(R.string.txt_gellery), getResources().getString(R.string.txt_cameray)};
        dialog.setItems(items, (dialog1, which) -> {
            switch (which) {
                case 0:
                    choosePhotoFromGallary();
                    break;
                case 1:
                    takePhotoFromCamera();
                    break;
            }
        });
        dialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        RegisterII.this.startActivityForResult(i, Const.CHOOSE_PHOTO);
    }

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        cameraFile = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));
        if (!cameraFile.exists()) {
            try {
                cameraFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cameraFile.delete();
            try {
                cameraFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent i;
        if (isPicture) {
            pictureToUpload = Uri.fromFile(cameraFile);
            i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, pictureToUpload);
        } else {
            carToUpload = Uri.fromFile(cameraFile);
            i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, carToUpload);
        }
        startActivityForResult(i, Const.TAKE_PHOTO);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = RegisterII.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void setSocailDetailsOnView(SocialMediaProfile mediaProfile) {
        aQuery.id(ivRegisterUserIcon).image(mediaProfile.getPictureUrl(), true, true,
                300, 300, new BitmapAjaxCallback() {
                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm,
                                         AjaxStatus status) {
                        filePath = aQuery.getCachedFile(url).getPath();
                        iv.setImageBitmap(bm);
                    }
                });
        socialId = mediaProfile.getSocialUniqueId();
        etRegisterFirstName.setText(mediaProfile.getFirstName());
        etRegisterLastName.setText(mediaProfile.getLastName());
        etRegisterYourEmail.setText(mediaProfile.getEmailId());
        socialUrl = mediaProfile.getPictureUrl();
        etRegisterYourPassword.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("mahi", "req code" + requestCode);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                fileToUpload = data.getData();
                Glide.with(RegisterII.this)
                        .load(fileToUpload)
                        .into(ivRegisterUserIcon);
            } else {
                UiUtils.showShortToast(RegisterII.this, "Choose any one of the above options");
            }

        } else {
            if (data != null) {
                filetoUploadVeh = data.getData();
                Glide.with(RegisterII.this)
                        .load(filetoUploadVeh)
                        .into(ivRegisterVehicleIcon);
            } else {
                UiUtils.showShortToast(RegisterII.this, "Choose any one of the above options");
            }

        }
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), (Calendar.getInstance().getTimeInMillis() + ".jpg")));
        Crop.of(source, outputUri).asSquare().start(RegisterII.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (isClicked == true) {
                filePath_vehicle = getRealPathFromURI(Crop.getOutput(result));
                Log.e("asher 1 ", filePath_vehicle + " " + Crop.getOutput(result));
                Glide.with(RegisterII.this).load(filePath_vehicle).override(400, 400).centerCrop().into(ivRegisterVehicleIcon);
                isClicked = false;
            } else {
                filePath = getRealPathFromURI(Crop.getOutput(result));
                Log.e("asher 2 ", filePath + " " + Crop.getOutput(result));
                Glide.with(RegisterII.this).load(filePath).override(400, 400).centerCrop().into(ivRegisterUserIcon);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(RegisterII.this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @OnClick({R.id.iv_register_user_icon, R.id.iv_register_vehicle_icon, R.id.rigister_btn, R.id.redirectLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_register_user_icon:
                // showPictureDialog();
                selectIcon = 1;
                callImagePicker(selectIcon);

                isPicture = true;
                //showPictureDialog();

                break;
            case R.id.iv_register_vehicle_icon:

                isClicked = true;
                selectIcon = 2;
                callImagePicker(selectIcon);
                isPicture = true;
                //showPictureDialog();
                break;
            case R.id.rigister_btn:
                if(checkBox.isChecked()) {
                    if (validateFields()) {
                        registeration("manual", "socialid");
                    }
                }
                break;
            case R.id.redirectLogin:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }

    private void callImagePicker(int selectIcon) {
        if (ContextCompat.checkSelfPermission(RegisterII.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new android.app.AlertDialog.Builder(RegisterII.this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.need_storage_permission_title))
                    .setMessage(getString(R.string.need_storage_permission_desc))
                    .setPositiveButton(getString(R.string.grant), ((dialog, which)
                            ->  EasyPermissions.requestPermissions(this, getString(R.string.need_storage_permission_desc),
                            RC_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE)))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        } else {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent,  selectIcon == 1 ?   PICK_IMAGE : PICK_IMAGE_VECHILE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent,  selectIcon == 1 ?   PICK_IMAGE : PICK_IMAGE_VECHILE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
