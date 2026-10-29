package dev.lumentv

import org.junit.Assert.*
import org.junit.Test

class PlaybackTest {
    private val movie = Title(550,"movie","Example")
    private val tv = Title(1399,"tv","Series")
    @Test fun movieEmbedUsesCurrentDocumentedRoute() { assertEquals("https://framextv.cc/embed/550?autoplay=1&muted=0&controls=1&start=0",Framex.embed(Playback(movie))) }
    @Test fun episodeEmbedAndResumeAreCorrect() { assertEquals("https://framextv.cc/embed/1399/2/3?autoplay=1&muted=0&controls=1&start=120&provider=archer",Framex.embed(Playback(tv,2,3,120.9,900.0),"archer")) }
    @Test fun redirectAliasesAreExplicitlyAllowlisted() { assertTrue("framextv.cc" in Framex.allowedHosts); assertTrue("framextv.xyz" in Framex.allowedHosts); assertFalse("example.com" in Framex.allowedHosts) }
    @Test fun recommendedProviderIsHighQualityDocumentedOption() { assertEquals("pekka",Framex.providers.first()); assertTrue(Framex.providerLabels.getValue("pekka").contains("1080p")) }
    @Test fun pausedReloadDoesNotAutoplay() { assertTrue(Framex.embed(Playback(movie),autoplay=false).contains("autoplay=0")) }
    @Test fun completedPlaybackDoesNotResume() { assertTrue(Framex.embed(Playback(movie,seconds=950.0,duration=1000.0)).endsWith("start=0")) }
    @Test fun mediaTypesDoNotCollide() { assertNotEquals(movie.key,Title(550,"tv","Another").key) }
    @Test fun episodesDoNotCollide() { assertNotEquals(Playback(tv,1,1).key,Playback(tv,1,2).key) }
    @Test fun invalidProgressIsRejected() { listOf(Double.NaN,Double.POSITIVE_INFINITY,-1.0,1003.0).forEach { assertFalse(ProgressPolicy.valid(it,1000.0)) }; assertFalse(ProgressPolicy.valid(10.0,0.0)) }
    @Test fun resumeThresholds() { assertFalse(ProgressPolicy.resumable(4.9,1000.0)); assertTrue(ProgressPolicy.resumable(5.0,1000.0)); assertTrue(ProgressPolicy.resumable(949.0,1000.0)); assertFalse(ProgressPolicy.resumable(950.0,1000.0)) }
    @Test fun mappedPlayerFocusStaysInsideRows() {
        assertEquals(7,PlayerSnap.bottomX.size)
        assertEquals(11,PlayerSnap.menuY.size)
        assertEquals(0,PlayerSnap.move(0,-1,PlayerSnap.bottomX.lastIndex))
        assertEquals(1,PlayerSnap.move(0,1,PlayerSnap.bottomX.lastIndex))
        assertEquals(PlayerSnap.bottomX.lastIndex,PlayerSnap.move(PlayerSnap.bottomX.lastIndex,1,PlayerSnap.bottomX.lastIndex))
        assertEquals(PlayerSnap.menuY.lastIndex,PlayerSnap.move(PlayerSnap.menuY.lastIndex,1,PlayerSnap.menuY.lastIndex))
    }
    @Test(expected=IllegalArgumentException::class) fun invalidProviderCannotEnterUrl() { Framex.embed(Playback(movie),"x&evil=1") }
    @Test(expected=IllegalArgumentException::class) fun zeroEpisodeIsRejected() { Playback(tv,1,0) }
    @Test(expected=IllegalArgumentException::class) fun invalidTitleTypeRejected() { Title(1,"person","Name") }
}
