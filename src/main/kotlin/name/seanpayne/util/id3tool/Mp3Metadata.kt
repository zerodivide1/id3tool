package name.seanpayne.util.id3tool

import com.mpatric.mp3agic.Mp3File

data class Mp3FileMetadata(
        val length:Long,
        val id3v1: Id3V1Metadata?)

fun Mp3File.jsonMetadata() = Mp3FileMetadata(
        this.lengthInMilliseconds,
        Id3V1Metadata.convertId3V1(this)
)

data class Id3V1Metadata(
        val album:String?,
        val artist:String?,
        val title:String?,
        val track:String?,
        val year:String?,
        val comment:String?,
        val genre:Int?,
        val genreDescription: String?) {
    companion object {
        fun convertId3V1(mp3File: Mp3File) : Id3V1Metadata? {
            return if (mp3File.hasId3v1Tag()) {
                Id3V1Metadata(
                        mp3File.id3v1Tag?.album,
                        mp3File.id3v1Tag?.artist,
                        mp3File.id3v1Tag?.title,
                        mp3File.id3v1Tag?.track,
                        mp3File.id3v1Tag?.year,
                        mp3File.id3v1Tag?.comment,
                        if (mp3File.id3v1Tag?.genre == -1) null else mp3File.id3v1Tag?.genre,
                        if (mp3File.id3v1Tag?.genre == -1) null else mp3File.id3v1Tag?.genreDescription
                )
            } else {
                null
            }
        }
    }
}