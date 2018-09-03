package hellosbt.config;

public interface Spring {

  interface Profiles {

    String DEFAULT = "default";
    String ONETIME_ON_STARTUP = "onetime-on-startup";
    String FILE_BASED = "file-based";
    String TEST = "test";
  }
}
