@file:OptIn(ExperimentalMaterial3Api::class)

package eniso.ia2.drip2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eniso.ia2.drip2.ui.theme.Typography

@Composable
fun MainScreen(
    closetItems: List<ClothingItem>,
    onAddItem: (ClothingItem) -> Unit,
    onClearCloset: () -> Unit,
    onSuggestOutfit: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Top") }
    var style by remember { mutableStateOf("Casual") }
    var season by remember { mutableStateOf("Summer") }

    val types = listOf("Top", "Bottom", "Footwear", "Accessory")
    val styles = listOf("Casual", "Formal", "Sport", "Street")
    val seasons = listOf("Summer", "Winter", "Spring", "Autumn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drip2 - Outfit Planner", style = Typography.headlineLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuField("Type", type, types) { type = it }
            DropdownMenuField("Style", style, styles) { style = it }
            DropdownMenuField("Season", season, seasons) { season = it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    onAddItem(
                        ClothingItem(name, type, color, style, season)
                    )
                    name = ""
                    color = ""
                }) {
                    Text("Add Item")
                }

                Button(onClick = onClearCloset, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Clear Closet")
                }
            }

            Divider()

            Text("Your Closet", style = Typography.titleMedium)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(closetItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${item.name} - ${item.type}", style = Typography.bodyMedium)
                            Text("${item.color}, ${item.style}, ${item.season}")
                        }
                    }
                }
            }

            Button(
                onClick = onSuggestOutfit,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text("Suggest Outfit")
            }
        }
    }
}