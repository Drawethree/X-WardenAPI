package dev.drawethree.xwarden.api.config;

/**
 * What happened to one attempted config write.
 *
 * <p>The writer edits the single line the setting lives on and leaves every other byte of the file
 * alone, because roughly six hundred of {@code warden.yml}'s thousand lines are comments explaining
 * what a false positive on each check looks like. Anything it cannot locate unambiguously it
 * refuses rather than guesses at, and a refusal says which of those it was.
 *
 * @param written   what was asked for
 * @param effective what the setting reads as now that the file has been re-read, which is not
 *                  always what was written - see {@link Reason#SHADOWED_BY_PRESET}
 */
public record WriteResult(boolean ok,
                          Reason reason,
                          String path,
                          String written,
                          String effective,
                          String detail) {

    public enum Reason {
        OK,
        /** No line in the file holds that path. */
        NOT_FOUND,
        /** More than one does, and rewriting the wrong one would report success and change nothing. */
        AMBIGUOUS,
        /** The key opens a nested block rather than holding a value. */
        NOT_A_SCALAR,
        /** The value is a {@code |} or {@code >} block. */
        BLOCK_SCALAR,
        /** The value is an anchor, an alias or a tag. */
        ANCHOR_OR_TAG,
        /** The value is an inline {@code [ ]} or <code>{ }</code> collection. */
        FLOW_COLLECTION,
        /** The value is an item in a list, which has no path of its own to address. */
        LIST_ITEM,
        /** The file indents with tabs somewhere, which YAML does not allow and nothing can walk. */
        TAB_INDENTED,
        /** The file could not be read. */
        UNREADABLE,
        /** The edited text did not parse, or did not hold the intended value. Nothing was written. */
        VERIFY_FAILED,
        /** The file could not be written. */
        IO_ERROR,
        /** The write would be ignored: this value defers to the preset. See {@link ConfigNode}. */
        SHADOWED_BY_PRESET
    }

    public static WriteResult ok(String path, String written, String effective) {
        return new WriteResult(true, Reason.OK, path, written, effective, "");
    }

    public static WriteResult refused(Reason reason, String path, String detail) {
        return new WriteResult(false, reason, path, null, null, detail);
    }
}
