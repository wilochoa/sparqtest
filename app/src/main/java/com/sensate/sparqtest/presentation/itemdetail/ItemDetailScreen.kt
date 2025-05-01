package com.sensate.sparqtest.presentation.itemdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sensate.sparqtest.R
import com.sensate.sparqtest.domain.model.Item
import com.sensate.sparqtest.presentation.common.UiState

/**
 * Screen to display details of a specific item.
 * @param onBackClick Callback for when the back button is clicked.
 * @param viewModel The ViewModel for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    onBackClick: () -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val itemState by viewModel.itemState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (itemState is UiState.Success) {
                        Text((itemState as UiState.Success<Item>).data.title)
                    } else {
                        Text(stringResource(R.string.item_details))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (itemState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is UiState.Success -> {
                    val item = (itemState as UiState.Success<Item>).data
                    ItemDetail(
                        item = item,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                is UiState.Error -> {
                    val errorMessage = (itemState as UiState.Error).message
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

/**
 * Composable to display the details of an item.
 * @param item The item to display details for.
 * @param modifier Modifier for the layout.
 */
@Composable
private fun ItemDetail(
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailSection(
                    content = item.description
                )
            }
        }
    }
}

/**
 * Composable for displaying a labeled section of content.
 * @param content The content to display.
 */
@Composable
private fun DetailSection(
    content: String
) {
    Column {
        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 