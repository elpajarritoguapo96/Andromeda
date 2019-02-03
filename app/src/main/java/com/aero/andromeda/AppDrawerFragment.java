package com.aero.andromeda;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.helpers.OnStartDragListener;
import com.aero.andromeda.helpers.TileTouchHelperCallback;
import com.aero.andromeda.managers.TilesLayoutManager;
import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;

public class AppDrawerFragment extends Fragment implements OnStartDragListener
{
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	private TilesLayoutManager gridLayoutManager;
	private ItemTouchHelper itemTouchHelper;
	private boolean isEnabled = true;
	
	public AppDrawerFragment()
	{
		Bundle args = new Bundle();
		this.setArguments(args);
		
		ServiceLocator.Current().RegisterInstance(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			// mParam1 = getArguments().getString(ARG_PARAM1);
			// mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		UISettings uiSettings = settingsService.getUISettings();
		
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);
		
		this.gridLayoutManager = new TilesLayoutManager(TilesLayoutManager.Orientation.VERTICAL, uiSettings.getLayoutWidth());
		this.gridLayoutManager.setItemOrderIsStable(true);
		
		// GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 6, GridLayoutManager.VERTICAL, false);
		this.tilesAdapter = appService.getTilesAdapter();
		
		this.recyclerView = view.findViewById(R.id.recycler_view);
		this.recyclerView.setLayoutManager(this.gridLayoutManager);
		this.recyclerView.setAdapter(this.tilesAdapter);
		
		ItemTouchHelper.Callback callback = new TileTouchHelperCallback(this.tilesAdapter);
		this.itemTouchHelper = new ItemTouchHelper(callback);
		this.itemTouchHelper.attachToRecyclerView(recyclerView);
		this.tilesAdapter.setDragStartListener(this);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}
	
	public View GetContainer(View view)
	{
		return this.recyclerView.findContainingItemView(view);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	
	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder)
	{
		if (this.itemTouchHelper != null)
			this.itemTouchHelper.startDrag(viewHolder);
	}
	
	public void Enable()
	{
		TileAnimationManager.Current().Start();
		this.isEnabled = true;
	}
	
	public void Disable()
	{
		this.isEnabled = false;
		TileAnimationManager.Current().Stop();
	}
	
	public boolean isEnabled()
	{
		return this.isEnabled;
	}
	
	public void ScrollToTop()
	{
		this.recyclerView.smoothScrollToPosition(0);
	}
	
	public void ScrollToBottom()
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		TilesAdapter tilesAdapter = appService.getTilesAdapter();
		this.recyclerView.smoothScrollToPosition(tilesAdapter.getItemCount() - 1);
	}
	
	public void RefreshLayout(boolean aggressiveRefresh)
	{
		TileFolderManager.Current.CloseFolder();
		
		if (aggressiveRefresh)
		{
			ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
			UISettings uiSettings = settingsService.getUISettings();
			
			if (this.gridLayoutManager.getSpanCount() != uiSettings.getLayoutWidth())
			{
				this.gridLayoutManager = new TilesLayoutManager(TilesLayoutManager.Orientation.VERTICAL, uiSettings.getLayoutWidth());
				this.gridLayoutManager.setItemOrderIsStable(true);
			}
			
			this.recyclerView.setAdapter(null);
			this.recyclerView.setLayoutManager(null);
			this.recyclerView.setAdapter(this.tilesAdapter);
			this.recyclerView.setLayoutManager(this.gridLayoutManager);
			
			synchronized (this.recyclerView)
			{
				this.tilesAdapter.notifyDataSetChanged();
			}
		}
		else
		{
			synchronized (this.recyclerView)
			{
				this.tilesAdapter.notifyDataSetChanged();
			}
		}
	}
}
