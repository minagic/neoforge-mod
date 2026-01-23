package com.minagic.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogger {
    public static final Logger GENERAL = LogManager.getLogger("Minagic");
    public static final Logger SIMULACRUM = LogManager.getLogger("Minagic-Simulacrum");
    public static final Logger SPELLCAST = LogManager.getLogger("Minagic-Spellcast");
}