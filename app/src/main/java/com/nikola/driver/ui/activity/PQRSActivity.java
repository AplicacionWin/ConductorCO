package com.nikola.driver.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh on 3/1/2017.
 */

public class PQRSActivity extends AppCompatActivity {

    @BindView(R.id.pqrs_back)
    ImageButton pqrsBack;
    @BindView(R.id.actionbar_lay)
    RelativeLayout actionbarLay;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.web_loader)
    ProgressBar webLoader;
    @BindView(R.id.helpContent)
    TextView helpContent;
    APIInterface apiInterface;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.pqrsview);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        try {
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

            webView.loadUrl("http://www.aplicacionwin.com/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }

        pqrsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
