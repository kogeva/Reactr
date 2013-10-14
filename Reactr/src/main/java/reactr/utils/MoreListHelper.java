package reactr.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import java.util.ArrayList;

import reactr.adaptor.MessageAdapter;

/**
 * Вспомагательный класс для создания подгружаемых списков.
 *
 * @author aNNiMON
 */
public abstract class MoreListHelper<T> {

    private int itemsPerPage;

    private Activity activity;
    private ArrayList<MessageEntity> items;
    private ArrayAdapter<T> adapter;
    private MessageAdapter messageAdapter;

    private ArrayList<MessageEntity> messageArray;
    private ArrayList<FriendEntity> friendEntities;

    private boolean loadingMore;

    public MoreListHelper(Activity activity, ListView listView,ArrayList<MessageEntity> messageArray,
                          ArrayList<FriendEntity> friendEntities) {


        this(activity, listView, null);
        this.messageArray=new ArrayList<MessageEntity>();
        this.friendEntities=new ArrayList<FriendEntity>();
        this.messageArray.addAll(messageArray);
        this.friendEntities.addAll(friendEntities);
        Log.d("More", "constructor");
    }

    public MoreListHelper(Activity activity, ListView listView, View footerView) {
        itemsPerPage = 10;

        this.activity = activity;
        if (footerView == null) {
            // Создаём footer по умочанию.
            footerView = new TextView(activity);
            ((TextView) footerView).setText("Loading...");
        }

        loadingMore = false;
        items = new ArrayList<MessageEntity>();

        listView.addFooterView(footerView);
        listView.setOnScrollListener(scrollListener);
    }


  /*  public void createAdapter(int layoutResource) {
        adapter = new ArrayAdapter<T>(activity, layoutResource, items);
    }*/

    public MessageAdapter getAdapter() {
        return messageAdapter;
    }
    public MessageAdapter getReactrAdapter() {
        return messageAdapter;
    }

    public void setAdapter(MessageAdapter adapter) {
        this.messageAdapter = adapter;
    }
    /**
     * Задаёт количество элементов на страницу.
     * Используется при загрузке новых элементов для ограничения загружаемых данных.
     * @param value количество элементов
     */
    public void setItemsPerPage(int value) {
        itemsPerPage = value;
    }

    public void loadItems() {
        Thread thread = new Thread(loadMoreListItemsRunnable);
        thread.start();
    }


    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if ( (lastInScreen == totalItemCount) && !loadingMore ){
                Log.d("More", "loadingMore");
                loadItems();
            }
        }
    };

    /**
     * Метод загрузки новых элементов списка.
     * Выполняется в отдельном потоке.
     * @param items список для заполнения
     * @param itemsPerPage количество элементов на страницу (по умолчанию 10)
     */
    public abstract void onLoadItems(ArrayList<MessageEntity> items, int itemsPerPage);

    private Runnable loadMoreListItemsRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("More", "loadMoreListItemsRunnable");
            loadingMore = true;
            items = new ArrayList<MessageEntity>();
            // Загружаем данные.
            onLoadItems(items, itemsPerPage);
            // Обновляем список и адаптер.
            friendEntities.addAll(friendEntities);
            messageArray.addAll(messageArray);

            activity.runOnUiThread(updateAdapterRunnable);
        }
    };

    private Runnable updateAdapterRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("More", "updateAdapterRunnable");
            if ( (items != null) && (items.size() > 0) ){
        //    adapter.addAll(items);
                if(messageAdapter!=null)
                {
                    messageAdapter.addMessages(items);
                    messageAdapter.notifyDataSetChanged();
                    Log.d("More", "notifyDataSetChanged");
                }
            }
         //   adapter.notifyDataSetChanged();

            loadingMore = false;
        }
    };
}
