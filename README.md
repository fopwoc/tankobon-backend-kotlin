Tankobon-server-kotlin
======================

Server for Tankōbon - a flutter manga reader app. WIP.

### Summary info
Kotlin REST API server build on top of Kotr+Exposed+Hikari. It serves your local manga library via Http, also has users privilege system, manga library management system and some more. Designed for [Tankōbon flutter client](https://github.com/ASPIRINmoe/tankobon-flutter).

## Features
- Authentication (JWT)
- Passwords bcrypt hashing
- Users management and basic privilege system
- Decompression of cbz/zip and cbr/rar (*no rar5 support*) formats
- Converting all images for one specific format
- Library hot reload on any changes in `manga` folder
- Auto soring all images and folders for specific structure in `manga` folder
- Thumbnail generation
- Some kind of multithreading while library processing
- Possibly designed for high load and many users usage
- Multiplatform. Designed without non-java dependencies, can run anywhere

## Build and run
2. Install java 9 or newer
3. Clone repo somewhere you like
4. Run server with `gradle run`. Also `gradle build` can build standalone jar file.

Or you can use docker-compose:
```yaml
   tankobon-server:
      image: aspirinmoe/tankobon-server-kotlin:dev
      volumes:
        - path/to/manga:/manga
        - tankobon-data:/data
      environment:
        - tkbn_username='username' //optional
        - tkbn_password='password' //optional
        - tkbn_address='0.0.0.0' //optional
        - tkbn_port='8080' //optional
      ports:
        - "8080:8080"
```

In any way there is docker cli way
```shell
sudo docker run -d \
    -e tkbn_username='username' \
    -e tkbn_password='password' \
    -v "tankobon-data:/data" \
    -v "path/to/manga:/manga" \
    aspirinmoe/tankobon-server-kotlin:dev
```

## Usage
1. Copy cbz/zip, cbr/rar or folders with images to `manga` generated folder.
2. Wait a little bit... (thumbnail generation and image converting takes some time).
3. Have fun!

After library procession, all content will be available via http only for authenticated users. To see what http request are can be used - see [`requests.http`](https://github.com/ASPIRINmoe/tankobon-server-kotlin/blob/dev/requests.http) file.

Default username and password is `username` `password`, but can be changed with `tkbn_username` and `tkbn_password` environment variables. First created user always has admin privileges, which allows to create new users and disable old ones. Also you can change default port from `8080` with `tkbn_port` variable.
