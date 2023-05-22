package com.example.pbmprojectfinal

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pbmprojectfinal.ui.data.NoteViewModel
import com.example.pbmprojectfinal.ui.screen.AboutScreen
import com.example.pbmprojectfinal.ui.screen.NoteAddScreen
import com.example.pbmprojectfinal.ui.screen.NoteEditScreen
import com.example.pbmprojectfinal.ui.screen.NoteMainScreen

enum class MainScreen(@StringRes val title: Int) {
    AddNote(R.string.add_notes),
    ListNote(R.string.list_notes),
    EditNote(R.string.edit_note),
    About(R.string.about)
}

@Composable
fun NoteAppTopBar(
    currentScreen: MainScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        actions = {
            IconButton(onClick = onAboutClicked) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colors.onSurface, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colors.surface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )

}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NoteApp (
    noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.factory)
)   {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = MainScreen.valueOf(
        backStackEntry?.destination?.route
            ?.takeIf { route -> MainScreen.values().any { it.name == route } }
            ?: MainScreen.EditNote.name
    )

    Scaffold (
        topBar = {
            NoteAppTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onAboutClicked = { navController.navigate(MainScreen.About.name) },
            )
        }
    ){ innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MainScreen.ListNote.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = MainScreen.ListNote.name) {
                NoteMainScreen(
                    noteViewModel = noteViewModel,
                    onAddNoteClick = {
                        navController.navigate(MainScreen.AddNote.name)
                    },
                    onEditNoteClick = { noteId ->
                        navController.navigate("${MainScreen.EditNote.name}/$noteId")
                    }
                )
            }

            composable(route = "${MainScreen.EditNote.name}/{noteId}") { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                if (noteId != null) {
                    NoteEditScreen(
                        noteId = noteId,
                        noteViewModel = noteViewModel,
                        onDone = { navController.navigate(MainScreen.ListNote.name) }
                    )
                } else {
                    NoteEditScreen(
                        noteId = 1,
                        noteViewModel = noteViewModel,
                        onDone = { navController.navigate(MainScreen.ListNote.name) }
                    )
                }
            }

            composable(route = MainScreen.AddNote.name) {
                NoteAddScreen(noteViewModel = noteViewModel)
            }

            composable(route = MainScreen.About.name) {
                AboutScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NoteApp()
}