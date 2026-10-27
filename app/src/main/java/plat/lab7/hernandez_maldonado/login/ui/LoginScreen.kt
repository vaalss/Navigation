package plat.lab7.hernandez_maldonado.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.lab7.hernandez_maldonado.R
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.LinearProgressIndicator

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    isSyncing: Boolean = false,
    errorMessage: String? = null,
    onStartClick: (String) -> Unit
) {

    var nameInput by rememberSaveable {
        mutableStateOf("")
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column (
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(35.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box (
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image (
                        painter = painterResource(
                            id = R.drawable.rick_and_morty),
                        contentDescription = "Logo Rick and Morty",
                    )
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { newValue ->
                        nameInput = newValue
                    },
                    label = {
                        Text("Nombre")
                    },
                    singleLine = true,
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth()
                )

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {onStartClick(nameInput)},
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    enabled = nameInput.isNotBlank() && !isSyncing,
                ) {
                    Text(
                        text = if (isSyncing) "Sincronizando..." else "Empezar",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (isSyncing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text (
                text = "Valeria Hernández - 25086",
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
    RickAndMortyAppTheme {
        LoginScreen(
            onStartClick = {}
        )
    }
}