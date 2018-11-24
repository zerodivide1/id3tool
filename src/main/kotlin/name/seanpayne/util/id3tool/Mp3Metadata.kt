package name.seanpayne.util.id3tool

import com.mpatric.mp3agic.ID3Wrapper
import com.mpatric.mp3agic.Mp3File

data class Mp3FileMetadata(
        val length:Long,
        val metdata: Id3Metadata?)

fun Mp3File.jsonMetadata() = Mp3FileMetadata(
        this.lengthInMilliseconds,
        Id3Metadata.convertId3(this)
)

data class Id3Metadata(
        val album:String?,
        val artist:String?,
        val title:String?,
        val track:String?,
        val year:String?,
        val comment:String?,
        val genre:Int?) {
    companion object {
        fun convertId3(mp3File: Mp3File) : Id3Metadata? {
            val wrapper = ID3Wrapper(mp3File.id3v1Tag, mp3File.id3v2Tag)
            return Id3Metadata(
                    album = wrapper.album,
                    artist = wrapper.artist,
                    title = wrapper.title,
                    track = wrapper.track,
                    year = wrapper.year,
                    comment = wrapper.comment,
                    genre = if (wrapper.genre == -1) null else wrapper.genre,
            )
        }
    }
}