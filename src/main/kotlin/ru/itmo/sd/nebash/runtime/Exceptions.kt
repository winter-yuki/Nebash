package ru.itmo.sd.nebash.runtime

import ru.itmo.sd.nebash.NebashException
import java.io.IOException

abstract class NebashRuntimeException(message: String, e: Throwable? = null) : NebashException(message, e)

class IOExecutionException(e: IOException) : NebashRuntimeException(e.message.orEmpty(), e)

class FailToStartExternalProcess(e: Exception) : NebashRuntimeException(e.message.orEmpty(), e)

class NonZeroExternalProcessExitCode(code: Int) : NebashRuntimeException("external process exited with code $code")
