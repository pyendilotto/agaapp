package com.mormandoweb.aga;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private String MiUrl = "http://agamdq.com/app.html";
    private static int REQUEST_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FULL SCREEN SIN ACTION BAR:
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //LINKEO LOS COMPONENTES:
        linkearXML();
        //PIDO LOS PERMISOS PARA VERSIONES MAYORES A API >=23:
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);
        //INTENTO ABRIR LA URL SI HAY CONEXION:
        if (isOnline())
        {
            cargarSitio();
        }
        //NO HAY INTERNET
        else
        {
            webview.loadDataWithBaseURL("", getResources().getString(R.string.error_internet), "text/html", "UTF-8", "");
        }



    }

    ///////////////////////////////////////////////////////////////////

    private void linkearXML(){

        webview = (WebView)findViewById(R.id.webview);

    }
    ///////////////////////////////////////////////////////////////////

    private void cargarSitio(){

        webview.clearCache(true);
        webview.clearHistory();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setBuiltInZoomControls(true);
        //SETEO MENSAJE DE CARGANDO:
        webview.loadDataWithBaseURL("", getResources().getString(R.string.cargando), "text/html", "UTF-8", "");
        //LISTENER QUE SI RECIBE UN ERROR LO MUESTRA EN EL WEBVIEW.
        //P. EJ.: CORTE DE INTERNET DURANTE LA NAVEGACION.
        webview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.loadDataWithBaseURL("", description, "text/html", "UTF-8", "");
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub

                view.loadUrl(url);
                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);

            }
        });

        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength){


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setDescription("Download file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }
        });

        //CARGO EL SITIO:
        webview.loadUrl(MiUrl);



    }
    ///////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        //CIERRO:
        finish();

        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////

    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    ///////////////////////////////////////////////////////////////////




}

