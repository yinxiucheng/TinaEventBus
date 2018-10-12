package com.tina.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unRegister();
    }


    public void change(View view)
    {
        startActivity(new Intent(this,SecondActivity.class));
    }



    //主线程---子线程接受
    @Subscribe(threadMode = ThreadMode.Async)
    public void receive(Friend friend )
    {
        Log.i("david", "thread: " + Thread.currentThread().getName());
    }


}
