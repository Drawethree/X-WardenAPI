package dev.drawethree.xwarden.api.integrity;

import java.util.Map;

/**
 * What a sweep found, without the items it found it in.
 *
 * <p>A sweep walks every inventory on the server and the detail behind it is one entry per tracked
 * item - megabytes of it on a busy prison. This is the part a list or a headline needs; ask for the
 * detail only on the one screen that shows where each copy sits.
 *
 * @param scanId         identifies this sweep, and is what a sighting is recorded against
 * @param finishedAt     when it ended, as epoch milliseconds
 * @param trackedItems   how many items carried an identity at all
 * @param duplicateCount how many identities were found in more than one place
 * @param duplicateCopies how many places those identities were found in altogether
 * @param countsByClass  how many tracked items of each class were seen
 */
public record ScanSummary(long scanId,
                          long finishedAt,
                          int trackedItems,
                          int duplicateCount,
                          int duplicateCopies,
                          Map<String, Long> countsByClass) {

    public boolean foundAnything() {
        return this.duplicateCount > 0;
    }
}
