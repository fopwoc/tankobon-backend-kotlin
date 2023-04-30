package com.tankobon.domain.models

interface RoutePath {
    val path: String
}
enum class BaseRoute(override val path: String) : RoutePath {
    AUTH("/auth"),
    INSTANCE("/about"),
    MANGA("/content"),
    USER("/user"),
}

private enum class BaseAuthRoute(override val path: String) : RoutePath {
    SESSIONS("/sessions"),
}

enum class AuthRoute(override val path: String) : RoutePath {
    LOGIN("${BaseRoute.AUTH.path}/login"),
    SESSIONS("${BaseRoute.AUTH.path}${BaseAuthRoute.SESSIONS.path}"),
    SESSIONS_ALL("${BaseRoute.AUTH.path}${BaseAuthRoute.SESSIONS.path}/all"),
    DELETE("${BaseRoute.AUTH.path}${BaseAuthRoute.SESSIONS.path}/delete"),
    CLEANUP("${BaseRoute.AUTH.path}${BaseAuthRoute.SESSIONS.path}/cleanup"),
    LOGOUT("${BaseRoute.AUTH.path}/logout")
}

private enum class BaseMangaRoute(override val path: String) : RoutePath {
    MANGA("/manga"),
    THUMB("/thumb"),
    TITLE("/{${MangaRouteType.ID_TITLE}}"),
    VOLUME("/{${MangaRouteType.ID_TITLE}}/{${MangaRouteType.ID_VOLUME}}"),
    PAGE("/{${MangaRouteType.ID_TITLE}}/{${MangaRouteType.ID_VOLUME}}/{${MangaRouteType.ID_PAGE}}"),
}
enum class MangaRoute(override val path: String) : RoutePath {
    MANGA("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}"),
    MANGA_TITLE("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}${BaseMangaRoute.TITLE.path}"),
    MANGA_TITLE_UPDATE("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}${BaseMangaRoute.TITLE.path}/update"),
    MANGA_VOLUME_UPDATE("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}${BaseMangaRoute.VOLUME.path}/update"),
    MANGA_PAGE("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}${BaseMangaRoute.PAGE.path}"),
    THUMB_PAGE("${BaseRoute.MANGA.path}${BaseMangaRoute.THUMB.path}${BaseMangaRoute.PAGE.path}"),
    LAST_READ("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}${BaseMangaRoute.TITLE.path}/last_read"),
    RELOAD_LIBRARY("${BaseRoute.MANGA.path}${BaseMangaRoute.MANGA.path}/reload_library"),
}

enum class UserRoute(override val path: String) : RoutePath {
    ME("${BaseRoute.USER.path}/me"),
    ALL("${BaseRoute.USER.path}/all"),
    CREATE("${BaseRoute.USER.path}/create"),
    EDIT("${BaseRoute.USER.path}/edit"),
    DELETE("${BaseRoute.USER.path}/delete"),
}

enum class InstanceRoute(override val path: String) : RoutePath {
    ABOUT(BaseRoute.INSTANCE.path),
    ABOUT_UPDATE("${BaseRoute.INSTANCE.path}/update"),
}
