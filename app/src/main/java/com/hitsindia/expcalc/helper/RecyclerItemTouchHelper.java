package com.hitsindia.expcalc.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.hitsindia.expcalc.adapters.ExpenseListAdapter;
import com.hitsindia.expcalc.enums.ButtonsState;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 300;
    private boolean swipeBack = false;
    private RectF buttonInstance = new RectF();
    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }
 
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        return true;
    }
 
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ExpenseListAdapter.ExpenseListViewHolder) viewHolder).itemView;
 
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }
 
    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ExpenseListAdapter.ExpenseListViewHolder) viewHolder).itemView;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }
 
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ExpenseListAdapter.ExpenseListViewHolder) viewHolder).itemView;
        getDefaultUIUtil().clearView(foregroundView);
    }
 
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ExpenseListAdapter.ExpenseListViewHolder) viewHolder).itemView;
        drawButtons(c, viewHolder);
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }
 
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }
 
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
 
    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
        p.setColor(Color.BLUE);
       // c.drawRoundRect(leftButton, corners, corners, p);
        //drawText("EDIT", c, leftButton, p);

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        //c.drawRoundRect(rightButton, corners, corners, p);
        //drawText("DELETE", c, rightButton, p);

        buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }
    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}