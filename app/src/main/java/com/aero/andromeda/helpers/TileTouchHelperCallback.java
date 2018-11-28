package com.aero.andromeda.helpers;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.adapters.ItemTouchHelperAdapter;
import com.aero.andromeda.managers.TilesLayoutManager;
import com.aero.andromeda.ui.FolderViewHolder;
import com.aero.andromeda.ui.ItemTouchHelperViewHolder;

public class TileTouchHelperCallback extends ItemTouchHelper.Callback
{
	public static final float ALPHA_FULL = 1.0f;
	
	private final ItemTouchHelperAdapter mAdapter;
	
	public TileTouchHelperCallback(ItemTouchHelperAdapter adapter)
	{
		mAdapter = adapter;
	}
	
	@Override
	public boolean isLongPressDragEnabled()
	{
		return Andromeda.isEditMode;
	}
	
	@Override
	public boolean isItemViewSwipeEnabled()
	{
		return false;
	}
	
	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
	{
		if (viewHolder instanceof FolderViewHolder)
			return makeMovementFlags(0, 0);
		
		// Set movement flags based on the layout manager
		if (recyclerView.getLayoutManager() instanceof GridLayoutManager || recyclerView.getLayoutManager() instanceof TilesLayoutManager)
		{
			final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
			final int swipeFlags = 0;
			
			return makeMovementFlags(dragFlags, swipeFlags);
		}
		else
		{
			final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
			final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
			
			return makeMovementFlags(dragFlags, swipeFlags);
		}
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target)
	{
		if (source.getItemViewType() != target.getItemViewType())
		{
			return false;
		}
		
		// Notify the adapter of the move
		mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
		return true;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int i)
	{
		// Notify the adapter of the dismissal
		mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
	}
	
	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
	{
		if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
		{
			// Fade out the view as it is swiped out of the parent's bounds
			final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
			viewHolder.itemView.setAlpha(alpha);
			viewHolder.itemView.setTranslationX(dX);
		}
		else
		{
			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		}
	}
	
	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
	{
		// We only want the active item to change
		if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
		{
			if (viewHolder instanceof ItemTouchHelperViewHolder)
			{
				// Let the view holder know that this item is being moved or dragged
				ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
				itemViewHolder.onItemSelected();
			}
		}
		
		super.onSelectedChanged(viewHolder, actionState);
	}
	
	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
	{
		super.clearView(recyclerView, viewHolder);
		
		viewHolder.itemView.setAlpha(ALPHA_FULL);
		
		if (viewHolder instanceof ItemTouchHelperViewHolder)
		{
			// Tell the view holder it's time to restore the idle state
			ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
			itemViewHolder.onItemClear();
		}
	}
}