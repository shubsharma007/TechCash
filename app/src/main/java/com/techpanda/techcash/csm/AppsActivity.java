package com.techpanda.techcash.csm;

import static com.techpanda.techcash.helper.PrefManager.BANNER_AD_TYPE;
import static com.techpanda.techcash.helper.PrefManager.CHARTBOOST_AD_TYPE;
import static com.techpanda.techcash.helper.PrefManager.INTERSTITAL_AD_TYPE;
import static com.techpanda.techcash.helper.PrefManager.getSavedString;
import static com.techpanda.techcash.helper.PrefManager.user_points;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techpanda.techcash.AdsManager;
import com.techpanda.techcash.R;
import com.techpanda.techcash.csm.adapter.WebsiteAdapter;
import com.techpanda.techcash.csm.model.WebsiteModel;
import com.techpanda.techcash.helper.Helper;
import com.techpanda.techcash.helper.PrefManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.techpanda.techcash.UpdateListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppsActivity extends AppCompatActivity implements UpdateListener {
    AppsActivity activity;
    TextView user_points_text_view, noWebsiteFound;
    private ImageView back;
    private int visit_index = 0;
    private WebsiteAdapter websiteAdapter;
    private ArrayList<WebsiteModel> websiteModelArrayList = new ArrayList<>();
    private ArrayList<WebsiteModel> websiteModels = new ArrayList<>();
    private RecyclerView websiteRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        activity = this;
        user_points_text_view = findViewById(R.id.points);
        back = findViewById(R.id.back);
        noWebsiteFound = findViewById(R.id.noWebsiteFound);
        websiteRv = findViewById(R.id.websiteRv);
        websiteRv.setLayoutManager(new LinearLayoutManager(activity));
        String json = PrefManager.getSavedString(activity, Helper.APPS_LIST);
        if (!json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<WebsiteModel>>() {
            }.getType();
            websiteModels = gson.fromJson(json, type);
            websiteAdapter = new WebsiteAdapter(websiteModelArrayList, activity, "app", activity);
            websiteRv.setAdapter(websiteAdapter);
        }
        setWebsiteData();
        back.setOnClickListener(view -> {
            onBackPressed();
        });
        if (getSavedString(this, BANNER_AD_TYPE).equalsIgnoreCase(CHARTBOOST_AD_TYPE) &&
                getSavedString(this, INTERSTITAL_AD_TYPE).equalsIgnoreCase(CHARTBOOST_AD_TYPE)
        ) {

        } else {
            AdsManager.loadBannerAd(this, findViewById(R.id.banner_ad_container));
        }
    }

    private void setWebsiteData() {
        websiteModelArrayList.clear();
        for (int i = 0; i < websiteModels.size(); i++) {
            if (!PrefManager.getSavedString(activity, Helper.APP_DATE + websiteModels.get(i).getId()).equalsIgnoreCase(PrefManager.getSavedString(activity, Helper.TODAY_DATE) + websiteModels.get(i).getId())) {
                if (!isAppInstalled(activity, websiteModels.get(i).getPackages())) {
                    websiteModelArrayList.add(websiteModels.get(i));
                }
            }
        }
        if (websiteModelArrayList.isEmpty()) {
            websiteRv.setVisibility(View.GONE);
            noWebsiteFound.setVisibility(View.VISIBLE);
        } else {
            websiteRv.setVisibility(View.VISIBLE);
            noWebsiteFound.setVisibility(View.GONE);
        }
        websiteAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        user_points(user_points_text_view);
        setWebsiteData();
        super.onResume();
    }


    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void UpdateListener(@Nullable String coin, @Nullable String time, @Nullable String link, @Nullable String browser, @Nullable String id, int index, @Nullable String description, @Nullable String logo, @Nullable String packages) {
        Intent intent = new Intent(activity, InstallActivity.class);
        intent.putExtra("logo", logo);
        intent.putExtra("name", websiteModelArrayList.get(index).getVisit_title());
        intent.putExtra("desc", description);
        intent.putExtra("link", link);
        intent.putExtra("pkg", packages);
        intent.putExtra("coin", coin);
        intent.putExtra("timer", time);
        intent.putExtra("isEqual", id);
        startActivity(intent);
    }
}