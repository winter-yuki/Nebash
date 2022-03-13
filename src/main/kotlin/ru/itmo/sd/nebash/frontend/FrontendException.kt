package ru.itmo.sd.nebash.frontend

import ru.itmo.sd.nebash.NebashException

abstract class FrontendException(message: String) : NebashException(message)

class EmptyPipelineAtomException : FrontendException("command between pipes can not be empty")

class ClosingQuoteExpected(quote: Char) : FrontendException("closing quote $quote missed")
