package org.evd.game.common;

import java.io.File;
import java.nio.file.Paths;

public class ConstPath {
    public final static String ROOT_PATH = Paths.get(".").toAbsolutePath().toString() + File.separator;
    public final static String CONFIGURATION_PATH = ROOT_PATH + "config" + File.separator;
}
