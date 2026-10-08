package com.example.data.repository

import com.example.R
import com.example.data.model.Channel
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.Season
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface VodRepository {
    fun getFeaturedContent(): Flow<List<MediaItem>>
    fun getTrending(): Flow<List<MediaItem>>
    fun getPopularMovies(): Flow<List<MediaItem>>
    fun getPopularSeries(): Flow<List<MediaItem>>
    fun getRecommended(): Flow<List<MediaItem>>
    fun getAllMovies(): Flow<List<MediaItem>>
    fun getAllSeries(): Flow<List<MediaItem>>
    fun getLiveChannels(): Flow<List<Channel>>
    fun getMediaById(id: String): MediaItem?
    fun getChannelById(id: String): Channel?
    fun searchMedia(query: String, filterGenre: String?, filterType: MediaType?): List<MediaItem>
    fun getMovieGenres(): List<String>
    fun getSeriesGenres(): List<String>
    fun getChannelCategories(): List<String>
}

class VodRepositoryImpl : VodRepository {

    private val sampleVideo1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val sampleVideo2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    private val sampleVideo3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val sampleVideo4 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
    private val sampleVideo5 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private val sampleVideo6 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"

    private val mockMediaList: List<MediaItem> = listOf(
        // Featured Sci-Fi Movie
        MediaItem(
            id = "m1",
            title = "Astra Horizon: Echoes of Eternity",
            overview = "When an interstellar expedition intercepts a mysterious tachyon signal from the edge of the known universe, Commander Alex Mercer and their crew navigate anomalous cosmic storms and gravitational rifts to uncover a forgotten cosmic civilization.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1600&auto=format&fit=crop&q=80",
            localBackdropRes = R.drawable.hero_scifi,
            rating = 9.2,
            releaseYear = 2025,
            durationOrSeasons = "2h 28m",
            ageRating = "PG-13",
            genres = listOf("Sci-Fi", "Adventure", "Space"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo2,
            cast = listOf("David Coren", "Elena Rostova", "Marcus Vance", "Sora Takahashi"),
            director = "Denis Villeneuve-inspired",
            isFeatured = true,
            isTrending = true,
            isPopular = true
        ),
        // Featured Cyberpunk Series
        MediaItem(
            id = "s1",
            title = "Neon Syndicate: Sub-Level 9",
            overview = "In a rain-drenched hyper-metropolis where synthetic consciousness is traded on the black market, a renegade data courier and a cybernetic investigator uncover a corporation's plan to rewrite human memory.",
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1600&auto=format&fit=crop&q=80",
            localBackdropRes = R.drawable.hero_neon,
            rating = 8.9,
            releaseYear = 2024,
            durationOrSeasons = "2 Seasons",
            ageRating = "18+",
            genres = listOf("Sci-Fi", "Cyberpunk", "Thriller", "Action"),
            type = MediaType.SERIES,
            videoUrl = sampleVideo1,
            cast = listOf("Kaelen Cross", "Yuki Tanaka", "Seraphina Stone", "Viktor Brandt"),
            director = "Christopher Nolan style",
            isFeatured = true,
            isTrending = true,
            isPopular = true,
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1: Neon Genesis",
                    episodes = listOf(
                        Episode("s1_e1", 1, 1, "The Ghost Carrier", "Courier Ren crosses into Sector 7 carrying an encrypted biometric capsule.", "48m", "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600", sampleVideo1),
                        Episode("s1_e2", 1, 2, "Silicon Alley", "A detective tracks the frequency left behind by an unauthorized neural tap.", "52m", "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600", sampleVideo3),
                        Episode("s1_e3", 1, 3, "Memory Overwrite", "Alliances shatter inside an underground synthetic neural clinic.", "55m", "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600", sampleVideo4),
                        Episode("s1_e4", 1, 4, "High Voltage Protocol", "The grid collapses as the Syndicate initiates black-site protocols.", "50m", "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600", sampleVideo2)
                    )
                ),
                Season(
                    seasonNumber = 2,
                    title = "Season 2: Retribution",
                    episodes = listOf(
                        Episode("s1_e5", 2, 1, "Zero Day Incident", "Six months after the blackout, a ghost node awakens deep in the server vault.", "56m", "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600", sampleVideo5),
                        Episode("s1_e6", 2, 2, "Ghost Protocol", "Ren infiltrates the orbital relay station high above the clouds.", "54m", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", sampleVideo6)
                    )
                )
            )
        ),
        // Movie 2: Shadow Vanguard
        MediaItem(
            id = "m2",
            title = "Shadow Vanguard",
            overview = "An elite tactical extraction unit is trapped behind enemy territory during a high-stakes covert extraction when unexpected geopolitical sabotage unfolds.",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1486572788966-cfd3dfdd4a48?w=1600&auto=format&fit=crop&q=80",
            rating = 8.5,
            releaseYear = 2024,
            durationOrSeasons = "1h 54m",
            ageRating = "16+",
            genres = listOf("Action", "Thriller", "Military"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo5,
            cast = listOf("Jason Holt", "Maya Lin", "Cole Ramirez"),
            director = "Anthony Russo",
            isTrending = true,
            isPopular = true
        ),
        // Movie 3: Kingdom of Frost
        MediaItem(
            id = "m3",
            title = "Kingdom of Frost: The Broken Crown",
            overview = "In the frozen highlands of Eldoria, an exiled queen must unite rival warrior clans against a supernatural blizzard driven by an ancient slumbering titan.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?w=1600&auto=format&fit=crop&q=80",
            rating = 8.8,
            releaseYear = 2024,
            durationOrSeasons = "2h 15m",
            ageRating = "PG-13",
            genres = listOf("Fantasy", "Adventure", "Drama"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo4,
            cast = listOf("Freja Lind", "Garrick Thorne", "Anya Stark"),
            director = "Guillermo del Toro inspired",
            isTrending = true,
            isPopular = true
        ),
        // Movie 4: Midnight Velocity
        MediaItem(
            id = "m4",
            title = "Midnight Velocity",
            overview = "A legendary getaway driver is blackmailed into one final night-long cross-country run with an experimental supercar carrying stolen corporate blueprints.",
            posterUrl = "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=1600&auto=format&fit=crop&q=80",
            rating = 8.1,
            releaseYear = 2025,
            durationOrSeasons = "1h 48m",
            ageRating = "16+",
            genres = listOf("Action", "Crime", "Thriller"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo6,
            cast = listOf("Leo Bennett", "Zoe Saldana style", "Mateo Silva"),
            director = "Justin Lin",
            isPopular = true
        ),
        // Movie 5: The Silent Deep
        MediaItem(
            id = "m5",
            title = "The Silent Deep",
            overview = "A marine research vessel in the Mariana Trench discovers bioluminescent geological organisms that communicate through resonant harmonic pulses.",
            posterUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1600&auto=format&fit=crop&q=80",
            rating = 8.7,
            releaseYear = 2023,
            durationOrSeasons = "2h 05m",
            ageRating = "PG-13",
            genres = listOf("Sci-Fi", "Mystery", "Documentary"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo3,
            cast = listOf("Dr. Susan Albright", "Kenji Sato", "Sarah Miller"),
            director = "James Cameron style",
            isFeatured = false,
            isPopular = true
        ),
        // Movie 6: Crimson Masquerade
        MediaItem(
            id = "m6",
            title = "Crimson Masquerade",
            overview = "A psychological murder mystery set inside a 1920s Venetian palace during Carnival where everyone wears a mask and every witness has a motive.",
            posterUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=1600&auto=format&fit=crop&q=80",
            rating = 8.4,
            releaseYear = 2023,
            durationOrSeasons = "2h 10m",
            ageRating = "16+",
            genres = listOf("Mystery", "Drama", "Crime"),
            type = MediaType.MOVIE,
            videoUrl = sampleVideo1,
            cast = listOf("Julian Vance", "Claire DeWitt", "Lucian Moretti"),
            director = "Rian Johnson style"
        ),
        // Series 2: Quantum Breakout
        MediaItem(
            id = "s2",
            title = "Quantum Breakout",
            overview = "A theoretical physicist trapped in a loop of collapsing parallel realities must find the singular variable that saves humanity from dimensional convergence.",
            posterUrl = "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1600&auto=format&fit=crop&q=80",
            rating = 9.0,
            releaseYear = 2024,
            durationOrSeasons = "3 Seasons",
            ageRating = "16+",
            genres = listOf("Sci-Fi", "Mystery", "Drama"),
            type = MediaType.SERIES,
            videoUrl = sampleVideo2,
            cast = listOf("Ethan Hawke style", "Naomi Watts style", "David Thewlis style"),
            director = "Jonathan Nolan",
            isTrending = true,
            isPopular = true,
            seasons = listOf(
                Season(1, "Season 1: Event Horizon", listOf(
                    Episode("s2_e1", 1, 1, "The Observer Effect", "Dr. Hayes witnesses the initial superposition collapse.", "45m", "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=600", sampleVideo2),
                    Episode("s2_e2", 1, 2, "Entangled Minds", "Two alternate timelines communicate via radio telemetry.", "47m", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", sampleVideo3)
                )),
                Season(2, "Season 2: Multiverse Collapse", listOf(
                    Episode("s2_e3", 2, 1, "Schrödinger's Gate", "The facility unlocks the containment barrier.", "50m", "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600", sampleVideo4)
                ))
            )
        ),
        // Series 3: Iron Dynasty
        MediaItem(
            id = "s3",
            title = "Iron Dynasty",
            overview = "The saga of a legendary rail and industrial empire during the turn of the century as sibling rivalries ignite an epic war for corporate and political dominance.",
            posterUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=1600&auto=format&fit=crop&q=80",
            rating = 8.6,
            releaseYear = 2023,
            durationOrSeasons = "4 Seasons",
            ageRating = "TV-MA",
            genres = listOf("Drama", "History", "Crime"),
            type = MediaType.SERIES,
            videoUrl = sampleVideo3,
            cast = listOf("Sterling Sterling", "Victoria Grey", "Archibald Sterling"),
            director = "Martin Scorsese style",
            isPopular = true,
            seasons = listOf(
                Season(1, "Season 1: The Foundation", listOf(
                    Episode("s3_e1", 1, 1, "The Gilded Spike", "A new continental route is surveyed through contested valleys.", "58m", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600", sampleVideo3)
                ))
            )
        ),
        // Series 4: Mythos: Age of Gods
        MediaItem(
            id = "s4",
            title = "Mythos: Age of Gods",
            overview = "Ancient deities live in secret among modern humans in Athens and Cairo until a cataclysmic solar anomaly threatens to restore their ancient mythical powers.",
            posterUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1600&auto=format&fit=crop&q=80",
            rating = 8.7,
            releaseYear = 2024,
            durationOrSeasons = "1 Season",
            ageRating = "16+",
            genres = listOf("Fantasy", "Action", "Mythology"),
            type = MediaType.SERIES,
            videoUrl = sampleVideo4,
            cast = listOf("Alexander Drake", "Thea Solon", "Orion Grey"),
            director = "Zack Snyder style",
            isTrending = true,
            seasons = listOf(
                Season(1, "Season 1: The Awakening", listOf(
                    Episode("s4_e1", 1, 1, "The Sparks of Olympus", "Lightning strikes the Acropolis on a clear summer night.", "53m", "https://images.unsplash.com/photo-1563089145-599997674d42?w=600", sampleVideo4),
                    Episode("s4_e2", 1, 2, "Echoes in the Sand", "Ancient glyphs glow along the Nile riverbed.", "49m", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600", sampleVideo1)
                ))
            )
        ),
        // Series 5: The Baker Street Code
        MediaItem(
            id = "s5",
            title = "The Baker Street Code",
            overview = "A hyper-modern reimagining of classic detective crime stories involving AI cryptanalysis, global dark web cartels, and high-stakes deduction.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=1600&auto=format&fit=crop&q=80",
            rating = 8.8,
            releaseYear = 2025,
            durationOrSeasons = "2 Seasons",
            ageRating = "16+",
            genres = listOf("Crime", "Mystery", "Drama"),
            type = MediaType.SERIES,
            videoUrl = sampleVideo5,
            cast = listOf("Benedict Style", "Martin Style"),
            director = "Mark Gatiss style",
            isPopular = true,
            seasons = listOf(
                Season(1, "Season 1: Cipher", listOf(
                    Episode("s5_e1", 1, 1, "The Pink Cipher", "A string of encrypted crypto-transactions predicts three unsolved heists.", "59m", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600", sampleVideo5)
                ))
            )
        )
    )

    private val mockChannels: List<Channel> = listOf(
        Channel(
            id = "ch_nova_cinema",
            channelNumber = 101,
            name = "Nova Cinema HD",
            category = "Cinema",
            logoUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400",
            currentShow = "Interstellar Odyssey: Director's Cut",
            showProgress = 0.65f,
            showTime = "14:00 - 16:30",
            nextShow = "The Cyber Heist (16:30)",
            streamUrl = sampleVideo2
        ),
        Channel(
            id = "ch_nova_sports",
            channelNumber = 204,
            name = "Nova Sports One",
            category = "Sports",
            logoUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=400",
            currentShow = "Global Grand Prix 2025: Live Final Lap",
            showProgress = 0.82f,
            showTime = "15:00 - 17:00",
            nextShow = "Championship Post-Race Analysis",
            streamUrl = sampleVideo6
        ),
        Channel(
            id = "ch_nova_news",
            channelNumber = 301,
            name = "Nova 24 Global News",
            category = "News",
            logoUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=400",
            currentShow = "World Today: Tech & Future Horizons",
            showProgress = 0.35f,
            showTime = "15:30 - 16:00",
            nextShow = "Global Financial Bulletin",
            streamUrl = sampleVideo1
        ),
        Channel(
            id = "ch_nova_nature",
            channelNumber = 405,
            name = "Planet Wild 4K",
            category = "Documentary",
            logoUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400",
            currentShow = "Depths of the Arctic Abyss",
            showProgress = 0.48f,
            showTime = "15:15 - 16:15",
            nextShow = "Serengeti: The Great Migration",
            streamUrl = sampleVideo3
        ),
        Channel(
            id = "ch_nova_music",
            channelNumber = 502,
            name = "Vivid Beats Live",
            category = "Music",
            logoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
            currentShow = "Synthwave & Electronic Festival 2025",
            showProgress = 0.70f,
            showTime = "14:45 - 17:00",
            nextShow = "Top 40 Global Hits Countdown",
            streamUrl = sampleVideo4
        ),
        Channel(
            id = "ch_nova_action",
            channelNumber = 108,
            name = "Adrenaline TV",
            category = "Action",
            logoUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400",
            currentShow = "Extreme Tactical Operations",
            showProgress = 0.20f,
            showTime = "15:40 - 17:10",
            nextShow = "Speed Demons: Turbo Series",
            streamUrl = sampleVideo5
        )
    )

    override fun getFeaturedContent(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.isFeatured })
    override fun getTrending(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.isTrending })
    override fun getPopularMovies(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.type == MediaType.MOVIE && it.isPopular })
    override fun getPopularSeries(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.type == MediaType.SERIES && it.isPopular })
    override fun getRecommended(): Flow<List<MediaItem>> = flowOf(mockMediaList.sortedByDescending { it.rating })
    override fun getAllMovies(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.type == MediaType.MOVIE })
    override fun getAllSeries(): Flow<List<MediaItem>> = flowOf(mockMediaList.filter { it.type == MediaType.SERIES })
    override fun getLiveChannels(): Flow<List<Channel>> = flowOf(mockChannels)

    override fun getMediaById(id: String): MediaItem? = mockMediaList.find { it.id == id }
    override fun getChannelById(id: String): Channel? = mockChannels.find { it.id == id }

    override fun searchMedia(query: String, filterGenre: String?, filterType: MediaType?): List<MediaItem> {
        val q = query.trim().lowercase()
        return mockMediaList.filter { item ->
            val matchesQuery = q.isEmpty() ||
                    item.title.lowercase().contains(q) ||
                    item.overview.lowercase().contains(q) ||
                    item.genres.any { it.lowercase().contains(q) } ||
                    item.cast.any { it.lowercase().contains(q) }

            val matchesGenre = filterGenre == null || filterGenre == "All" || item.genres.any { it.equals(filterGenre, ignoreCase = true) }
            val matchesType = filterType == null || item.type == filterType

            matchesQuery && matchesGenre && matchesType
        }
    }

    override fun getMovieGenres(): List<String> = listOf("All", "Action", "Sci-Fi", "Adventure", "Fantasy", "Mystery", "Crime", "Drama")
    override fun getSeriesGenres(): List<String> = listOf("All", "Sci-Fi", "Action", "Cyberpunk", "Drama", "Mystery", "Fantasy", "History")
    override fun getChannelCategories(): List<String> = listOf("All", "Cinema", "Sports", "News", "Documentary", "Music", "Action")
}
