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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.model.home.HomeListItem
import com.example.myapplication.vm.home.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    maxSelectionCount: Int = 1
) {
    val existingImages by homeViewModel.existingImages.collectAsState()

    val buttonText = "이미지 불러오기"

    val dialogShown = remember { mutableStateOf(false) }
    val dialogMessage = "최대 $maxSelectionCount 개의 이미지를 불러올 수 있습니다."

    var isFirstTime = false

    LaunchedEffect(Unit) {
        isFirstTime = true
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            homeViewModel.addNewImages(
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
            homeViewModel.addNewImages(
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
    if (dialogShown.value) {
        AlertDialog(
            onDismissRequest = { dialogShown.value = false },
            title = { Text(text = "알림")},
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    dialogShown.value = false
                    isFirstTime = false
                    launchPhotoPicker()
                }) {
                    Text(text = "확인")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        Button(
            onClick = {
                if (isFirstTime) {
                    dialogShown.value = true
                } else {
                    dialogShown.value = false
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
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalItemSpacing = 20.dp
        ) {
            items(existingImages) { listItem ->
                GalleryImage(item = listItem)
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