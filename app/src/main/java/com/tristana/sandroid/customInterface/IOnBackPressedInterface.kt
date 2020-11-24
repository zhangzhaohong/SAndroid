package com.tristana.sandroid.customInterface

interface IOnBackPressedInterface {
    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     * @return true if your processing has priority if not false
     */
    fun onBackPressed(): Boolean
}