package com.aero.andromeda.managers;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.ui.BaseTileViewHolder;

public class TileOrderManager
{
	private static TileOrderManager self;
	
	public static TileOrderManager Current()
	{
		if (self == null)
		{
			self = new TileOrderManager();
		}
		
		return self;
	}
	
	private final IAppService appService;
	private final TilesAdapter tilesAdapter;
	private final TileAnimationManager tileAnimationManager;
	private final AppDrawerFragment appDrawerFragment;
	private final MainActivity mainActivity;
	
	private BaseTileViewHolder selectTileViewHolder = null;
	
	private TileOrderManager()
	{
		this.appService = ServiceLocator.Current().GetInstance(IAppService.class);
		this.tilesAdapter = this.appService.getTilesAdapter();
		this.tileAnimationManager = ServiceLocator.Current().GetInstance(TileAnimationManager.class);
		this.appDrawerFragment = ServiceLocator.Current().GetInstance(AppDrawerFragment.class);
		this.mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
	}
	
	public void EnterEditMode()
	{
		Andromeda.isEditMode = true;
		this.selectTileViewHolder = null;
		this.tileAnimationManager.Stop();
		this.mainActivity.LockNavigationDrawer();
		this.mainActivity.LockViewPager();
		this.appDrawerFragment.RefreshLayout();
		ServiceLocator.Current().GetInstance(MainActivity.class).CoverDarkBackground();
		
		WobbleAnimationManager.Current(this.tilesAdapter).startWobbleAnimation();
	}
	
	public void ExitEditMode()
	{
		Andromeda.isEditMode = false;
		this.selectTileViewHolder = null;
		WobbleAnimationManager.Current(this.tilesAdapter).stopWobble(true);
		this.tileAnimationManager.Start();
		this.mainActivity.UnlockNavigationDrawer();
		this.mainActivity.UnlockViewPager();
		this.appDrawerFragment.RefreshLayout();
		ServiceLocator.Current().GetInstance(MainActivity.class).UncoverDarkBackground();
	}
	
	public void SelectTile(BaseTileViewHolder tileHolder)
	{
		if (Andromeda.isEditMode)
		{
			if (this.selectTileViewHolder != null)
			{
				BaseTileViewHolder unselectedTileHolder = this.selectTileViewHolder;
				this.selectTileViewHolder = null;
				unselectedTileHolder.onItemUnselected();
			}
			
			this.selectTileViewHolder = tileHolder;
			
			if (this.selectTileViewHolder != null)
				this.selectTileViewHolder.onItemSelected();
		}
	}
	
	public boolean IsSelectedTile(BaseTileViewHolder tileHolder)
	{
		if (!Andromeda.isEditMode)
			return false;
		
		if (this.selectTileViewHolder == null || tileHolder == null)
			return false;
		
		return this.selectTileViewHolder.equals(tileHolder);
	}
	
	public void RefreshSelectedTileHolder(BaseTileViewHolder tileHolder)
	{
		if (this.IsSelectedTile(tileHolder))
			this.selectTileViewHolder = tileHolder;
	}
}