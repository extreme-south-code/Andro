package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.model.home.HomeListItem
import com.example.myapplication.vm.home.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
) {
    val existingImages by homeViewModel.existingImages.collectAsState()
    val dialogShown by homeViewModel.dialogShown.collectAsState()
    val imageSliderShown by homeViewModel.imageSliderShown.collectAsState()
    val imageSliderIndex by homeViewModel.imageSliderIndex.collectAsState()

    // 상태로써 홈 화면의 블러 처리 여부 관리
    val isHomeScreenBlurred = imageSliderShown

    val addNewImages: (List<HomeListItem>) -> Unit = { newImages ->
        homeViewModel.addNewImages(newImages)
    }
    val showDialog: () -> Unit = {
        homeViewModel.showDialog()
    }
    val dismissDialog: () -> Unit = {
        homeViewModel.dismissDialog()
    }

    val showImageSlider: (Int) -> Unit = { imageIndex ->
        homeViewModel.showImageSlider(imageIndex)
    }

    val dismissImageSlider: () -> Unit = {
        homeViewModel.dismissImageSlider()
    }

    HomeScreenContent(
        existingImages = existingImages,
        dialogShown = dialogShown,
        imageSliderIndex = imageSliderIndex,
        imageSliderShown = imageSliderShown,
        isHomeScreenBlurred = isHomeScreenBlurred,
        addNewImages = addNewImages,
        showDialog = showDialog,
        dismissDialog = dismissDialog,
        showImageSlider = showImageSlider,
        dismissImageSlider = dismissImageSlider,
    )
}


@Composable
private fun HomeScreenContent(
    existingImages: List<HomeListItem>,
    dialogShown: Boolean,
    imageSliderIndex: Int?,
    imageSliderShown: Boolean,
    isHomeScreenBlurred: Boolean, // 블러 처리 여부 상태
    addNewImages: (List<HomeListItem>) -> Unit,
    showDialog: () -> Unit,
    dismissDialog: () -> Unit,
    showImageSlider: (Int) -> Unit,
    dismissImageSlider: () -> Unit,
    maxSelectionCount: Int = 10,
) {

    val buttonText = "이미지 불러오기"
    val dialogMessage = "최대 $maxSelectionCount 개의 이미지를 불러올 수 있습니다."

    val isFirstTime = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isFirstTime.value = true
    }

    val scrollState = rememberLazyStaggeredGridState()

    LaunchedEffect(scrollState.canScrollForward) {
        // Scroll to the bottom when new images are added
        scrollState.scrollToItem(existingImages.size - 1)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            addNewImages(
                listOf(
                    HomeListItem(
                        height = Random.nextInt(100, 300).dp,
                        imageUri = uri
                    )
                )
            )
        }
    )

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = if (maxSelectionCount > 1) maxSelectionCount else 2
        ),
        onResult = { uris ->
            addNewImages(
                uris.map { uri ->
                    HomeListItem(
                        height = Random.nextInt(100, 300).dp,
                        imageUri = uri
                    )
                }
            )
        }
    )

    fun launchPhotoPicker() {
        if (maxSelectionCount > 1) {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    // show dialog
    if (dialogShown) {
        AlertDialog(
            onDismissRequest = { dismissDialog() },
            title = { Text(text = "알림")},
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    dismissDialog()
                    isFirstTime.value = false
                    launchPhotoPicker()
                }) {
                    Text(text = "확인")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = {
                if (isFirstTime.value) {
                    showDialog()
                } else {
                    dismissDialog()
                    launchPhotoPicker()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
            ),
            modifier = Modifier
                .padding(8.dp),
        ) {
            Text(buttonText)
        }

        // Blur radius 값 설정
        val blurredAlpha = if (isHomeScreenBlurred) 0.3f else 1f // 블러 적용 여부에 따라 투명도 조절

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = blurredAlpha // 블러 적용
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (existingImages.isEmpty()) {
                Text(
                    text = "사진을 채워주세요",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalItemSpacing = 20.dp,
                ) {
                    itemsIndexed(existingImages) {index, listItem ->
                        GalleryImage(
                            item = listItem,
                            modifier = Modifier.clickable {
                                // Show the image slider popup when an image is clicked
                                showImageSlider(index)
                            }
                        )
                    }
                }
            }
            // Show the dialog when dialog when dialogShown is true
            if (imageSliderShown) {
                imageSliderIndex?.let { index ->
                    ImageDialogPopup(
                        images = existingImages,
                        initialImageIndex = index,
                        onClose = dismissImageSlider
                    )
                }
            }
        }
    }
}


@Composable
fun GalleryImage(
    item: HomeListItem,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(model = item.imageUri)
    val description = null

    Surface(
        modifier = modifier
            .shadow(elevation = 15.dp, shape = RoundedCornerShape(10))
            .clip(RoundedCornerShape(10)),
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = description,
            modifier = Modifier
                .fillMaxSize()
                .height(item.height)
                .clip(RoundedCornerShape(10))
        )
    }
}

@Composable
fun ImageDialogPopup(
    images: List<HomeListItem>,
    initialImageIndex: Int,
    onClose: () -> Unit,
    imageSize: Dp = 500.dp, // 원하는 이미지 크기
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var currentIndex by rememberSaveable {
        mutableIntStateOf(initialImageIndex)
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onClose() }
    ) {
        Dialog(onDismissRequest = onClose) {
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(images[currentIndex].imageUri),
                    contentDescription = null,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(imageSize)
                        .clickable { /* Do nothing when the image is clicked */ },
                    contentScale = ContentScale.Crop,
                )

                // Left arrow
                if (currentIndex > 0) {
                    IconButton(
                        onClick = { currentIndex =  (currentIndex - 1).coerceIn(0, images.size - 1) },
                        modifier = modifier
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Image",
                            modifier = modifier
                                .size(48.dp),
                            tint = Color.LightGray
                        )
                    }
                }

                if (currentIndex < images.size - 1) {
                    // Right arrow
                    IconButton(
                        onClick = { currentIndex = (currentIndex + 1).coerceIn(0, images.size - 1) },
                        modifier = modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Image",
                            modifier = modifier
                                .size(48.dp),
                            tint = Color.LightGray,
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenContentPreview() {
    val existingImages = listOf<HomeListItem>(
        HomeListItem(height = 100.dp, imageUri = Uri.parse("https://i.pinimg.com/236x/26/cc/4f/26cc4f2ec693fb121ff082442cc462b5.jpg")),
        HomeListItem(height = 150.dp, imageUri = Uri.parse("https://i.pinimg.com/236x/26/cc/4f/26cc4f2ec693fb121ff082442cc462b5.jpg")),
        HomeListItem(height = 200.dp, imageUri = Uri.parse("https://i.pinimg.com/236x/26/cc/4f/26cc4f2ec693fb121ff082442cc462b5.jpg")),
    )
    val dialogShown = false
    val imageSliderShown = false
    val imageSliderIndex = null

    HomeScreenContent(
        existingImages = existingImages,
        dialogShown = dialogShown,
        imageSliderIndex = imageSliderIndex,
        imageSliderShown = imageSliderShown,
        isHomeScreenBlurred = imageSliderShown,
        addNewImages = {},
        showDialog = {},
        dismissDialog = {},
        showImageSlider = {},
        dismissImageSlider = {},
    )
}