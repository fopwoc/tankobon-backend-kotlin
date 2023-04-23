<p align="center">
  <img src="https://raw.githubusercontent.com/fopwoc/tankobon-backend-kotlin/master/.github/assets/logo.svg" height="128">
  <h1 align="center">Tankōbon-backend-kotlin</h1>
</p>

<p align="center">
  <a href="https://github.com/fopwoc/tankobon-backend-kotlin/actions/workflows/ci.yaml">
    <img src="https://github.com/fopwoc/tankobon-backend-kotlin/actions/workflows/ci.yaml/badge.svg">
  </a>
  <a href="https://detekt.dev">
    <img src="https://img.shields.io/badge/Analyzer-detekt-ae4c98">
  </a>
</p>

Backend for Tankōbon - a Flutter manga reader app. __Early prototype, WIP__.

Kotlin REST backend build on top of Ktor+Exposed. It serves your local manga library via HTTP REST also has users privilege system, manga library management system and more. Designed for [Tankōbon flutter client](https://github.com/fopwoc/tankobon-flutter) with some <3;

## Features
- Access + refresh tokens authentication using JWT
- Passwords bcrypt hashing
- Users management and basic privilege system
- Decompression of cbz/zip and cbr/rar (*even rar5*) formats
- Library hot reload on any changes in content folder
- Thumbnail generation
- In some way designed for high load and many users
- Can work on any UNIX-like os

## Build and run

To work it requires postgres database, `p7zip` and `rar` packages in your system.

1. Install jdk 17 or newer
2. Clone repo somewhere you like
3. Prepare config file with preferred settings
4. Run server with `gradle runShadow`. Also `gradle shadowJar` can build standalone jar file.

Or you can use docker-compose:
```yaml
  postgres:
    image: postgres:latest
    environment:
      PGDATA: /data/postgres
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    volumes:
      - postgres:/data/postgres
    ports:
      - "5432:5432"

  tankobon-server:
    image: ghcr.io/fopwoc/tankobon-backend-kotlin:master
    volumes:
      - /path/to/tankobon-config.yml:/opt/app/tankobon-config.yml
      - path/to/manga:/manga
      - tankobon-data:/data
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```

In any way there is docker cli way
```shell
sudo docker run -d \
    -v "path/to/tankobon-config.yml:/opt/app/tankobon-config.yml" \
    -v "tankobon-data:/data" \
    -v "path/to/manga:/manga" \
    ghcr.io/fopwoc/tankobon-backend-kotlin:master
```

## Usage
1. Copy cbz/zip, cbr/rar or folders with images to `manga` generated folder.
2. Get some patience... Thumbnail generation and image converting take some time. My M1 mac takes about 2 minutes to process 2.5GB folder.
3. Have fun!

After content preparation, the content will be available via HTTP for authenticated users only. You can closer look at API here -> [`requests.http`](https://github.com/fopwoc/tankobon-server-kotlin/blob/dev/requests.http)

Default username and password is `username` and `password` but can be changed in yaml config file. Example of config file you can find [here](https://github.com/fopwoc/tankobon-backend-kotlin/blob/master/tankobon-config.yml).

First created user always has admin privileges, which allows to create new users and disable old ones.

## TODO
- OpenAPI
- Tests, tests and tests.
- Optimizations, especially content calculation related.
- Documentation


## Reason why does this project exist

I want to self-host my manga library for myself and... iPhone has no Tachiymi lol.
