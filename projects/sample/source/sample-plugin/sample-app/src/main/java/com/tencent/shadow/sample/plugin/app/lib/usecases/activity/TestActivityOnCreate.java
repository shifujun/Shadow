/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.plugin.app.lib.usecases.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.FutureTarget;
import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race.MqttDataResolver;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race.OnMqttMessageComeListener;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race.OnResultCallback;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race.RaceTrackItemData;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race.UserItemData;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class TestActivityOnCreate extends Activity implements OnMqttMessageComeListener {
    private MapView mapView;

    private HashMap<String, Marker> markersMap = new HashMap<>();
    private HashMap<String, UserItemData> userMap = new HashMap<>();
    private HashMap<String, Bitmap> bmpMap = new HashMap<>();
    private TencentMap aMap;

    @Override
    public void onNewUserCome(UserItemData user) {
        addNewUser(user);
    }

    public static class Case extends UseCase{
        @Override
        public String getName() {
            return "生命周期测试";
        }

        @Override
        public String getSummary() {
            return "测试Activity的生命周期方法是否正确回调";
        }

        @Override
        public Class getPageClass() {
            return TestActivityOnCreate.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MqttDataResolver.getInstance().addMessageComeListener(this);
        setContentView(R.layout.layout_activity_lifecycle);
        mapView = findViewById(R.id.mapview);

        aMap = mapView.getMap();
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        markersMap.clear();

        loadTrack(new OnResultCallback<ArrayList<RaceTrackItemData>>() {
            @Override
            public void onResult(ArrayList<RaceTrackItemData> data) {
                addTracks(data);
            }
        });

        loadAllUser(new OnResultCallback<ArrayList<UserItemData>>() {
            @Override
            public void onResult(ArrayList<UserItemData> users) {
                for (UserItemData item : users) {
                    addNewUser(item);
                }
            }
        });
    }

    private void loadTrack(OnResultCallback<ArrayList<RaceTrackItemData>> successCallback) {
        new AsyncTask<String, Integer, ArrayList<RaceTrackItemData>>() {

            @Override
            protected ArrayList<RaceTrackItemData> doInBackground(String... strings) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<RaceTrackItemData> tracks = new ArrayList<>();
                int scale = 10;
                for (int i = 0; i <= 360 * scale; i++) {
                    double angle = (i / (1.0 * 360 * scale)) * 2 * Math.PI;
                    double x = Math.cos(angle) * 0.01 + 39.983186;
                    double y = Math.sin(angle) * 0.01 + 116.306503;
                    tracks.add(new RaceTrackItemData(x, y));
                }
                return tracks;
            }

            @Override
            protected void onPostExecute(ArrayList<RaceTrackItemData> raceTrackItemData) {
                super.onPostExecute(raceTrackItemData);
                if (successCallback != null) {
                    successCallback.onResult(raceTrackItemData);
                }
            }
        }.execute();
    }

    private void loadAllUser(OnResultCallback<ArrayList<UserItemData>> successCallback) {
        new AsyncTask<String, Integer, ArrayList<UserItemData>>() {
            @Override
            protected ArrayList<UserItemData> doInBackground(String... strings) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<UserItemData> users = new ArrayList<UserItemData>();
                String icon = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2F2021%2Fedpic_360_360%2F96%2F1d%2Fe9%2F961de9fd3aae1656df8234fad2c004f6_1.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642683084&t=ddbdb1836aadf964c4337cd037bcccb5";
                for (int i = 1; i <= 6; i++) {
                    double x = Math.cos(0) * 0.01 + 39.983186;
                    double y = Math.sin(0) * 0.01 + 116.306503;
                    users.add(new UserItemData("" + i, String.format("黄%d%d", i, i), x, y, icon, 0));
                }
                return users;
            }

            @Override
            protected void onPostExecute(ArrayList<UserItemData> userItemData) {
                super.onPostExecute(userItemData);
                successCallback.onResult(userItemData);
            }
        }.execute();
    }

    private void addNewUser(UserItemData item) {
        if (item == null) {
            return;
        }

        boolean isNotExists = userMap.get(item.id) == null;
        userMap.put(item.id, item);
        if(isNotExists) {
            Marker marker1 = addMarker(item);
            markersMap.put(item.id, marker1);
            loadIcon(item.id, item.icon);
        }else{
            Marker marker = markersMap.get(item.id);
            if (marker != null) {
                updateMaker(marker, item);
            }
        }
    }

    private void addTracks(ArrayList<RaceTrackItemData> tracks) {
        //调整最佳视界
        LatLng[] mCarLatLngArray = new LatLng[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            RaceTrackItemData item = tracks.get(i);
            mCarLatLngArray[i] = new LatLng(item.lat, item.lng);
        }
        aMap.addPolyline(new PolylineOptions().add(mCarLatLngArray));
        LatLngBounds bounds = LatLngBounds.builder().include(Arrays.asList(mCarLatLngArray)).build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
    }

    private Marker addMarker(UserItemData item) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_head_icon);
        LatLng latLng = new LatLng(item.lat, item.lng);
        View view = LayoutInflater.from(this).inflate(R.layout.item_map_marker, null, false);
        TextView tvName = view.findViewById(R.id.tv_name);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvName.setText(item.name);
        tvMsg.setText(item.distance + "m");
        ivIcon.setImageBitmap(bmp);
        MarkerOptions markerOption1 = new MarkerOptions(latLng)
                .icon(BitmapDescriptorFactory.fromView(view))
                .title(null)
                .snippet(null)
                .draggable(true);
        Marker marker1 = aMap.addMarker(markerOption1);
        return marker1;
    }

    public void updateMaker(Marker marker, UserItemData item) {
        Bitmap bmp = bmpMap.get(item.id);
        if(bmp == null || bmp.isRecycled()) {
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_head_icon);
        }
        LatLng latLng = new LatLng(item.lat, item.lng);
        View view = LayoutInflater.from(this).inflate(R.layout.item_map_marker, null, false);
        TextView tvName = view.findViewById(R.id.tv_name);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvName.setText(item.name);
        tvMsg.setText(item.distance + "m");
        ivIcon.setImageBitmap(bmp);
        marker.setIcon(BitmapDescriptorFactory.fromView(view));
        marker.setPosition(latLng);
    }

    private void loadIcon(String id, String icon) {
        new AsyncTask<String, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                FutureTarget<Bitmap> k = Glide.with(TestActivityOnCreate.this)
                        .asBitmap()
                        .transform(new CenterCrop(), new CircleCrop())
                        .load(icon)
                        .submit();
                try {
                    Bitmap bitmap = k.get();
                    return bitmap;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);
                Marker marker = markersMap.get(id);
                UserItemData userItemData = userMap.get(id);
                if (userItemData != null) {
                    bmpMap.put(id, bmp);
                    updateMaker(marker, userItemData);
                }
            }
        }.execute(id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        MqttDataResolver.getInstance().removeMessageComeListener(this);
    }

}
