<p align="center">
  <img src="https://raw.githubusercontent.com/AcetylsalicylicAcid/tankobon-backend-kotlin/master/.github/assets/logo.svg" height="128">
  <h1 align="center">Tankōbon-backend-kotlin</h1>
</p>

<p align="center">
  <a href="https://github.com/AcetylsalicylicAcid/tankobon-backend-kotlin/actions/workflows/docker-publish.yml">
    <img src="https://github.com/AcetylsalicylicAcid/tankobon-backend-kotlin/actions/workflows/docker-publish.yml/badge.svg">
  </a>
  <a href="https://detekt.dev">
    <img src="https://img.shields.io/badge/Analyzer-detekt-ae4c98">
  </a>
</p>

Backend for Tankōbon - a Flutter manga reader app. __Early prototype, WIP__.

Kotlin REST backend build on top of Ktor+Exposed+Hikari. It serves your local manga library via HTTP, also has users privilege system, manga library management system and some more. Designed for [Tankōbon flutter client](https://github.com/AcetylsalicylicAcid/tankobon-flutter) with some <3;

## Features
- Access + refresh tokens authentication using JWT wth RSA256 signing algorithm
- Passwords bcrypt hashing
- Users management and basic privilege system
- Decompression of cbz/zip and cbr/rar (*no rar5 support*) formats
- Converting all images for one specific format (jpeg)
- Library hot reload on any changes in `manga` folder
- Auto soring all images and folders for specific structure in `manga` folder
- Thumbnail generation
- Some kind of multithreading while library processing
- Possibly designed for high load and many users usage
- Multiplatform. Designed without non-java dependencies, can run anywhere

## Build and run
1. Install jdk 11 or newer
2. Clone repo somewhere you like
3. Run server with `gradle runShadow`. Also `gradle shadowJar` can build standalone jar file.

Or you can use docker-compose:
```yaml
   tankobon-server:
      image: acetylsalicylicacid/tankobon-backend-kotlin:master
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
    acetylsalicylicacid/tankobon-backend-kotlin:master
```

## Usage
1. Copy cbz/zip, cbr/rar (be careful with rar5 archives, they don't support) or folders with images to `manga` generated folder.
2. Get some patience... Thumbnail generation and image converting takes some time. My ryzen 5 1600 takes about 300 seconds to process 2GB title folder.
3. Have fun!

After library procession, all content will be available via http only for authenticated users. To see what http request are can be used - see [`requests.http`](https://github.com/ASPIRINmoe/tankobon-server-kotlin/blob/dev/requests.http) file.

Default username and password is `username` `password`, but can be changed with `tkbn_username` and `tkbn_password` environment variables. First created user always has admin privileges, which allows to create new users and disable old ones. Also you can change default port from `8080` with `tkbn_port` variable.

## TODO
- optimisation of file processing (it works so slow on my opinion)
- maybe use WebP for images.
- tests, tests and tests.
- documentaion... ughhhh...

## Reason why does this project exist

Since I'm kinda wierd combo of FOSS enthusiast and maniac Apple ecosystem user, I want to read manga using my phone, and I don't have any kind of cool open source manga readers like [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi), so... one beautiful day I decided to build for myself some sort of Plex-like thing but for manga. This project is a challenge to myself to create good-looking from code and ui perspective, well working and maintainable client-server open source application and will provide me able to read manga like I want. Feel free to fork and do whatever you want with this code. As I said before in usage section - have fun!