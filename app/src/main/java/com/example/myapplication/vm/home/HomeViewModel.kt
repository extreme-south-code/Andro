package com.example.myapplication.vm.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.home.HomeListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _existingImages = MutableStateFlow<List<HomeListItem>>(emptyList())
    val existingImages: StateFlow<List<HomeListItem>> = _existingImages.asStateFlow()

    fun addNewImages(newImages: List<HomeListItem>) {
        _existingImages.value += newImages
    }
}