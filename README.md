<div align="center">
<img width="320" height="200" alt="CSER 283" src="https://github.com/user-attachments/assets/d0f4598f-75c9-4c16-9200-a6daccb13794" />
</div>

<h1 align="center">😇 AfterlifeExtractor 😈</h1>

A tool that dumps chunks from the `*.000` files used in [LucasArts' Afterlife](https://en.wikipedia.org/wiki/Afterlife_(video_game)).

## Format

The container is essentially an IFF file, that is:

| Size            | Type          |
|-----------------|---------------|
| byte[4]         | Chunk Name    |
| int32           | Chunk Size    |
| byte[chunkSize] | Chunk Content |

The container top tag is `JIFF`.

For the `CSER` chunk, the chunk content is:

| Size                   | Type                                                    |
|------------------------|---------------------------------------------------------|
| int32                  | Unknown, seems to always be 2                           |
| int32                  | Content Size, relative to AFTER the header + chunk size |
| int32                  | Unknown, seems to always be 0                           |
| int32                  | Unknown, seems to always be 0                           |
| byte[contentSize - 16] | Contents                                                |

The `CSER` chunk has... resources (wow), such as bitmaps, cursor, and other unknown formats.