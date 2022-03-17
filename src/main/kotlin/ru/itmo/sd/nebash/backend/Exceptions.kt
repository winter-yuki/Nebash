package ru.itmo.sd.nebash.backend

import ru.itmo.sd.nebash.NebashException
import java.io.IOException

abstract class BackendException(message: String, e: Throwable? = null) : NebashException(message, e)

class IOExecutionException(e: IOException) : BackendException(e.message.orEmpty(), e)

class FailToStartExternalProcess(e: Exception) : BackendException(e.message.orEmpty(), e)

class NonZeroExternalProcessExitCode(code: Int) : BackendException("external process exited with code $code")
