package com.example.myapplication.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val addNewImages: (List<HomeListItem>) -> Unit = { newImages ->
        homeViewModel.addNewImages(newImages)
    }
    val showDialog: () -> Unit = {
        homeViewModel.showDialog()
    }
    val dismissDialog: () -> Unit = {
        homeViewModel.dismissDialog()
    }

    HomeScreenContent(
        existingImages = existingImages,
        dialogShown = dialogShown,
        addNewImages = addNewImages,
        showDialog = showDialog,
        dismissDialog = dismissDialog
    )
}


@Composable
private fun HomeScreenContent(
    existingImages: List<HomeListItem>,
    dialogShown: Boolean,
    addNewImages: (List<HomeListItem>) -> Unit,
    showDialog: () -> Unit,
    dismissDialog: () -> Unit,
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
                .padding(8.dp)
        ) {
            Text(buttonText)
        }

        Column(
            Modifier.fillMaxSize(),
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
                    items(existingImages) { listItem ->
                        GalleryImage(item = listItem)
                    }
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