package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import kotlin.random.Random

data class ListItem(
    val height: Dp,
    val imageRes: Int,
)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val imageResources = listOf<Int>(
        R.drawable.nature_1,
        R.drawable.nature_2,
        R.drawable.nature_3,
        R.drawable.nature_4,
        R.drawable.nature_5,
        R.drawable.nature_6,
        R.drawable.nature_7,
        R.drawable.nature_8,
        R.drawable.nature_9,
        R.drawable.nature_10,
    )
    val items = (1..100).map {
        ListItem(
            height = Random.nextInt(100, 300).dp,
            imageRes = imageResources.random(),
        )
    }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalItemSpacing = 20.dp
    ) {
        items(items.size) { index ->
            val galleryItem = items[index]
            GalleryImage(item = galleryItem)
        }
    }
}

@Composable
fun GalleryImage(
    item: ListItem,
    modifier: Modifier = Modifier
) {
    val painter = painterResource(id = item.imageRes)
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