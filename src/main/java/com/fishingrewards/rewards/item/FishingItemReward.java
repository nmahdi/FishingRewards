package com.fishingrewards.rewards.item;

import com.fishingrewards.rewards.FishingReward;

public class FishingItemReward extends FishingReward {

	private ItemStackContainer itemStackContainer;
	private boolean consumed = false;

	public FishingItemReward(ItemStackContainer itemStackContainer){
		this.itemStackContainer = itemStackContainer;
	}

	public ItemStackContainer getItemStackContainer() {
		return itemStackContainer;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed() {
		this.consumed = true;
	}


}
