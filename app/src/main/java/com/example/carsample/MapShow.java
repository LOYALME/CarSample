package com.example.carsample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MapShow extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String [] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };





/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    //도시명 - 위도- 경도
    private static final String [] dosi = {"인천","전주","수원", "서울","포항","대구","안동","영덕","구미","부산","김해"};

    private static final Double [] lat = {37.45608784039938,35.82411488804424,  37.263309210326774, 37.56650780456895, 36.018938604175084, 35.87133424601544,
            36.56834481174756, 36.414912116600036,  36.11963264671915,  35.1795542154271,  35.22828653093207 };

    private static final Double [] longi = {126.70606341287771,127.14795758236247, 127.0287492845448, 126.97834988356041, 129.34354506093388, 128.60157135588148,
            128.72958618423402 ,129.36622858090558 , 128.34439155723587, 129.07513262467285 ,128.88946741488235};
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    boolean seach_ = false;
    NaverMap mNaverMap;

    String TAG = "LEEHEESUNG";
    ImageButton imageButton;
    EditText et;
    String url ;
    String key = "eq2xJlUPqcaVBS92m7ykJEXYPaBknZgCS41MgYvv%2FDdEOwP1clArrzEtnHioRU9fP9hKmB%2F%2F49RI9fwwGzyCrw%3D%3D"; // 전기차충전소 api key

    FusedLocationSource mLocationSource; // 내위치 gps
    ArrayList<DTO> arrayList; // data.go.kr에서 파싱 데이터배열

    ArrayList<DTO> backlist; // 2번째 검색부터 이전 검색데이터를 저장하는배열
    ArrayList<Marker> markerlist; //파싱 데이터 위치 마커 리스트

    InfoWindow infoWindow;

    LinearLayout linearLayout;
    String where;

    InputMethodManager imm;

    ;@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_show);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.line1);
        linearLayout.bringToFront();
        imageButton = findViewById(R.id.seach_imagbtu);
        et = findViewById(R.id.seach);
        markerlist = new ArrayList<>();
        infoWindow = new InfoWindow();




        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        where = et.getText().toString(); // 검색값 가져오기
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); // 키보드 내리기

                        et.setText("");
                        if(arrayList != null && arrayList.size() > 0) {
                            backlist = arrayList;
                        }

                        arrayList = getData(where);
                        seach_ = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean ff = false;
                                if(mNaverMap != null) {

                                    // 이전 검색기록있는지 확인 - 있다면 마커 제거 구문문
                                   if(backlist != null && backlist.size() > 0 ) {
                                        for(int i=0; i<backlist.size(); i++) {
                                            Marker data = markerlist.get(i);
                                            data.setPosition(new LatLng(Double.parseDouble(backlist.get(i).getLat()), Double.parseDouble(backlist.get(i).getLongi())));
                                            data.setMap(null);
                                        }
                                    }
                                    if(markerlist != null && markerlist.size() > 0)
                                    {

                                        markerlist.clear(); // 기존 마커 데이터 초기화
                                    }


                                    //검색리스트 지도 표시 반복문
                                    for(DTO d : arrayList) {
                                        Marker marker = new Marker();
                                        marker.setPosition(new LatLng(Double.parseDouble(d.getLat()), Double.parseDouble(d.getLongi())));

                                        //각 마커 마다 클릭시 이벤트 발생 문구
                                        marker.setOnClickListener(new Overlay.OnClickListener() {
                                            @Override
                                            public boolean onClick(@NonNull Overlay overlay) {
                                                Marker ma = (Marker) overlay;
                                                infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(MapShow.this) {    // 각 마커 마다 정보창 발생문구
                                                    @NonNull
                                                    @Override
                                                    protected View getContentView(@NonNull InfoWindow infoWindow) {
                                                        View view = View.inflate(MapShow.this, R.layout.view_info_window, null);
                                                        if(d.getChargeTp().equals("1")) {
                                                            ((TextView)view.findViewById(R.id.title)).setText("충전기 타입 : "+"완속충전기");
                                                        }else if(d.getChargeTp().equals("2")) {
                                                            ((TextView)view.findViewById(R.id.title)).setText("충전기 타입 : "+"급속충전기");
                                                        }

                                                        if(d.getCpStat().equals("1")) {
                                                            ((TextView)view.findViewById(R.id.content)).setText("충전상태가능여부 : 사용가능");
                                                        }else if(d.getCpStat().equals("2")) {
                                                            ((TextView)view.findViewById(R.id.content)).setText("충전상태가능여부 : 사용중");
                                                        }else if(d.getCpStat().equals("3")) {
                                                            ((TextView)view.findViewById(R.id.content)).setText("충전상태가능여부 : 고장/점검중");
                                                        }else {
                                                            ((TextView)view.findViewById(R.id.content)).setText("충전상태가능여부 : 이용불가능");
                                                        }


                                                        return view;
                                                    }
                                                });

                                                if(ma.getInfoWindow() != null) {
                                                    infoWindow.close();
                                                } else {
                                                    infoWindow.open(ma);
                                                    return true;
                                                }
                                                return false;
                                            }
                                        });

                                        //충전사용가능 - 초록아이콘표시
                                        // 충전 사용중/ 불가 - 붉은아이콘표시

                                        marker.setHideCollidedMarkers(true); //마커 겹칠때 ZIndex 가 높은마커를 띄움

                                        if(d.getCpStat().equals("1")) {
                                            if (d.getChargeTp().equals("1")) {
                                                marker.setIcon(OverlayImage.fromResource(R.drawable.ic_baseline_place_24_green));
                                                marker.setZIndex(4);
                                            }
                                            else if(d.getChargeTp().equals("2")) {
                                                marker.setIcon(OverlayImage.fromResource(R.drawable.ic_power));
                                                marker.setZIndex(4);
                                            }
                                        }else {
                                            if(d.getChargeTp().equals("2")) {
                                                marker.setIcon(OverlayImage.fromResource(R.drawable.ic_power_red));
                                                marker.setZIndex(2);
                                            } else if(d.getChargeTp().equals("1")) {
                                                marker.setIcon(OverlayImage.fromResource(R.drawable.ic_baseline_place_24_red));
                                                marker.setZIndex(1);
                                            }

                                        }

                                        markerlist.add(marker);
                                        marker.setMap(mNaverMap);
                                    }
                                    CameraUpdate c = null;

                                    //검색어에 따라 각검색어 동사무소 읍내사무소로 이동구문
                                    for(int i=0; i<dosi.length; i++) {
                                        if(where != null && !where.equals("") && dosi[i].equals(where)) {
                                            c = CameraUpdate.scrollAndZoomTo(new LatLng(lat[i],longi[i]), 12).animate(CameraAnimation.Fly, 3000);
                                            mNaverMap.moveCamera(c);
                                            ff = true;
                                            break;
                                        }
                                    }

                                    //검색한 도시가 DB에 없을때 Toast 메세지 띄움
                                    if(!ff) {
                                        //받아온 지역 전기충전소 마커삭제 문구
                                        for(int i=0; i<markerlist.size(); i++) {
                                            Marker ma = markerlist.get(i);
                                            ma.setPosition(new LatLng(Double.parseDouble(arrayList.get(i).getLat()), Double.parseDouble(arrayList.get(i).getLongi())));
                                            ma.setMap(null);
                                        }

                                        arrayList = null;   // 검색도시 null값 부여 ( 도시명이 아닐때 초기화 ).
                                        backlist = null;    // 검색도시 오타검색했을시 오타검색도시정보들어가있음 초기화필수.
                                        markerlist.clear(); // create 에서 객체선언 했기에 null값을 주는게 아니라 clear 처리.

                                        Toast.makeText(getApplicationContext(), "도시명을 다시 입력해주세요.",Toast.LENGTH_SHORT).show();
                                        c = CameraUpdate.scrollAndZoomTo(new LatLng(mLocationSource.getLastLocation().getLatitude(),
                                                mLocationSource.getLastLocation().getLongitude()),12).animate(CameraAnimation.Fly,3000);
                                        mNaverMap.moveCamera(c);
                                    }




                                }

                            }
                        });
                    }
                }).start();

            }
        });


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.maps);
        if(mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.maps, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady: ");

        //마커 정보가 표시되어 있을때 지도 아무부분 클릭하면 마커정보 닫기
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                if(infoWindow != null) {
                    infoWindow.close();
                }
            }
        });



        mNaverMap = naverMap;

        mNaverMap.setLocationSource(mLocationSource);


        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setLogoGravity(Gravity.RIGHT| Gravity.BOTTOM);

        LocationButtonView locationButtonView = findViewById(R.id.location);

        locationButtonView.setMap(mNaverMap);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaverMap = null;
        arrayList = null;
        backlist = null;
        markerlist = null;
    }

    //전기차 충전소 data.co.kr api xml로 받아오는구문
    ArrayList<DTO> getData(String data) {
           ArrayList<DTO> items = new ArrayList<>();
        DTO item = new DTO();
//        String where = data;
        url = "http://openapi.kepco.co.kr/service/EvInfoServiceV2/getEvSearchList?addr=" + data+
                "&pageNo=1&numOfRows=500&ServiceKey="+key;
        try {
            URL ur = new URL(url);
            InputStream is = ur.openStream();


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is,"UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {

                String tag = "";


                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "파싱시작");
                        break;

                    case XmlPullParser.START_TAG :
                        tag = parser.getName();

                        if(tag.equals("item"));
                        else if(tag.equals("addr")){
                            parser.next();
                            item.setAddr(parser.getText());
                            Log.d(TAG, "주소"+parser.getText());
                        }
                        else if(tag.equals("chargeTp")){
                            parser.next();
                            item.setChargeTp(parser.getText());
                            Log.d(TAG, "충전소 타입"+parser.getText());
                        }
                        else if(tag.equals("cpId")){
                            parser.next();
                            item.setCpld(parser.getText());
                            Log.d(TAG, "충전소ID"+parser.getText());
                        }
                        else if(tag.equals("cpNm")){
                            parser.next();
                            item.setCpNm(parser.getText());
                            Log.d(TAG, "충전기 명칭"+parser.getText());
                        }
                        else if(tag.equals("cpStat")){
                            parser.next();
                            item.setCpStat(parser.getText());
                            Log.d(TAG, "충전기 상태코드"+parser.getText());
                        }
                        else if(tag.equals("cpTp")){
                            parser.next();
                            item.setCpTp(parser.getText());
                            Log.d(TAG, "충전 방식"+parser.getText());
                        }
                        else if(tag.equals("csId")){
                            parser.next();
                            item.setCsld(parser.getText());
                            Log.d(TAG, "충전소ID"+parser.getText());
                        }
                        else if(tag.equals("csNm")){
                            parser.next();
                            item.setCpNm(parser.getText());
                            Log.d(TAG, "충전소 명칭"+parser.getText());
                        }
                        else if(tag.equals("lat")){
                            parser.next();
                            item.setLat(parser.getText());
                            Log.d(TAG, "위도"+parser.getText());
                        }
                        else if(tag.equals("longi")){
                            parser.next();
                            item.setLongi(parser.getText());
                            Log.d(TAG, "경도"+parser.getText());
                        }
                        else if(tag.equals("statUpdateDatetime")) {
                            parser.next();
                            item.setStatUpdateDatetime(parser.getText());
                            items.add(item);
                            Log.d(TAG, "업데이트순위"+parser.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if(tag.equals("item")) {
                            item = new DTO();
                            Log.d(TAG, "-------------------------------\n");
                        }
                        break;
                }

                eventType = parser.next();
            }


        }catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
    
}
















