package org.springframework.security.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringVersion;

/**
 * Internal class used for checking version compatibility in a deployed application.
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class SpringSecurityCoreVersion {
    private static final String DISABLE_CHECKS = SpringSecurityCoreVersion.class.getName().concat(".DISABLE_CHECKS");

    private static final Log logger = LogFactory.getLog(SpringSecurityCoreVersion.class);

    /**
     * Global Serialization value for Spring Security classes.
     *
     * N.B. Classes are not intended to be serializable between different versions.
     * See SEC-1709 for why we still need a serial version.
     */
    public static final long SERIAL_VERSION_UID = 400L;

    static final String MIN_SPRING_VERSION = "4.0.3.DEPENDENCIES";

    static {
        performVersionChecks();
    }

    public static String getVersion() {
        Package pkg = SpringSecurityCoreVersion.class.getPackage();
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }

    /**
     * Performs version checks
     */
    private static void performVersionChecks() {
        // Check Spring Compatibility
        String springVersion = SpringVersion.getVersion();
        String version = getVersion();

        if(disableChecks(springVersion, version)) {
            return;
        }

        logger.info("You are running with Spring Security Core " + version);
        if (springVersion.compareTo(MIN_SPRING_VERSION) < 0) {
            logger.warn("**** You are advised to use Spring " + MIN_SPRING_VERSION +
                    " or later with this version. You are running: " + springVersion);
        }
    }

    /**
     * Disable if springVersion and springSecurityVersion are the same to allow
     * working with Uber Jars.
     *
     * @param springVersion
     * @param springSecurityVersion
     * @return
     */
    private static boolean disableChecks(String springVersion, String springSecurityVersion) {
        if(springVersion == null || springVersion.equals(springSecurityVersion)) {
            return true;
        }
        return Boolean.getBoolean(DISABLE_CHECKS);
    }
}
