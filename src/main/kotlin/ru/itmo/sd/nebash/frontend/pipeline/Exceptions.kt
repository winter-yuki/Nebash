package ru.itmo.sd.nebash.frontend.pipeline

import ru.itmo.sd.nebash.frontend.FrontendException

class EmptyPipelineAtomException : FrontendException("command between pipes can not be empty")

class ClosingQuoteExpected(quote: Char) : FrontendException("closing quote $quote missed")
