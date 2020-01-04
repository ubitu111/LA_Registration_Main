package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpec;

        Intent firstTabIntent = new Intent(this, NotSentVolunteersActivity.class);
        Intent seconTabIntent = new Intent(this, SentVolunteersActivity.class);
        Intent thirdTabIntent = new Intent(this, AllVolunteersActivity.class);

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Новые");
        tabSpec.setContent(firstTabIntent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Отправлены");
        tabSpec.setContent(seconTabIntent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Все");
        tabSpec.setContent(thirdTabIntent);
        tabHost.addTab(tabSpec);


    }
}
