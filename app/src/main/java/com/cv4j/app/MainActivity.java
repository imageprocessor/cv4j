package com.cv4j.app;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.filters.SepiaToneFilter;
import com.cv4j.core.filters.SinCityFilter;

public class MainActivity extends AppCompatActivity {

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initViews();
        initData();

//        new AsyncTask<String, Void, InputStream>() {
//            //该方法运行在后台线程中，因此不能在该线程中更新UI，UI线程为主线程
//            @Override
//            protected InputStream doInBackground(String... params) {
//
//                try {
//                    String url = params[0];
//                    URL HttpURL = new URL(url);
//                    HttpURLConnection conn = (HttpURLConnection) HttpURL.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    return is;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            //在doInBackground 执行完成后，onPostExecute 方法将被UI 线程调用，
//            // 后台的计算结果将通过该方法传递到UI线程，并且在界面上展示给用户.
//            @Override
//            protected void onPostExecute(InputStream is) {
//                if(is != null){
//                    ColorImage ci = new ColorImage(is);
//                    imageView3.setImageBitmap(ci.toBitmap());
//                }
//            }
//        }.execute("http://wx2.sinaimg.cn/large/6be8bc3dly1fdbttw81yfj20qo0qo0zo.jpg");

    }

    private void initViews() {
        imageView1 = (ImageView)findViewById(R.id.image1);
        imageView2 = (ImageView)findViewById(R.id.image2);
        imageView3 = (ImageView)findViewById(R.id.image3);
    }

    private void initData() {

        Resources res= getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.activity_2);
        imageView1.setImageBitmap(bitmap);

        ColorImage colorImage1 = new ColorImage(bitmap);
        SepiaToneFilter filter1 = new SepiaToneFilter();
        ColorImage newImage1 = (ColorImage) filter1.filter(colorImage1);
        imageView2.setImageBitmap(newImage1.toBitmap());

        ColorImage colorImage2 = new ColorImage(bitmap);
        SinCityFilter filter2 = new SinCityFilter();
        ColorImage newImage2 = (ColorImage) filter2.filter(colorImage2);
        imageView3.setImageBitmap(newImage2.toBitmap());
    }
}
