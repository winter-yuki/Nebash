package ru.itmo.sd.nebash.backend

import ru.itmo.sd.nebash.NebashException

// TODO wrap io exceptions
abstract class BackendException(message: String) : NebashException(message)


