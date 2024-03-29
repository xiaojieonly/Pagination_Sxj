package com.example.lwy.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sxj.paginationlib.PaginationRecycleView;
import com.sxj.paginationlib.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PaginationRecycleView.Adapter.OnItemClickListener {


        private PaginationRecycleView mPaginationRcv;
    private CustomAdapter mAdapter;
    private int[] perPageCountChoices = {10, 20, 30, 50};

        private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            List<JSONObject> data = (List<JSONObject>) msg.obj;
            mAdapter.setDatas(msg.arg1, data);
            mPaginationRcv.setState(PaginationRecycleView.SUCCESS);
        }
    };
    private int mPerPageCount;
//    private PaginationIndicator mIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mIndicatorView = (PaginationIndicator) findViewById(R.id.indicator);
//        mIndicatorView.setTotalCount(99);
//        mIndicatorView.setPerPageCountChoices(perPageCountChoices);
//        mIndicatorView.setListener(new PaginationIndicator.OnChangedListener() {
//            @Override
//            public void onPageSelectedChanged(int currentPapePos, int lastPagePos, int totalPageCount, int total) {
//                Toast.makeText(MainActivity.this, "选中" + currentPapePos + "页", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onPerPageCountChanged(int perPageCount) {
//
//            }
//        });

        mPaginationRcv = findViewById(R.id.pagination_rcv);

        mAdapter = new CustomAdapter(this, 99);
        mPaginationRcv.setAdapter(mAdapter);
//        mPaginationRcv.setPerPageCountChoices(perPageCountChoices);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mPaginationRcv.setLayoutManager(layoutManager);
        mPaginationRcv.setListener(new PaginationRecycleView.Listener() {
            @Override
            public void loadMore(int currentPagePosition, int nextPagePosition, int perPageCount, int dataTotalCount) {
                final int loadPos = nextPagePosition;
                mPerPageCount = perPageCount;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(800);
                            Message msg = Message.obtain();
                            msg.obj = geneDatas(loadPos, mPerPageCount);
                            msg.arg1 = loadPos;
                            mHandler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onPerPageCountChanged(int perPageCount) {

            }
        });
        mAdapter.setOnItemClickListener(this);

    }

    /**
     * 产生假数据
     *
     * @param currentPagePosition
     * @param perPageCount
     * @return
     */
    public List<JSONObject> geneDatas(int currentPagePosition, int perPageCount) {
        int from = (currentPagePosition - 1) * perPageCount;
        List<JSONObject> datas = new ArrayList<>();
        try {
            for (int i = 0; i < perPageCount; i++) {
                JSONObject json = new JSONObject();

                json.put("name", "测试<" + (from++) + ">");

                datas.add(json);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return datas;
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        JSONObject item = mAdapter.getCurrentPageItem(position);  // 此处position返回的是recycleview的位置，所以取当前页显示列表的项
        Toast.makeText(this, item.optString("name"), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    class CustomAdapter extends PaginationRecycleView.Adapter<JSONObject, ViewHolder> {


        private Context mContext;

        public CustomAdapter(Context context, int dataTotalCount) {
            super(dataTotalCount);
            mContext = context;
        }


        @Override
        public void bindViewHolder(ViewHolder viewholder, JSONObject data) {
            viewholder.setText(R.id.text, data.optString("name"));
        }

        @Override
        public ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewTypea) {
            return ViewHolder.createViewHolder(mContext, parent, R.layout.item_list);
        }
    }


}
