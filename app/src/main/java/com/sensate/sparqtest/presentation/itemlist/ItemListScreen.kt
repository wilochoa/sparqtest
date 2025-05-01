package com.sensate.sparqtest.presentation.itemlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sensate.sparqtest.domain.model.Item
import com.sensate.sparqtest.presentation.common.UiState
import com.sensate.sparqtest.presentation.components.ItemCard
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.launch

/**
 * Screen to display a list of items.
 * @param onItemClick Callback for when an item is clicked.
 * @param viewModel The ViewModel for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(
    onItemClick: (String) -> Unit,
    viewModel: ItemListViewModel = hiltViewModel()
) {
    val itemsState by viewModel.itemsState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val networkMessage by viewModel.networkMessage.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Track if we've triggered a refresh from the pull gesture
    var hasTriggeredRefresh by remember { mutableStateOf(false) }
    
    // Show network message in snackbar if available
    LaunchedEffect(networkMessage) {
        networkMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    // When user pulls past threshold, trigger refresh
    LaunchedEffect(pullRefreshState.distanceFraction) {
        if (pullRefreshState.distanceFraction >= 1.0f && !hasTriggeredRefresh && !isRefreshing) {
            hasTriggeredRefresh = true
            viewModel.refreshItems()
        }
    }
    
    // Reset the trigger flag when refreshing completes or times out
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && hasTriggeredRefresh) {
            coroutineScope.launch {
                pullRefreshState.animateToHidden()
                hasTriggeredRefresh = false
            }
        }
    }
    
    // Safety timeout for refresh operation
    LaunchedEffect(hasTriggeredRefresh) {
        if (hasTriggeredRefresh) {
            // After 5 seconds, force reset the pull refresh state if still refreshing
            kotlinx.coroutines.delay(5000)
            if (hasTriggeredRefresh) {
                coroutineScope.launch {
                    pullRefreshState.animateToHidden()
                    hasTriggeredRefresh = false
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Items") }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = Color.Yellow,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(data.visuals.message)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { 
                if (!hasTriggeredRefresh && !isRefreshing) {
                    hasTriggeredRefresh = true
                    viewModel.refreshItems()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pullRefreshState
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (itemsState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    is UiState.Success -> {
                        val items = (itemsState as UiState.Success<List<Item>>).data
                        ItemList(
                            items = items,
                            onItemClick = onItemClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    is UiState.Error -> {
                        val errorMessage = (itemsState as UiState.Error).message
                        OfflineErrorMessage(
                            message = errorMessage,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable to display a list of items.
 * @param items The list of items to display.
 * @param onItemClick Callback for when an item is clicked.
 * @param modifier Modifier for the list.
 */
@Composable
private fun ItemList(
    items: List<Item>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = "No items available",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            items(items) { item ->
                ItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

/**
 * Composable to display an offline error message.
 * @param message The error message to display.
 * @param modifier Modifier for the layout.
 */
@Composable
private fun OfflineErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(48.dp)
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 