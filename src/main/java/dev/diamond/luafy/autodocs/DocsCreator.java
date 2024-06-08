package dev.diamond.luafy.autodocs;

public interface DocsCreator {
    byte[] getBytesToWriteToFile();
    String getFilenameToUse();
}
