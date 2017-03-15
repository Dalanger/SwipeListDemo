package com.dl.swipelistviewdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dl.swipelistviewdemo.modle.Info;
import com.dl.swipelistviewdemo.view.SwipeListLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ClickListener{

    private ListView mListView;
    private List<Info> list=new ArrayList<>();
    private ListAdapter listAdapter;
    private Set<SwipeListLayout> sets = new HashSet();
    private ListAdapter.ViewHolder viewHolder;
    private TextView mEdit;
    private boolean clickToShow;
    private AlertDialog dialog;
    private boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = ((ListView) findViewById(R.id.lv_main));
        mEdit = ((TextView) findViewById(R.id.tv_edit));
        initList();
        listAdapter = new ListAdapter(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });
        mListView.setAdapter(listAdapter);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowing) {
                    clickToShow = false;
                    isShowing=false;
                } else {
                    clickToShow=true;
                }
                listAdapter.notifyDataSetChanged();
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i){
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (sets.size()>0) {
                            for (SwipeListLayout s: sets) {
                                s.setStatus(SwipeListLayout.Status.Close,true);
                                sets.remove(s);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


    }

    private void initList() {
        for (int i = 0; i < 20; i++) {
            list.add(new Info("王子"+i,"今天我要去逛街买衣服"));
        }
    }

    @Override
    public void click(int position) {
        Toast.makeText(this, "你点击了"+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void longClick(int position) {
//        Toast.makeText(this, "你长按了"+position, Toast.LENGTH_SHORT).show();
        showDeleteDialog(position);
    }


    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }

    class ListAdapter extends BaseAdapter{
        private ClickListener listener;

        public ListAdapter(ClickListener listener) {
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public Info getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            viewHolder = null;
            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_slid, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);
                viewHolder.slid_item= (SwipeListLayout) view.findViewById(R.id.slid_item);
                viewHolder.tv_top= (TextView) view.findViewById(R.id.tv_top);
                viewHolder.tv_delete= (TextView) view.findViewById(R.id.tv_delete);
                viewHolder.iv_delete= (ImageView) view.findViewById(R.id.iv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
                viewHolder.tv_title.setText(list.get(i).getTitle());
                viewHolder.tv_content.setText(list.get(i).getContent());
                viewHolder.slid_item.setOnSwipeStatusListener(new MyOnSlipStatusListener(viewHolder.slid_item));
                viewHolder.slid_item.setOnTouchListener(new View.OnTouchListener() {

                    private long upTime;
                    private long downTime;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();

                        int xDown = 0, yDown = 0, xUp, yUp;

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                xDown = x;
                                yDown = y;
                                downTime = System.currentTimeMillis();
                                Log.i("click", "down" + xDown + yDown);

                                break;
                            case MotionEvent.ACTION_UP:

                                xUp = x;
                                yUp = y;
                                Log.i("click", "up" + xUp + yUp);
                                int dx = xUp - xDown;
                                int dy = yUp - yDown;
                                Log.i("click", dx + " " + dy);
                                upTime = System.currentTimeMillis();
                                if (upTime - downTime<200) {
                                    listener.click(i);
                                    Log.i("click","点击"+i);
                                } else if (upTime-downTime>800) {
                                    listener.longClick(i);
                                    Log.i("click","长按点击"+i);
                                }

                                break;
                        }
                        return false;
                    }
                });

                viewHolder.tv_top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.slid_item.setStatus(SwipeListLayout.Status.Close,true);
                        Info info=list.get(i);
                        list.remove(i);
                        list.add(0,info);
                        notifyDataSetChanged();
                    }
                });
            if (clickToShow) {
                viewHolder.iv_delete.setVisibility(View.VISIBLE);
                isShowing = true;
            } else {
                viewHolder.iv_delete.setVisibility(View.GONE);
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(i);


                }
            });
            viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.slid_item.setStatus(SwipeListLayout.Status.Close,true);
                    list.remove(i);
                    notifyDataSetChanged();
                }
            });

            return view;
        }
        class ViewHolder{
            private TextView tv_title;
            private TextView tv_content;
            private SwipeListLayout slid_item;
            private TextView tv_top;
            private TextView tv_delete;
            private ImageView iv_delete;
        }

    }

    private void showDeleteDialog(final int i) {
        dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("你确定要删除吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        list.remove(i);
                        listAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


}
