Tankobon-server-kotlin
======================

Server for Tankōbon - a flutter manga reader app. WIP.

### Summary info
Kotlin rest api server build on top of Kotr. It serve your local manga library and use Tankōbon as client.

## Features

- Authentication
- Uncompression of cbz/zip and cbr/rar formats
- Library hot reload
- Thumbnail generation

## Usage
1. Run server `gradle run`. (also `gradle build` can build standalone jar file)
3. Copy cbz/zip or cbr/rar manga to `manga` generated folder
4. Wait... (thumb generation actually takes much time)

After library procession all content will be available via http. To see what http request are can be used - see `requests.http` file.
