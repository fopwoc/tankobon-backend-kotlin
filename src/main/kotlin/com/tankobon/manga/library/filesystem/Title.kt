package com.tankobon.manga.library.filesystem

import com.tankobon.manga.library.ProcessingType
import com.tankobon.manga.library.fileProcessing
import com.tankobon.utils.logger
import java.io.File

fun title(file: File) {
    val log = logger("fs-title")

    var newFile = file

    if (newFile.isFile) {
        log.debug("${newFile.name} is file")
        val archiveFile = fileProcessing(newFile, type = ProcessingType.ARCHIVE, increaseHierarchy = true)
        if (archiveFile != null) newFile = archiveFile
    }

    if (newFile.isDirectory) {
        log.debug("${newFile.name} is dir")
        log.trace("${newFile.listFiles()?.map {it.name}}")

        newFile.listFiles()?.map {
            if (it.isDirectory) volume(it)
        }
    }



    log.debug("work for $file ends")
}


export FILTER_BRANCH_SQUELCH_WARNING=1 && git config alias.change-commits '!'"f() { VAR=\$1; OLD=\$2; NEW=\$3; shift 3; git filter-branch --env-filter \"if [[ \\\"\$\`echo \$VAR\`\\\" = '\$OLD' ]]; then export \$VAR='\$NEW'; fi\" \$@; }; f " && git change-commits GIT_COMMITTER_EMAIL aspirin@govno.tech ilya.dobryakov@icloud.com -f && git change-commits GIT_AUTHOR_EMAIL aspirin@govno.tech ilya.dobryakov@icloud.com -f
