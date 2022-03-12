package ru.itmo.sd.nebash.backend

import ru.itmo.sd.nebash.NebashException

// TODO wrap io exceptions
class BackendException(message: String) : NebashException(message)
