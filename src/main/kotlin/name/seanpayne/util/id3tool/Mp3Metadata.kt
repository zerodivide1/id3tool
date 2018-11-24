package name.seanpayne.util.id3tool

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
            return Id3Metadata(
                    album = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.album else mp3File.id3v1Tag?.album,
                    artist = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.artist else mp3File.id3v1Tag?.artist,
                    title = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.title else mp3File.id3v1Tag?.title,
                    track = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.track else mp3File.id3v1Tag?.track,
                    year = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.year else mp3File.id3v1Tag?.year,
                    comment = if (mp3File.hasId3v2Tag()) mp3File.id3v2Tag.comment else mp3File.id3v1Tag?.comment,
                    genre = if (mp3File.hasId3v2Tag()) if(mp3File.id3v2Tag.genre == -1) null else mp3File.id3v2Tag.genre else if(mp3File.id3v1Tag.genre == -1) null else mp3File.id3v1Tag?.genre
            )
        }
    }
}