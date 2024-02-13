package com.example.myapplication.vm.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.home.HomeListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _existingImages = MutableStateFlow<List<HomeListItem>>(emptyList())
    val existingImages: StateFlow<List<HomeListItem>> = _existingImages.asStateFlow()

    private val _dialogShown = MutableStateFlow<Boolean>(false)
    val dialogShown: StateFlow<Boolean> = _dialogShown.asStateFlow()

    private val _imageSliderShown = MutableStateFlow<Boolean>(false)
    val imageSliderShown: StateFlow<Boolean> = _imageSliderShown.asStateFlow()

    private val _imageSliderIndex = MutableStateFlow<Int?>(null)
    val imageSliderIndex: StateFlow<Int?> = _imageSliderIndex.asStateFlow()

    fun addNewImages(newImages: List<HomeListItem>) {
        _existingImages.value += newImages
    }

    fun showDialog() {
        _dialogShown.value = true
    }

    fun dismissDialog() {
        _dialogShown.value = false
    }

    fun showImageSlider(imageIndex: Int) {
        _imageSliderShown.value = true
        _imageSliderIndex.value = imageIndex
    }

    fun dismissImageSlider() {
        _imageSliderShown.value = false
    }
}