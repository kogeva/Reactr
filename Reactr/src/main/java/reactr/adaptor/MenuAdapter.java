package reactr.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.reactr.R;
import com.example.reactr.reactr.models.MenuItem;

import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<MenuItem> objects;
    private int select_position;

    public MenuAdapter(Context ctx, ArrayList<MenuItem> menuItems) {
        this.objects = menuItems;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        select_position = -1;
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null)
            view = inflater.inflate(R.layout.menu_item, parent, false);

        MenuItem menu = getMenuItem(position);

        ((TextView) view.findViewById(R.id.menuNameText)).setText(menu.getName());
        if (menu.getIndex() == MenuItem.MAILBOX && (!menu.getCountNewMessage().equals("0")))
            ((TextView) view.findViewById(R.id.counterText)).setText(menu.getCountNewMessage());
        else
            ((TextView) view.findViewById(R.id.counterText)).setVisibility(view.INVISIBLE);
        if (position >= 4) {
            view.setBackgroundResource(R.drawable.menu_item_style_gray);
        } else {
            view.setBackgroundResource(R.drawable.menu_item_light_gray);
        }
        if (position == select_position) {
            view.setBackgroundResource(R.drawable.menu_item_clicked);
        }
        view.setOnFocusChangeListener(myOnFocusChangeListener);
        return view;
    }

    private MenuItem getMenuItem(int position) {
        return ((MenuItem) getItem(position));
    }

    public void setSelect(int position) {
        select_position = position;
    }

    View.OnFocusChangeListener myOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                ((TextView) v.findViewById(R.id.menuNameText)).setTextColor(Color.BLACK);
            }
        }
    };


}
