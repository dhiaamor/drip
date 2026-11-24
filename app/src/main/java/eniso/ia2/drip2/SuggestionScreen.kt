package eniso.ia2.drip2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SuggestionScreen(
    closetItems: List<ClothingItem>,
    initialStyle: String,
    suggestion: String,
    onGenerateSuggestion: (String, String) -> Unit
) {
    val styles = listOf("Casual", "Formal", "Sport", "Street")
    val seasons = listOf("Summer", "Winter", "Spring", "Autumn")

    var selectedStyle by remember { mutableStateOf(initialStyle) }
    var selectedSeason by remember { mutableStateOf(seasons[0]) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Select Style:")
        DropdownMenuBox(options = styles, selected = selectedStyle, onSelected = { selectedStyle = it })

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Season:")
        DropdownMenuBox(options = seasons, selected = selectedSeason, onSelected = { selectedSeason = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onGenerateSuggestion(selectedStyle, selectedSeason)
        }) {
            Text("Generate Suggestion")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = suggestion)
    }
}
@Composable
fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

