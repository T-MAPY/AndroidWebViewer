package cz.tmapy.webviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_DEFAULT_URL = "PREF_DEFAULT_URL";
    @BindView(R.id.root_view)
    ConstraintLayout rootView;
    @BindView(R.id.url_text_view)
    EditText urlTextView;
    @BindView(R.id.search_button)
    ImageButton searchButton;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        // Reload last URL
        urlTextView.setText(sharedPref.getString(PREF_DEFAULT_URL, getString(R.string.default_url)));
        // Open URL on Search action on keyboard
        urlTextView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onViewClicked();
                    return true;
                }
                return false;
            }
        });

        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Snackbar mySnackbar = Snackbar.make(rootView, getString(R.string.cannot_load_page), Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Open default URL
        onViewClicked();
    }

    @OnClick(R.id.search_button)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(urlTextView.getText().toString()) && URLUtil.isValidUrl(urlTextView.getText().toString())) {
            webView.loadUrl(urlTextView.getText().toString());
            sharedPref.edit().putString(PREF_DEFAULT_URL, urlTextView.getText().toString()).apply();
        } else {
            Snackbar mySnackbar = Snackbar.make(rootView, getString(R.string.bad_url), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }
}
