package hellosbt.config;

/**
 * Supported Spring profile names as constants, for convenience.
 */

public interface Spring {

  interface Profiles {

    String DEFAULT = "default";
    String ONETIME_ON_STARTUP = "onetime-on-startup";
    String FILE_BASED = "file-based";
    String TEST = "test";
  }
}
