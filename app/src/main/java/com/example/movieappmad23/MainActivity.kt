package com.example.movieappmad23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.movieappmad23.models.Movie
import com.example.movieappmad23.models.getMovies
import com.example.movieappmad23.ui.theme.MovieAppMAD23Theme

class MainActivity : ComponentActivity() {
    private val movieViewModel by viewModels<MovieViewModel>()
    // Define a list of tab titles
    private val tabs = listOf(
        "Home",
        "Favorites"
    )
    // Define a mutable state variable to hold the current tab index
    var currentTab by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view using Jetpack Compose
        setContent {
            MovieAppMAD23Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    // Create a vertical column to hold the UI elements
                    Column {
                        // Create a TabRow to display the tabs
                        TabRow(
                            selectedTabIndex = currentTab,
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ) {
                            // Loop through the tabs and create a Tab for each one
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(title) },
                                    selected = currentTab == index,
                                    onClick = { currentTab = index }
                                )
                            }
                        }

                        // Display either one of the screens which you select
                        if (currentTab == 0) {
                            // Retrieve the list of movies
                            val movies = getMovies()
                            MovieList(movies = movies, movieViewModel = movieViewModel)
                        } else {
                            // Display the FavoriteScreen
                            FavoriteScreen(movieViewModel = movieViewModel)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FavoriteScreen(movieViewModel: MovieViewModel) {
    val favoriteMovies = movieViewModel.favoriteMovies

    LazyColumn {
        items(favoriteMovies) { movie ->
            // FavoriteMovieRow is another composable that renders each movie in the list
            FavoriteMovieRow(movie = movie, movieViewModel = movieViewModel)
        }
    }
}

@Composable
fun FavoriteMovieRow(movie: Movie, movieViewModel: MovieViewModel) {
    val isFavorite = movieViewModel.favoriteMovies.contains(movie)
    // Display movie details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {
        // Display the movie title, poster, and favorite icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the movie poster
            Image(
                painter = painterResource(id = R.drawable.avatar2),
                contentDescription = "Movie Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            )
            // Display movie title
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            // Used to display a favorite icon that toggles the favorite state of the movie when clicked
            Icon(
                tint = MaterialTheme.colors.secondary,
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Add to favorites",
                modifier = Modifier.clickable {
                    movieViewModel.toggleFavorite(movie)
                }
            )
        }
    }
}

class MovieViewModel : ViewModel() {
    private val _favoriteMovies = mutableStateListOf<Movie>()
    val favoriteMovies: List<Movie> = _favoriteMovies

    // add a function to toggle the favorite state of a movie
    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, movieViewModel: MovieViewModel) {
    val favoriteMovies = movieViewModel.favoriteMovies
    val isFavorite: (Movie) -> Boolean = { movie -> favoriteMovies.contains(movie) }
    // Used to render a list of movies.
    LazyColumn {
        // Render each movie in the list
        items(movies) { movie ->
            MovieRow(movie, isFavorite(movie)) {
                // Toggle the favorite state of the movie when the icon is clicked
                movieViewModel.toggleFavorite(movie)
            }
        }
    }
}

@Composable
fun MovieRow(movie: Movie, isFavorite: Boolean, onToggleFavorite: (Movie) -> Unit) {
    // Representing an individual movie item in the list
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar2),
                    contentDescription = "Movie Poster",
                    contentScale = ContentScale.Crop
                )
                // A Box composable wrapping a favorite icon
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        tint = MaterialTheme.colors.secondary,
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        modifier = Modifier.clickable {
                            // A callback to toggle the favorite state of the movie
                            onToggleFavorite(movie)
                        }
                    )
                }
            }
            // Displaying the movie title and a details icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(movie.title, style = MaterialTheme.typography.h6)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Show details"
                )
            }
        }
    }
}