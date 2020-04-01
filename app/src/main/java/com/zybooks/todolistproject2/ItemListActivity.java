package com.zybooks.todolistproject2;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zybooks.todolistproject2.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements ItemListTextDialogFragment.ItemDialogListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ItemListTextDialogFragment textDialogFragment = new ItemListTextDialogFragment();
    private AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
    private DeleteConfirmationDialogFragment deleteConfirmationDialogFragment = new DeleteConfirmationDialogFragment();
    private String TAG = "GESTURELISTENER: ";

    private ArrayList<GestureDetectorCompat> mDetectorList;
    private GestureDetectorCompat mDetector;
    private GestureDetector anotherDetector;
    public int currentItemPosition;

    public class DeleteMode{
        private boolean deleteMode = false;

        public boolean isActive(){
            return deleteMode;
        }

        public void changeMode(){
            deleteMode = deleteMode ? false : true;
        }
    }

    public class DeleteConfirmation{
        private boolean didConfirm = false;

        public boolean isActive(){ return didConfirm;}

        public void confirm(){

            if(deleteMode.isActive()){

                DummyContent.ITEM_MAP.remove(currentItemPosition);
                DummyContent.ITEMS.remove(currentItemPosition);

                reorganizeItems();
                FindAndSetupRecyclerView();
            }

        }

    }

    public DeleteMode deleteMode;
    public static DeleteConfirmation deleteConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mDetector = new GestureDetectorCompat(this, new GestureListener());
        deleteMode = new DeleteMode();
        deleteConfirmation = new DeleteConfirmation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(deleteMode.isActive() == false) {
                    openItemDialog();
                }
            }
        });


        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        FindAndSetupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add:
                openItemDialog();
                return true;

            case R.id.action_delete:
                //activate a delete mode that deletes dummycontent.items and dummy content.itemmap
                deleteMode.changeMode();
                String currentModeString = deleteMode.isActive() ? "Activated" : "Deactivated";
                Snackbar.make(findViewById(R.id.item_list), (CharSequence)"Delete Mode: " + currentModeString , Snackbar.LENGTH_SHORT ).show();
                return true;

            case R.id.action_about:
                openAboutDialog();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
    }

    private void FindAndSetupRecyclerView() {

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    public void listenText(String newItemText) {

        DummyContent.DummyItem newItem = new DummyContent.DummyItem(Integer.toString(DummyContent.ITEMS.size() + 1), newItemText, makeDetails(DummyContent.ITEMS.size() + 1));

        DummyContent.ITEMS.add(newItem);
        DummyContent.ITEM_MAP.put(newItem.id, newItem);

        textDialogFragment.dismiss();
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;
        private Tag currentTag;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane && deleteMode.isActive() == false) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else if(deleteMode.isActive() == false) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };



        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnTouchListener( new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    Log.d(TAG, Integer.toString(position));
                    currentItemPosition = position;
                    mDetector.onTouchEvent(event);
                    return ItemListActivity.super.onTouchEvent(event);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }

    }

    class GestureListener implements GestureDetector.OnGestureListener{


        public boolean TouchEvent(MotionEvent event, int position){



            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling");
            View view;

            float masterX = e1.getX();
            float masterY = e1.getY();

            if(deleteMode.isActive()) {
                openDeleteConfirmationDialog();
            }


            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

    private void openItemDialog(){

        textDialogFragment.show(getSupportFragmentManager(), "New Item Dialog");
    }

    private void openAboutDialog(){

        aboutDialogFragment.show(getSupportFragmentManager(), "About Dialog");
    }

    private void openDeleteConfirmationDialog(){

        deleteConfirmationDialogFragment.show(getSupportFragmentManager(), "Delete Confirmation Dialog");

    }

    private void reorganizeItems(){

        for(int i = 0; i < DummyContent.ITEMS.size(); ++i) {
            DummyContent.DummyItem currentItem = DummyContent.ITEMS.get(i);
            currentItem.id = Integer.toString(i+1);

            DummyContent.ITEMS.add(i, currentItem);
            DummyContent.ITEMS.remove(i+1);
            DummyContent.ITEM_MAP.put(currentItem.id, currentItem);
        }
    }


}

